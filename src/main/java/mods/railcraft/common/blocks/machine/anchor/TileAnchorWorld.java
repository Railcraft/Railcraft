/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.anchor;

import com.google.common.collect.MapMaker;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.carts.ItemCartAnchor;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.collections.ItemMap;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.ChunkManager;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.IAnchor;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileAnchorWorld extends TileMachineItem implements IAnchor, ISidedInventory {
    public static final PropertyBool DISABLED = PropertyBool.create("disabled");
    private static final Map<UUID, Ticket> tickets = new MapMaker().makeMap();
    private static final Map<EntityPlayer, WorldCoordinate> sentinelPairingMap = new MapMaker().weakKeys().makeMap();
    private static final int SENTINEL_CHECK = 128;
    private static final byte MAX_CHUNKS = 25;
    private static final byte FUEL_CYCLE = 9;
    private static final byte ANCHOR_RADIUS = 1;
    private static final int[] SLOTS = {0};
    private static final int[] SLOTS_NO_ACCESS = {};
    private BlockPos sentinelPos = BlockPos.ORIGIN;
    private int prevX, prevY, prevZ;
    private Set<ChunkPos> chunks;
    private long fuel;
    private int fuelCycle;
    private boolean hasTicket;
    private boolean refreshTicket;
    private boolean powered;

    public TileAnchorWorld() {
        super(1);
    }

    @Override
    public int getSizeInventory() {
        return needsFuel() ? 1 : 0;
    }

    @Override
    public AnchorVariant getMachineType() {
        return AnchorVariant.WORLD;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (heldItem != null && heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, hand, heldItem, getPos())) {
                if (Game.isHost(worldObj)) {
                    WorldCoordinate ourCoord = new WorldCoordinate(this);
                    WorldCoordinate target = sentinelPairingMap.get(player);
                    if (target == null)
                        setTarget(ourCoord, player, getLocalizationTag());
                    else if (worldObj.provider.getDimension() != target.getDim())
                        ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.anchor.pair.fail.dimension", getLocalizationTag());
                    else if (Objects.equals(ourCoord, target)) {
                        removeTarget(player);
                        ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.anchor.pair.cancel", getLocalizationTag());
                    } else
                        setSentinel(player, target);
                    crowbar.onWhack(player, hand, heldItem, getPos());
                }
                return true;
            }
        }
        return super.blockActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Nullable
    public static WorldCoordinate getTarget(EntityPlayer player) {
        return sentinelPairingMap.get(player);
    }

    public static void setTarget(WorldCoordinate pos, EntityPlayer player, String locTag) {
        sentinelPairingMap.put(player, pos);
        ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.anchor.pair.start", locTag);
    }

    public static void removeTarget(EntityPlayer player) {
        sentinelPairingMap.remove(player);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        if (needsFuel()) {
            GuiHandler.openGui(EnumGui.WORLD_ANCHOR, player, worldObj, getPos());
            return true;
        }
        return false;
    }

    public int getMaxSentinelChunks() {
        Ticket ticket = getTicket();
        if (ticket == null)
            return MAX_CHUNKS;
        return Math.min(ticket.getMaxChunkListDepth(), MAX_CHUNKS);
    }

    @Nullable
    public static boolean isTargetLoaded(EntityPlayer player, WorldCoordinate coord, String locTag) {
        if (!WorldPlugin.isBlockLoaded(player.worldObj, coord.getPos())) {
            ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.anchor.pair.fail.unloaded", locTag);
            return false;
        }
        return true;
    }

    public boolean setSentinel(EntityPlayer player, WorldCoordinate coord) {
        if (!isTargetLoaded(player, coord, getLocalizationTag())) {
            return false;
        }
        IBlockState state = WorldPlugin.getBlockState(worldObj, coord.getPos());
        if (RailcraftBlocks.ANCHOR_SENTINEL.isEqual(state)) {
            int xChunk = getPos().getX() >> 4;
            int zChunk = getPos().getZ() >> 4;

            int xSentinelChunk = coord.getX() >> 4;
            int zSentinelChunk = coord.getZ() >> 4;

            if (xChunk != xSentinelChunk && zChunk != zSentinelChunk) {
                ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.anchor.pair.fail.alignment", getLocalizationTag(), state.getBlock().getUnlocalizedName());
                return false;
            }

            int max = getMaxSentinelChunks();
            if (Math.abs(xChunk - xSentinelChunk) >= max || Math.abs(zChunk - zSentinelChunk) >= max) {
                ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.anchor.pair.fail.distance", getLocalizationTag(), state.getBlock().getUnlocalizedName());
                return false;
            }

            sentinelPos = coord.getPos();

            requestTicket();
            sendUpdateToClient();
            removeTarget(player);
            ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.anchor.pair.success", getLocalizationTag());
            return true;
        }
        ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.anchor.pair.fail.invalid", getLocalizationTag());
        return false;
    }

    public void clearSentinel() {
        if (!hasSentinel())
            return;

        sentinelPos = BlockPos.ORIGIN;

        requestTicket();
        sendUpdateToClient();
    }

    public boolean hasSentinel() {
        return !sentinelPos.equals(BlockPos.ORIGIN);
    }

    public boolean hasFuel() {
        return fuel > 0;
    }

    @Override
    public List<ItemStack> getDrops(int fortune) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        ItemStack drop = getMachineType().getStack();
        if (needsFuel() && hasFuel()) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setLong("fuel", fuel);
            drop.setTagCompound(nbt);
        }
        items.add(drop);
        return items;
    }

    @Override
    public void initFromItem(ItemStack stack) {
        super.initFromItem(stack);
        if (needsFuel())
            fuel = ItemCartAnchor.getFuel(stack);
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(worldObj)) {
            if (chunks != null)
                EffectManager.instance.chunkLoaderEffect(worldObj, this, chunks);
            return;
        }

        if (RailcraftConfig.deleteAnchors()) {
            releaseTicket();
            worldObj.setBlockState(getPos(), Blocks.OBSIDIAN.getDefaultState());
            return;
        }

        if (getX() != prevX || getY() != prevY || getZ() != prevZ) {
            releaseTicket();
            prevX = getX();
            prevY = getY();
            prevZ = getZ();
        }

        if (isTicketInvalid())
            releaseTicket();

        if (needsFuel()) {
            fuelCycle++;
            if (fuelCycle >= FUEL_CYCLE) {
                fuelCycle = 0;
                if (chunks != null && hasActiveTicket() && fuel > 0)
                    fuel -= chunks.size();
                if (fuel <= 0) {
                    ItemStack stack = getStackInSlot(0);
                    if (InvTools.isEmpty(stack)) {
                        setInventorySlotContents(0, InvTools.emptyStack());
                        releaseTicket();
                    } else if (getFuelMap().containsKey(stack)) {
                        decrStackSize(0, 1);
                        fuel = (long) (getFuelMap().get(stack) * RailcraftConstants.TICKS_PER_HOUR);
                    }
                }
            }
        }

        if (clock % SENTINEL_CHECK == 0 && hasSentinel()) {
            IBlockState sentinel = worldObj.getBlockState(sentinelPos);
            if (!RailcraftBlocks.ANCHOR_SENTINEL.isEqual(sentinel))
                clearSentinel();
        }

        if (!hasActiveTicket())
            requestTicket();

        if (RailcraftConfig.printAnchorDebug() && hasActiveTicket())
            if (clock % 64 == 0) {
                int numChunks = chunks == null ? 0 : chunks.size();
                ChatPlugin.sendLocalizedChatToAllFromServer(worldObj, "%s has loaded %d chunks and is ticking at <%d> in dim:%d - logged on tick %d", getName(), numChunks, getPos(), worldObj.provider.getDimension(), worldObj.getWorldTime());
                Game.log(Level.DEBUG, "{0} has loaded {1} chunks and is ticking at <{2}> in dim:{3} - logged on tick {4}", getName(), numChunks, getPos(), worldObj.provider.getDimension(), worldObj.getWorldTime());
            }
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        releaseTicket();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        refreshTicket = true;
    }

    @Override
    public void validate() {
        super.validate();
        refreshTicket = true;
    }

    protected void releaseTicket() {
        refreshTicket = false;
        setTicket(null);
    }

    protected void requestTicket() {
        if (meetsTicketRequirements()) {
            Ticket chunkTicket = getTicketFromForge();
            if (chunkTicket != null) {
                setTicketData(chunkTicket);
                forceChunkLoading(chunkTicket);
            }
        }
    }

    public boolean needsFuel() {
        return !getFuelMap().isEmpty();
    }

    @Override
    public ItemMap<Float> getFuelMap() {
        return RailcraftConfig.anchorFuelWorld;
    }

    protected boolean meetsTicketRequirements() {
        return !powered && (hasFuel() || !needsFuel());
    }

    @Nullable
    protected Ticket getTicketFromForge() {
        return ForgeChunkManager.requestTicket(Railcraft.getMod(), worldObj, Type.NORMAL);
    }

    protected void setTicketData(Ticket chunkTicket) {
        chunkTicket.getModData().setInteger("xCoord", getPos().getX());
        chunkTicket.getModData().setInteger("yCoord", getPos().getY());
        chunkTicket.getModData().setInteger("zCoord", getPos().getZ());
        chunkTicket.getModData().setString("type", getMachineType().getTag());
    }

    public boolean isTicketInvalid() {
        Ticket ticket = getTicket();
        return ticket != null && (ticket.world != worldObj || refreshTicket || powered);
    }

    public boolean hasActiveTicket() {
        if (Game.isClient(worldObj))
            return hasTicket;
        return getTicket() != null;
    }

    @Nullable
    public Ticket getTicket() {
        return tickets.get(getUUID());
    }

    public void setTicket(@Nullable Ticket t) {
        boolean changed = false;
        Ticket ticket = getTicket();
        if (ticket != t) {
            if (ticket != null) {
                if (ticket.world == worldObj) {
                    for (ChunkPos chunk : ticket.getChunkList()) {
                        if (ForgeChunkManager.getPersistentChunksFor(worldObj).keys().contains(chunk))
                            ForgeChunkManager.unforceChunk(ticket, chunk);
                    }
                    ForgeChunkManager.releaseTicket(ticket);
                }
                tickets.remove(getUUID());
            }
            changed = true;
        }
        hasTicket = t != null;
        if (hasTicket)
            tickets.put(getUUID(), t);
        if (changed)
            sendUpdateToClient();
    }

    public void forceChunkLoading(Ticket ticket) {
        setTicket(ticket);

        setupChunks();

        if (chunks != null)
            for (ChunkPos chunk : chunks) {
                ForgeChunkManager.forceChunk(ticket, chunk);
            }
    }

    public void setupChunks() {
        if (!hasTicket)
            chunks = null;
        else if (hasSentinel())
            chunks = ChunkManager.getInstance().getChunksBetween(getX() >> 4, getZ() >> 4, sentinelPos.getX() >> 4, sentinelPos.getZ() >> 4, getMaxSentinelChunks());
        else
            chunks = ChunkManager.getInstance().getChunksAround(getX() >> 4, getZ() >> 4, ANCHOR_RADIUS);
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean power) {
        powered = power;
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        if (Game.isClient(getWorld()))
            return;
        boolean newPower = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
        if (powered != newPower)
            powered = newPower;
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(hasTicket);

        data.writeBlockPos(sentinelPos);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        boolean tick = data.readBoolean();
        if (hasTicket != tick) {
            hasTicket = tick;
            markBlockForUpdate();
        }

        sentinelPos = data.readBlockPos();

        setupChunks();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setLong("fuel", fuel);

        data.setBoolean("powered", powered);

        NBTPlugin.writeBlockPos(data, "sentinel", sentinelPos);

        data.setInteger("prevX", prevX);
        data.setInteger("prevY", prevY);
        data.setInteger("prevZ", prevZ);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        if (needsFuel())
            fuel = data.getLong("fuel");

        powered = data.getBoolean("powered");

        sentinelPos = NBTPlugin.readBlockPos(data, "sentinel");

        prevX = data.getInteger("prevX");
        prevY = data.getInteger("prevY");
        prevZ = data.getInteger("prevZ");
    }

    @Override
    public long getAnchorFuel() {
        return fuel;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (RailcraftConfig.anchorsCanInteractWithPipes())
            return SLOTS;
        return SLOTS_NO_ACCESS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return RailcraftConfig.anchorsCanInteractWithPipes();
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }
}
