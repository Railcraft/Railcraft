/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ICartContentsTextureProvider;
import mods.railcraft.api.carts.IMinecart;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.collections.ItemMap;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.ChunkManager;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.IAnchor;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntityCartAnchor extends CartContainerBase implements ICartContentsTextureProvider, IAnchor, IMinecart {
    public static final byte TICKET_FLAG = 6;
    private static final byte ANCHOR_RADIUS = 2;
    private static final byte MAX_CHUNKS = 25;
    private final IInventory invWrapper = new InventoryMapper(this);
    protected Ticket ticket;
    private Set<ChunkCoordIntPair> chunks;
    private long anchorFuel;
    private boolean teleported = false;
    private int disabled = 0;
    private int clock = MiscTools.getRand().nextInt();

    public EntityCartAnchor(World world) {
        super(world);
    }

    public EntityCartAnchor(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.ANCHOR;
    }

    @Override
    public void initEntityFromItem(ItemStack stack) {
        super.initEntityFromItem(stack);
        long fuel = ItemCartAnchor.getFuel(stack);
        setAnchorFuel(fuel);
    }

    private boolean hasFuel() {
        return anchorFuel > 0;
    }

    public boolean hasActiveTicket() {
        return ticket != null || (Game.isNotHost(worldObj) && getFlag(TICKET_FLAG));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isNotHost(worldObj)) {
            if (getFlag(TICKET_FLAG))
                if (chunks != null)
                    EffectManager.instance.chunkLoaderEffect(worldObj, this, chunks);
                else
                    setupChunks(chunkCoordX, chunkCoordZ);
            return;
        }

        if (RailcraftConfig.deleteAnchors()) {
            setDead();
            return;
        }

        if (disabled > 0)
            disabled--;

        if (needsFuel()) {
            if (ticket != null && anchorFuel > 0)
                anchorFuel--;
            if (anchorFuel <= 0) {
                stockFuel();
                ItemStack stack = getStackInSlot(0);
                if (stack == null || stack.stackSize <= 0) {
                    setInventorySlotContents(0, null);
                    releaseTicket();
                } else if (getFuelMap().containsKey(stack)) {
                    decrStackSize(0, 1);
                    anchorFuel = (long) (getFuelMap().get(stack) * RailcraftConstants.TICKS_PER_HOUR);
                }
            }
        }

        if (ticket == null)
            requestTicket();

        if (RailcraftConfig.printAnchorDebug() && ticket != null) {
            clock++;
            if (clock % 64 == 0) {
                ChatPlugin.sendLocalizedChatToAllFromServer(worldObj, "%s has a ticket and is ticking at <%.0f,%.0f,%.0f> in dim:%d - logged on tick %d", getCommandSenderName(), posX, posY, posZ, worldObj.provider.dimensionId, worldObj.getWorldTime());
                Game.log(Level.DEBUG, "{0} has a ticket and is ticking at <{1},{2},{3}> in dim:{4} - logged on tick {5}", getCommandSenderName(), posX, posY, posZ, worldObj.provider.dimensionId, worldObj.getWorldTime());
            }
        }
    }

    private void stockFuel() {
        ItemStack stack = getStackInSlot(0);
        if (stack != null && !getFuelMap().containsKey(stack)) {
            CartTools.offerOrDropItem(this, stack);
            setInventorySlotContents(0, null);
            return;
        }
        stack = getStackInSlot(0);
        if (stack == null) {
            ItemStack found = CartTools.transferHelper.pullStack(this, getFuelMap().getStackFilter());
            if (found != null)
                InvTools.moveItemStack(found, this);
        }
    }

    protected Ticket getTicketFromForge() {
        return ForgeChunkManager.requestTicket(Railcraft.getMod(), worldObj, Type.ENTITY);
    }

    public boolean needsFuel() {
        return !getFuelMap().isEmpty();
    }

    @Override
    public ItemMap<Float> getFuelMap() {
        return RailcraftConfig.anchorFuelWorld;
    }

    protected boolean meetsTicketRequirements() {
        return !isDead && !teleported && disabled <= 0 && (hasFuel() || !needsFuel());
    }

    protected void releaseTicket() {
        ForgeChunkManager.releaseTicket(ticket);
        ticket = null;
        setFlag(TICKET_FLAG, false);
    }

    private boolean requestTicket() {
        if (meetsTicketRequirements()) {
            Ticket chunkTicket = getTicketFromForge();
            if (chunkTicket != null) {
//                System.out.println("Request Ticket: " + worldObj.getClass().getSimpleName());
                chunkTicket.getModData();
                chunkTicket.setChunkListDepth(MAX_CHUNKS);
                chunkTicket.bindEntity(this);
                setChunkTicket(chunkTicket);
                forceChunkLoading(chunkCoordX, chunkCoordZ);
                return true;
            }
        }
        return false;
    }

    public void setChunkTicket(Ticket tick) {
        if (this.ticket != tick)
            ForgeChunkManager.releaseTicket(this.ticket);
        this.ticket = tick;
        setFlag(TICKET_FLAG, ticket != null);
    }

    public void forceChunkLoading(int xChunk, int zChunk) {
        if (ticket == null)
            return;

        setupChunks(xChunk, zChunk);

        Set<ChunkCoordIntPair> innerChunks = ChunkManager.getInstance().getChunksAround(xChunk, zChunk, 1);

//        System.out.println("Chunks Loaded = " + Arrays.toString(chunks.toArray()));
        for (ChunkCoordIntPair chunk : chunks) {
            ForgeChunkManager.forceChunk(ticket, chunk);
            ForgeChunkManager.reorderChunk(ticket, chunk);
        }
        for (ChunkCoordIntPair chunk : innerChunks) {
            ForgeChunkManager.forceChunk(ticket, chunk);
            ForgeChunkManager.reorderChunk(ticket, chunk);
        }


        ChunkCoordIntPair myChunk = new ChunkCoordIntPair(xChunk, zChunk);
        ForgeChunkManager.forceChunk(ticket, myChunk);
        ForgeChunkManager.reorderChunk(ticket, myChunk);
    }

    public void setupChunks(int xChunk, int zChunk) {
        if (getFlag(TICKET_FLAG))
            chunks = ChunkManager.getInstance().getChunksAround(xChunk, zChunk, ANCHOR_RADIUS);
        else
            chunks = null;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);

        data.setLong("anchorFuel", anchorFuel);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);

        if (needsFuel())
            anchorFuel = data.getLong("anchorFuel");
    }

    @Override
    public void setDead() {
        releaseTicket();
        super.setDead();
    }

    public void travelToDimension(int dim) {
        teleported = true;
        releaseTicket();
        super.travelToDimension(dim);
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(worldObj) && needsFuel())
            GuiHandler.openGui(EnumGui.CART_ANCHOR, player, worldObj, this);
        return true;
    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        items.add(getCartItem());
        return items;
    }

    @Override
    public ItemStack getCartItem() {
        ItemStack drop = super.getCartItem();
        if (needsFuel() && hasFuel()) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setLong("fuel", anchorFuel);
            drop.setTagCompound(nbt);
        }
        return drop;
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        return EnumCart.getCartType(stack) == EnumCart.ANCHOR;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return needsFuel() ? 1 : 0;
    }

    @Override
    public String getInventoryName() {
        return LocalizationPlugin.translate(EnumCart.ANCHOR.getTag());
    }

    @Override
    public Block func_145820_n() {
        return RailcraftBlocks.getBlockMachineAlpha();
    }

    @Override
    public int getDisplayTileData() {
        return EnumMachineAlpha.WORLD_ANCHOR.ordinal();
    }

    @Override
    public IIcon getBlockTextureOnSide(int side) {
        if (side < 2 && !getFlag(TICKET_FLAG))
            return EnumMachineAlpha.WORLD_ANCHOR.getTexture(6);
        return EnumMachineAlpha.WORLD_ANCHOR.getTexture(side);
    }

    @Override
    public long getAnchorFuel() {
        return anchorFuel;
    }

    public void setAnchorFuel(long fuel) {
        anchorFuel = fuel;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!RailcraftConfig.anchorsCanInteractWithPipes())
            return false;
        return getFuelMap().containsKey(stack);
    }

    @Override
    public double getDrag() {
        return CartConstants.STANDARD_DRAG;
    }

    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean powered) {
        if (powered) {
            disabled = 10;
            releaseTicket();
        }
    }
}
