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
import mods.railcraft.common.blocks.machine.IEnumMachine;
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
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import org.apache.logging.log4j.Level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    private static final int[] SLOTS = new int[]{0};
    private static final int[] SLOTS_NO_ACCESS = new int[]{};
    private int xSentinel = -1;
    private int ySentinel = -1;
    private int zSentinel = -1;
    private int prevX, prevY, prevZ;
    private Set<ChunkCoordIntPair> chunks;
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
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.WORLD_ANCHOR;
    }

    @Override
    public IIcon getIcon(int side) {
        if (!hasTicket && side < 2)
            return getMachineType().getTexture(6);
        return getMachineType().getTexture(side);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, int side) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, xCoord, yCoord, zCoord)) {
                if (Game.isHost(worldObj)) {
                    WorldCoordinate target = sentinelPairingMap.get(player);
                    if (target == null)
                        setTarget(this, player);
                    else if (worldObj.provider.dimensionId != target.dimension)
                        ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.dimension", getLocalizationTag());
                    else if (new WorldCoordinate(this).equals(target)) {
                        removeTarget(player);
                        ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.cancel", getLocalizationTag());
                    } else
                        setSentinel(player, target);
                    crowbar.onWhack(player, current, xCoord, yCoord, zCoord);
                }
                return true;
            }
        }
        return super.blockActivated(player, side);
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
            GuiHandler.openGui(EnumGui.WORLD_ANCHOR, player, worldObj, xCoord, yCoord, zCoord);
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
        if (!WorldPlugin.blockExists(searcher.getWorldObj(), coord.x, coord.y, coord.z)) {
            ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.unloaded", searcher.getLocalizationTag());
            return null;
        }
        return WorldPlugin.getBlockTile(searcher.getWorldObj(), coord.x, coord.y, coord.z);
    }

    public boolean setSentinel(EntityPlayer player, WorldCoordinate coord) {
        TileEntity tile = getTargetAt(player, this, coord);
        if (tile == null)
            return false;
        if (tile instanceof TileSentinel) {
            int xChunk = xCoord >> 4;
            int zChunk = zCoord >> 4;

            int xSentinelChunk = tile.xCoord >> 4;
            int zSentinelChunk = tile.zCoord >> 4;

            if (xChunk != xSentinelChunk && zChunk != zSentinelChunk) {
                ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.alignment", getLocalizationTag(), ((TileSentinel) tile).getLocalizationTag());
                return false;
            }

            int max = getMaxSentinelChunks();
            if (Math.abs(xChunk - xSentinelChunk) >= max || Math.abs(zChunk - zSentinelChunk) >= max) {
                ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.anchor.pair.fail.distance", getLocalizationTag(), ((TileSentinel) tile).getLocalizationTag());
                return false;
            }

            xSentinel = tile.xCoord;
            ySentinel = tile.yCoord;
            zSentinel = tile.zCoord;

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
    public ArrayList<ItemStack> getDrops(int fortune) {
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
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(worldObj)) {
            if (chunks != null)
                EffectManager.instance.chunkLoaderEffect(worldObj, this, chunks);
            return;
        }

        if (RailcraftConfig.deleteAnchors()) {
            releaseTicket();
            worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.obsidian);
            return;
        }

        if (xCoord != prevX || yCoord != prevY || zCoord != prevZ) {
            releaseTicket();
            prevX = xCoord;
            prevY = yCoord;
            prevZ = zCoord;
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
            TileEntity tile = worldObj.getTileEntity(xSentinel, ySentinel, zSentinel);
            if (!(tile instanceof TileSentinel))
                clearSentinel();
        }

        if (!hasActiveTicket())
            requestTicket();

        if (RailcraftConfig.printAnchorDebug() && hasActiveTicket())
            if (clock % 64 == 0) {
                int numChunks = chunks == null ? 0 : chunks.size();
                ChatPlugin.sendLocalizedChatToAllFromServer(worldObj, "%s has loaded %d chunks and is ticking at <%d,%d,%d> in dim:%d - logged on tick %d", getName(), numChunks, xCoord, yCoord, zCoord, worldObj.provider.dimensionId, worldObj.getWorldTime());
                Game.log(Level.DEBUG, "{0} has loaded {1} chunks and is ticking at <{2},{3},{4}> in dim:{5} - logged on tick {6}", getName(), numChunks, xCoord, yCoord, zCoord, worldObj.provider.dimensionId, worldObj.getWorldTime());
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
        chunkTicket.getModData().setInteger("xCoord", xCoord);
        chunkTicket.getModData().setInteger("yCoord", yCoord);
        chunkTicket.getModData().setInteger("zCoord", zCoord);
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
                    for (ChunkCoordIntPair chunk : ticket.getChunkList()) {
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
            for (ChunkCoordIntPair chunk : chunks) {
                ForgeChunkManager.forceChunk(ticket, chunk);
            }
    }

    public void setupChunks() {
        if (!hasTicket)
            chunks = null;
        else if (hasSentinel())
            chunks = ChunkManager.getInstance().getChunksBetween(xCoord >> 4, zCoord >> 4, xSentinel >> 4, zSentinel >> 4, getMaxSentinelChunks());
        else
            chunks = ChunkManager.getInstance().getChunksAround(xCoord >> 4, zCoord >> 4, ANCHOR_RADIUS);
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean power) {
        powered = power;
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        if (Game.isNotHost(getWorld()))
            return;
        boolean newPower = PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord, zCoord);
        if (powered != newPower)
            powered = newPower;
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(hasTicket);

        data.writeInt(xSentinel);
        data.writeInt(ySentinel);
        data.writeInt(zSentinel);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
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

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setLong("fuel", fuel);

        data.setBoolean("powered", powered);

        data.setInteger("xSentinel", xSentinel);
        data.setInteger("ySentinel", ySentinel);
        data.setInteger("zSentinel", zSentinel);

        data.setInteger("prevX", prevX);
        data.setInteger("prevY", prevY);
        data.setInteger("prevZ", prevZ);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
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

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        if (RailcraftConfig.anchorsCanInteractWithPipes())
            return SLOTS;
        return SLOTS_NO_ACCESS;
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return RailcraftConfig.anchorsCanInteractWithPipes();
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return false;
    }
}
