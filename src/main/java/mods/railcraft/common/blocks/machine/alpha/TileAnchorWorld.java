/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import com.google.common.collect.MapMaker;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.blocks.machine.beta.TileSentinel;
import mods.railcraft.common.carts.ItemCartAnchor;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.collections.ItemMap;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.ChunkManager;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.IAnchor;
import mods.railcraft.common.util.network.RailcraftDataInputStream;
import mods.railcraft.common.util.network.RailcraftDataOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileAnchorWorld extends TileMachineItem implements IAnchor, ISidedInventory {

    private static final Map<UUID, Ticket> tickets = new MapMaker().makeMap();
    private static final Map<EntityPlayer, WorldCoordinate> sentinelPairingMap = new MapMaker().weakKeys().makeMap();
    private static final int SENTINEL_CHECK = 128;
    private static final byte MAX_CHUNKS = 25;
    private static final byte FUEL_CYCLE = 9;
    private static final byte ANCHOR_RADIUS = 1;
    private static final int[] SLOTS = {0};
    private static final int[] SLOTS_NO_ACCESS = {};
    private int xSentinel = -1;
    private int ySentinel = -1;
    private int zSentinel = -1;
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
    public EnumMachineAlpha getMachineType() {
        return EnumMachineAlpha.ANCHOR_WORLD;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side) {
        if (heldItem != null && heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, heldItem, getPos())) {
                if (Game.isHost(worldObj)) {
                    WorldCoordinate target = sentinelPairingMap.get(player);
                    if (target == null)
                        setTarget(this, player);
                    else if (worldObj.provider.getDimension() != target.getDim())
                        ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.dimension", getLocalizationTag());
                    else if (new WorldCoordinate(this).equals(target)) {
                        removeTarget(player);
                        ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.cancel", getLocalizationTag());
                    } else
                        setSentinel(player, target);
                    crowbar.onWhack(player, heldItem, getPos());
                }
                return true;
            }
        }
        return super.blockActivated(player, hand, heldItem, side);
    }

    public static WorldCoordinate getTarget(EntityPlayer player) {
        return sentinelPairingMap.get(player);
    }

    public static void setTarget(RailcraftTileEntity tile, EntityPlayer player) {
        sentinelPairingMap.put(player, new WorldCoordinate(tile));
        ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.start", tile.getLocalizationTag());
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

    public static TileEntity getTargetAt(EntityPlayer player, RailcraftTileEntity searcher, WorldCoordinate coord) {
        if (!WorldPlugin.isBlockLoaded(searcher.getWorld(), coord)) {
            ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.unloaded", searcher.getLocalizationTag());
            return null;
        }
        return WorldPlugin.getBlockTile(searcher.getWorld(), coord);
    }

    public boolean setSentinel(EntityPlayer player, WorldCoordinate coord) {
        TileEntity tile = getTargetAt(player, this, coord);
        if (tile == null)
            return false;
        if (tile instanceof TileSentinel) {
            int xChunk = getPos().getX() >> 4;
            int zChunk = getPos().getZ() >> 4;

            int xSentinelChunk = tile.getPos().getX() >> 4;
            int zSentinelChunk = tile.getPos().getZ() >> 4;

            if (xChunk != xSentinelChunk && zChunk != zSentinelChunk) {
                ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.alignment", getLocalizationTag(), ((TileSentinel) tile).getLocalizationTag());
                return false;
            }

            int max = getMaxSentinelChunks();
            if (Math.abs(xChunk - xSentinelChunk) >= max || Math.abs(zChunk - zSentinelChunk) >= max) {
                ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.distance", getLocalizationTag(), ((TileSentinel) tile).getLocalizationTag());
                return false;
            }

            xSentinel = tile.getPos().getX();
            ySentinel = tile.getPos().getY();
            zSentinel = tile.getPos().getZ();

            requestTicket();
            sendUpdateToClient();
            removeTarget(player);
            ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.success", getLocalizationTag());
            return true;
        }
        ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.invalid", getLocalizationTag());
        return false;
    }

    public void clearSentinel() {
        if (!hasSentinel())
            return;

        xSentinel = -1;
        ySentinel = -1;
        zSentinel = -1;

        requestTicket();
        sendUpdateToClient();
    }

    public boolean hasSentinel() {
        return ySentinel != -1;
    }

    public boolean hasFuel() {
        return fuel > 0;
    }

    @Override
    public List<ItemStack> getDrops(int fortune) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        ItemStack drop = getMachineType().getItem();
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

        if (hasActiveTicket() && (getTicket().world != worldObj || refreshTicket || powered))
            releaseTicket();

        if (needsFuel()) {
            fuelCycle++;
            if (fuelCycle >= FUEL_CYCLE) {
                fuelCycle = 0;
                if (chunks != null && hasActiveTicket() && fuel > 0)
                    fuel -= chunks.size();
                if (fuel <= 0) {
                    ItemStack stack = getStackInSlot(0);
                    if (stack == null || stack.stackSize <= 0) {
                        setInventorySlotContents(0, null);
                        releaseTicket();
                    } else if (getFuelMap().containsKey(stack)) {
                        decrStackSize(0, 1);
                        fuel = (long) (getFuelMap().get(stack) * RailcraftConstants.TICKS_PER_HOUR);
                    }
                }
            }
        }

        if (clock % SENTINEL_CHECK == 0 && hasSentinel()) {
            TileEntity tile = worldObj.getTileEntity(new BlockPos(xSentinel, ySentinel, zSentinel));
            if (!(tile instanceof TileSentinel))
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

    protected Ticket getTicketFromForge() {
        return ForgeChunkManager.requestTicket(Railcraft.getMod(), worldObj, Type.NORMAL);
    }

    protected void setTicketData(Ticket chunkTicket) {
        chunkTicket.getModData().setInteger("xCoord", getPos().getX());
        chunkTicket.getModData().setInteger("yCoord", getPos().getY());
        chunkTicket.getModData().setInteger("zCoord", getPos().getZ());
        chunkTicket.getModData().setString("type", getMachineType().getTag());
    }

    public boolean hasActiveTicket() {
        return getTicket() != null;
    }

    public Ticket getTicket() {
        return tickets.get(getUUID());
    }

    public void setTicket(Ticket t) {
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
            chunks = ChunkManager.getInstance().getChunksBetween(getX() >> 4, getZ() >> 4, xSentinel >> 4, zSentinel >> 4, getMaxSentinelChunks());
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
    public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull Block block) {
        super.onNeighborBlockChange(state, block);
        if (Game.isClient(getWorld()))
            return;
        boolean newPower = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
        if (powered != newPower)
            powered = newPower;
    }

    @Override
    public void writePacketData(@Nonnull RailcraftDataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(hasTicket);

        data.writeInt(xSentinel);
        data.writeInt(ySentinel);
        data.writeInt(zSentinel);
    }

    @Override
    public void readPacketData(@Nonnull RailcraftDataInputStream data) throws IOException {
        super.readPacketData(data);

        boolean tick = data.readBoolean();
        if (hasTicket != tick) {
            hasTicket = tick;
            markBlockForUpdate();
        }

        xSentinel = data.readInt();
        ySentinel = data.readInt();
        zSentinel = data.readInt();

        setupChunks();
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        data.setLong("fuel", fuel);

        data.setBoolean("powered", powered);

        data.setInteger("xSentinel", xSentinel);
        data.setInteger("ySentinel", ySentinel);
        data.setInteger("zSentinel", zSentinel);

        data.setInteger("prevX", prevX);
        data.setInteger("prevY", prevY);
        data.setInteger("prevZ", prevZ);
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        if (needsFuel())
            fuel = data.getLong("fuel");

        powered = data.getBoolean("powered");

        xSentinel = data.getInteger("xSentinel");
        ySentinel = data.getInteger("ySentinel");
        zSentinel = data.getInteger("zSentinel");

        prevX = data.getInteger("prevX");
        prevY = data.getInteger("prevY");
        prevZ = data.getInteger("prevZ");
    }

    @Override
    public float getResistance(Entity exploder) {
        return 60f;
    }

    @Override
    public float getHardness() {
        return 20;
    }

    @Override
    public long getAnchorFuel() {
        return fuel;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        if (RailcraftConfig.anchorsCanInteractWithPipes())
            return SLOTS;
        return SLOTS_NO_ACCESS;
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
        return RailcraftConfig.anchorsCanInteractWithPipes();
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return false;
    }
}
