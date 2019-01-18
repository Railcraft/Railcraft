/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.worldspike;

import com.google.common.collect.MapMaker;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.items.IToolCrowbar;
import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.carts.ItemCartWorldspike;
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
import mods.railcraft.common.util.misc.IWorldspike;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileWorldspike extends TileMachineItem implements IWorldspike, ISidedInventory {
    private static final Map<UUID, Ticket> tickets = new MapMaker().makeMap();
    private static final Map<EntityPlayer, WorldCoordinate> pointPairingMap = new MapMaker().weakKeys().makeMap();
    private static final int WORLDSPIKE_POINT_CHECK = 128;
    private static final byte MAX_CHUNKS = 25;
    private static final byte FUEL_CYCLE = 9;
    private static final byte CHUNK_RADIUS = 1;
    private static final int[] SLOTS = {0};
    private static final int[] SLOTS_NO_ACCESS = {};
    private BlockPos pointPos = BlockPos.ORIGIN;
    private int prevX, prevY, prevZ;
    private @Nullable Set<ChunkPos> chunks;
    private long fuel;
    private int fuelCycle;
    private boolean hasTicket;
    private boolean refreshTicket;
    private boolean powered;

    public TileWorldspike() {
        super(1);
        getInventory().setInventoryStackLimit(16);
    }

    @Override
    public int getSizeInventory() {
        return needsFuel() ? 1 : 0;
    }

    @Override
    public WorldspikeVariant getMachineType() {
        return WorldspikeVariant.STANDARD;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, hand, heldItem, getPos())) {
                if (Game.isHost(world)) {
                    WorldCoordinate ourCoord = WorldCoordinate.from(this);
                    WorldCoordinate target = pointPairingMap.get(player);
                    if (target == null) {
                        setTarget(ourCoord, player, getLocalizationTag());
                    } else {
                        if (world.provider.getDimension() != target.getDim()) {
                            ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.fail.dimension", getLocalizationTag());
                        } else if (Objects.equals(ourCoord, target)) {
                            ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.cancel", getLocalizationTag());
                        } else {
                            setPoint(player, target);
                        }
                        removeTarget(player);
                    }
                    crowbar.onWhack(player, hand, heldItem, getPos());
                }
                return true;
            }
        }
        return super.blockActivated(player, hand, side, hitX, hitY, hitZ);
    }

    public static @Nullable WorldCoordinate getTarget(EntityPlayer player) {
        return pointPairingMap.get(player);
    }

    public static void setTarget(WorldCoordinate pos, EntityPlayer player, String locTag) {
        pointPairingMap.put(player, pos);
        ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.start", locTag);
    }

    public static void removeTarget(EntityPlayer player) {
        pointPairingMap.remove(player);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        if (needsFuel()) {
            GuiHandler.openGui(EnumGui.WORLDSPIKE, player, world, getPos());
            return true;
        }
        return false;
    }

    public int getMaxPointChunks() {
        Ticket ticket = getTicket();
        if (ticket == null)
            return MAX_CHUNKS;
        return Math.min(ticket.getMaxChunkListDepth(), MAX_CHUNKS);
    }

    public static boolean isTargetLoaded(EntityPlayer player, WorldCoordinate coord, String locTag) {
        if (!WorldPlugin.isBlockLoaded(player.world, coord.getPos())) {
            ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.fail.unloaded", locTag);
            return false;
        }
        return true;
    }

    public boolean setPoint(EntityPlayer player, WorldCoordinate coord) {
        if (!isTargetLoaded(player, coord, getLocalizationTag())) {
            return false;
        }
        IBlockState state = WorldPlugin.getBlockState(world, coord.getPos());
        if (RailcraftBlocks.WORLDSPIKE_POINT.isEqual(state)) {
            int xChunk = getPos().getX() >> 4;
            int zChunk = getPos().getZ() >> 4;

            int xPointChunk = coord.getX() >> 4;
            int zPointChunk = coord.getZ() >> 4;

            if (xChunk != xPointChunk && zChunk != zPointChunk) {
                ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.fail.alignment", getLocalizationTag(), state.getBlock().getLocalizedName());
                return false;
            }

            int max = getMaxPointChunks();
            if (Math.abs(xChunk - xPointChunk) >= max || Math.abs(zChunk - zPointChunk) >= max) {
                ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.fail.distance", getLocalizationTag(), state.getBlock().getLocalizedName());
                return false;
            }

            pointPos = coord.getPos();

            requestTicket();
            sendUpdateToClient();
            removeTarget(player);
            ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.success", getLocalizationTag());
            return true;
        }
        ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.fail.invalid", getLocalizationTag());
        return false;
    }

    public void clearPoint() {
        if (!hasPoint())
            return;

        pointPos = BlockPos.ORIGIN;

        requestTicket();
        sendUpdateToClient();
    }

    public boolean hasPoint() {
        return !pointPos.equals(BlockPos.ORIGIN);
    }

    public boolean hasFuel() {
        return fuel > 0;
    }

    @Override
    public List<ItemStack> getDrops(int fortune) {
        List<ItemStack> items = new ArrayList<>();
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
            fuel = ItemCartWorldspike.getFuel(stack);
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(world)) {
            if (chunks != null)
                ClientEffects.INSTANCE.chunkLoaderEffect(world, this, chunks);
            return;
        }

        if (RailcraftConfig.deleteWorldspikes()) {
            releaseTicket();
            world.setBlockState(getPos(), Blocks.OBSIDIAN.getDefaultState());
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
                    } else {
                        Optional<Float> fuelValue = getFuelValue(stack);
                        if (fuelValue.isPresent()) {
                            fuel = (long) (fuelValue.get() * RailcraftConstants.TICKS_PER_HOUR);
                            decrStackSize(0, 1); // this operation modifies the stack variable and must be done at last
                        }
                    }
                }
            }
        }

        if (clock % WORLDSPIKE_POINT_CHECK == 0 && hasPoint()) {
            IBlockState pointState = world.getBlockState(pointPos);
            if (!RailcraftBlocks.WORLDSPIKE_POINT.isEqual(pointState))
                clearPoint();
        }

        if (!hasActiveTicket())
            requestTicket();

        if (RailcraftConfig.printWorldspikeDebug() && hasActiveTicket())
            if (clock % 64 == 0) {
                int numChunks = chunks == null ? 0 : chunks.size();
                ChatPlugin.sendLocalizedChatToAllFromServer(world, "%s has loaded %d chunks and is ticking at <%d> in dim:%d - logged on tick %d", getName(), numChunks, getPos(), world.provider.getDimension(), world.getWorldTime());
                Game.log().msg(Level.DEBUG, "{0} has loaded {1} chunks and is ticking at <{2}> in dim:{3} - logged on tick {4}", getName(), numChunks, getPos(), world.provider.getDimension(), world.getWorldTime());
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
    public final Map<Ingredient, Float> getFuelMap() {
        return getMachineType().getFuelList();
    }

    protected boolean meetsTicketRequirements() {
        return !powered && (hasFuel() || !needsFuel());
    }

    protected @Nullable Ticket getTicketFromForge() {
        return ForgeChunkManager.requestTicket(Railcraft.getMod(), world, Type.NORMAL);
    }

    protected void setTicketData(Ticket chunkTicket) {
        chunkTicket.getModData().setInteger("x", getPos().getX());
        chunkTicket.getModData().setInteger("y", getPos().getY());
        chunkTicket.getModData().setInteger("z", getPos().getZ());
        chunkTicket.getModData().setString("type", getMachineType().getTag());
    }

    public boolean isTicketInvalid() {
        Ticket ticket = getTicket();
        return ticket != null && (ticket.world != world || refreshTicket || powered);
    }

    public boolean hasActiveTicket() {
        if (Game.isClient(world))
            return hasTicket;
        return getTicket() != null;
    }

    public @Nullable Ticket getTicket() {
        return tickets.get(getUUID());
    }

    public void setTicket(@Nullable Ticket t) {
        boolean changed = false;
        Ticket ticket = getTicket();
        if (ticket != t) {
            if (ticket != null) {
                if (ticket.world == world) {
                    for (ChunkPos chunk : ticket.getChunkList()) {
                        if (ForgeChunkManager.getPersistentChunksFor(world).keys().contains(chunk))
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
        else if (hasPoint())
            chunks = ChunkManager.getInstance().getChunksBetween(getX() >> 4, getZ() >> 4, pointPos.getX() >> 4, pointPos.getZ() >> 4, getMaxPointChunks());
        else
            chunks = ChunkManager.getInstance().getChunksAround(getX() >> 4, getZ() >> 4, CHUNK_RADIUS);
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean power) {
        powered = power;
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block, BlockPos neighborPos) {
        super.onNeighborBlockChange(state, block, neighborPos);
        if (Game.isClient(getWorld()))
            return;
        boolean newPower = PowerPlugin.isBlockBeingPowered(world, getPos());
        if (powered != newPower)
            powered = newPower;
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(hasTicket);

        data.writeBlockPos(pointPos);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        boolean tick = data.readBoolean();
        if (hasTicket != tick) {
            hasTicket = tick;
            markBlockForUpdate();
        }

        pointPos = data.readBlockPos();

        setupChunks();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setLong("fuel", fuel);

        data.setBoolean("powered", powered);

        NBTPlugin.writeBlockPos(data, "point", pointPos);

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

        BlockPos pos = NBTPlugin.readBlockPos(data, "point");
        pointPos = pos == null ? BlockPos.ORIGIN : pos;

        prevX = data.getInteger("prevX");
        prevY = data.getInteger("prevY");
        prevZ = data.getInteger("prevZ");
    }

    @Override
    public long getFuelAmount() {
        return fuel;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (RailcraftConfig.worldspikesCanInteractWithPipes())
            return SLOTS;
        return SLOTS_NO_ACCESS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return RailcraftConfig.worldspikesCanInteractWithPipes();
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }
}
