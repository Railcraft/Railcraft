/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.carts.IMinecart;
import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class EntityCartWorldspike extends CartBaseContainer implements IWorldspike, IMinecart, INeedsFuel {
    private static final DataParameter<Boolean> TICKET = DataManagerPlugin.create(DataSerializers.BOOLEAN);
    private static final byte CHUNK_RADIUS = 2;
    private static final byte MAX_CHUNKS = 25;
    protected @Nullable Ticket ticket;
    private Set<ChunkPos> chunks = Collections.emptySet();
    private long fuel;
    private boolean teleported;
    private int disabled;
    private int clock = MiscTools.RANDOM.nextInt();

    protected EntityCartWorldspike(World world) {
        super(world);
    }

    protected EntityCartWorldspike(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(TICKET, false);
    }

    @Override
    public void initEntityFromItem(ItemStack stack) {
        super.initEntityFromItem(stack);
        long fuel = ItemCartWorldspike.getFuel(stack);
        setFuel(fuel);
    }

    private boolean hasFuel() {
        return fuel > 0;
    }

    public boolean hasActiveTicket() {
        return ticket != null || (Game.isClient(world) && hasTicketFlag());
    }

    private void setTicketFlag(boolean flag) {
        dataManager.set(TICKET, flag);
    }

    public boolean hasTicketFlag() {
        return dataManager.get(TICKET);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isClient(world)) {
            if (hasTicketFlag())
                if (chunks != null)
                    ClientEffects.INSTANCE.chunkLoaderEffect(world, this, chunks);
                else
                    setupChunks(chunkCoordX, chunkCoordZ);
            return;
        }

        if (RailcraftConfig.deleteWorldspikes()) {
            setDead();
            return;
        }

//        if (disabled > 0)
//            disabled--;

        if (usesFuel()) {
            if (ticket != null && fuel > 0)
                fuel--;
            if (fuel <= 0) {
                stockFuel();
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

        if (ticket == null)
            requestTicket();

        if (RailcraftConfig.printWorldspikeDebug() && ticket != null) {
            clock++;
            if (clock % 64 == 0) {
                ChatPlugin.sendLocalizedChatToAllFromServer(world, "%s has a ticket and is ticking at <%.0f,%.0f,%.0f> in dim:%d - logged on tick %d", getName(), posX, posY, posZ, world.provider.getDimension(), world.getWorldTime());
                Game.log().msg(Level.DEBUG, "{0} has a ticket and is ticking at <{1},{2},{3}> in dim:{4} - logged on tick {5}", getName(), posX, posY, posZ, world.provider.getDimension(), world.getWorldTime());
            }
        }
    }

    private void stockFuel() {
        ItemStack stack = getStackInSlot(0);
        if (!InvTools.isEmpty(stack) && !getFuelValue(stack).isPresent()) {
            CartToolsAPI.transferHelper().offerOrDropItem(this, stack);
            setInventorySlotContents(0, InvTools.emptyStack());
            return;
        }
        stack = getStackInSlot(0);
        if (InvTools.isEmpty(stack)) {
            ItemStack found = CartToolsAPI.transferHelper().pullStack(this, Predicates.and(getFuelMap().keySet()));
            if (!InvTools.isEmpty(found))
                addStack(found);
        }
    }

    protected abstract @Nullable Ticket getTicketFromForge();

    public boolean usesFuel() {
        return !getFuelMap().isEmpty();
    }

    @Override
    public boolean needsFuel() {
        if (!usesFuel())
            return false;
        ItemStack stack = getStackInSlot(0);
        return InvTools.isEmpty(stack) || (stack.getMaxStackSize() > 1 && InvTools.sizeOf(stack) <= 1);
    }

    @Override
    public abstract Map<Ingredient, Float> getFuelMap();

    protected boolean meetsTicketRequirements() {
        return !isDead && !teleported && disabled <= 0 && (hasFuel() || !usesFuel());
    }

    protected void releaseTicket() {
        ForgeChunkManager.releaseTicket(ticket);
        ticket = null;
        setTicketFlag(false);
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean requestTicket() {
        if (meetsTicketRequirements()) {
            Ticket chunkTicket = getTicketFromForge();
            if (chunkTicket != null) {
//                System.out.println("Request Ticket: " + world.getClass().getSimpleName());
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

    public void setChunkTicket(@Nullable Ticket ticket) {
        if (this.ticket != ticket)
            ForgeChunkManager.releaseTicket(this.ticket);
        this.ticket = ticket;
        setTicketFlag(this.ticket != null);
    }

    public void forceChunkLoading(int xChunk, int zChunk) {
        if (ticket == null)
            return;

        setupChunks(xChunk, zChunk);

        Set<ChunkPos> innerChunks = ChunkManager.getInstance().getChunksAround(xChunk, zChunk, 1);

//        System.out.println("Chunks Loaded = " + Arrays.toString(chunks.toArray()));
        for (ChunkPos chunk : chunks) {
            ForgeChunkManager.forceChunk(ticket, chunk);
            ForgeChunkManager.reorderChunk(ticket, chunk);
        }
        for (ChunkPos chunk : innerChunks) {
            ForgeChunkManager.forceChunk(ticket, chunk);
            ForgeChunkManager.reorderChunk(ticket, chunk);
        }


        ChunkPos myChunk = new ChunkPos(xChunk, zChunk);
        ForgeChunkManager.forceChunk(ticket, myChunk);
        ForgeChunkManager.reorderChunk(ticket, myChunk);
    }

    public void setupChunks(int xChunk, int zChunk) {
        if (hasTicketFlag())
            chunks = ChunkManager.getInstance().getChunksAround(xChunk, zChunk, CHUNK_RADIUS);
        else
            chunks = Collections.emptySet();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);

        data.setLong("fuel", fuel);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);

        if (usesFuel())
            fuel = data.getLong("fuel");
    }

    @Override
    public void setDead() {
        releaseTicket();
        super.setDead();
    }

    @Override
    public Entity changeDimension(int dim) {
        teleported = true;
        releaseTicket();
        return super.changeDimension(dim);
    }

    @Override
    public ItemStack createCartItem(EntityMinecart cart) {
        ItemStack drop = super.createCartItem(cart);
        if (usesFuel() && hasFuel()) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setLong("fuel", fuel);
            drop.setTagCompound(nbt);
        }
        return drop;
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        if (stack.hasDisplayName()) {
            if (!hasCustomName())
                return false;
            if (!stack.getDisplayName().equals(getCustomNameTag()))
                return false;
        }
        return getCartType() == RailcraftCarts.getCartType(stack);
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return usesFuel() ? 1 : 0;
    }

    @Override
    public int getInventoryStackLimit() {
        return 16;
    }

    @Override
    public abstract IBlockState getDefaultDisplayTile();

    @Override
    public long getFuelAmount() {
        return fuel;
    }

    public void setFuel(long fuel) {
        this.fuel = fuel;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return RailcraftConfig.worldspikesCanInteractWithPipes() && getFuelValue(stack).isPresent();
    }

    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean powered) {
        if (powered) {
            disabled = 1;
            releaseTicket();
        }
        if (!powered) {
            disabled = 0;
        }
    }

    @Override
    protected void openRailcraftGui(EntityPlayer player) {
        if (needsFuel())
            super.openRailcraftGui(player);
    }

    @Override
    protected EnumGui getGuiType() {
        return EnumGui.CART_WORLDSPIKE;
    }
}
