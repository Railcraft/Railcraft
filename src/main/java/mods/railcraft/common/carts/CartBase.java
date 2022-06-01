/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import mods.railcraft.api.carts.IItemCart;
import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.blocks.logic.InventoryLogic;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.Logic.Adapter;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.containers.FactoryContainer;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.IExtInvSlot;
import mods.railcraft.common.util.inventory.IInventoryComposite;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import mods.railcraft.common.util.inventory.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * It also contains some generic code that most carts will find useful.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBase extends EntityMinecartContainer implements IRailcraftCart, INeedsFuel, IItemCart, IInventoryComposite, ILogicContainer, IEntityAdditionalSpawnData {
    private final EnumFacing[] travelDirectionHistory = new EnumFacing[2];
    protected @Nullable EnumFacing travelDirection;
    protected @Nullable EnumFacing verticalTravelDirection;
    @SuppressWarnings("CanBeFinal")
    protected List<InventoryMapper> invMappers = new ArrayList<>();
    protected Logic logic;

    protected CartBase(World world) {
        super(world);
    }

    protected CartBase(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        logic = new Logic(Adapter.of(this));
        cartInit();
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean needsFuel() {
        return getLogic(INeedsFuel.class).map(INeedsFuel::needsFuel).orElse(false);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        saveToNBT(compound);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        loadFromNBT(compound);
    }

    @Override
    public String getName() {
        return hasCustomName() ? getCustomNameTag() : LocalizationPlugin.translate(getCartType().getEntityLocalizationTag());
    }

    @Override
    public final boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand)) || doInteract(player, hand);
    }

    @OverridingMethodsMustInvokeSuper
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        if (Game.isHost(world)) {
            if (!logic.interact(player, hand))
                openRailcraftGui(player);
        }
        return true;
    }

    @Override
    public final ItemStack getCartItem() {
        return createCartItem(this);
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    @Override
    public <L> Optional<L> getLogic(Class<L> logicClass) {
        return logic.getLogic(logicClass);
    }

    @Override
    public void setDead() {
        if (Game.isClient(world)) {
            InventoryIterator.get(this).stream().forEach(IExtInvSlot::clear);
            getLogic(IInventoryImplementor.class).ifPresent(inv ->
                    InventoryIterator.get(inv).stream().forEach(IExtInvSlot::clear));
        }
        super.setDead();
    }

    @Override
    public void killMinecart(DamageSource damageSource) {
        killAndDrop(this);
    }

    /**
     * {@link net.minecraft.entity.item.EntityArmorStand#IS_RIDEABLE_MINECART}
     */
    @Override
    public EntityMinecart.Type getType() {
        throw new RuntimeException("This method should NEVER be called");
    }

    @Override
    public boolean isPoweredCart() {
        return false;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    protected void updateTravelDirection(BlockPos pos, IBlockState state) {
        BlockRailBase.EnumRailDirection shape = TrackTools.getTrackDirection(getEntityWorld(), pos, state);
        @Nullable EnumFacing facing = determineTravelDirection(shape);
        @Nullable EnumFacing previousEnumFacing = travelDirectionHistory[1];
        if (previousEnumFacing != null && travelDirectionHistory[0] == previousEnumFacing) {
            travelDirection = facing;
            verticalTravelDirection = determineVerticalTravelDirection(shape);
        }
        travelDirectionHistory[0] = previousEnumFacing;
        travelDirectionHistory[1] = facing;
    }

    private @Nullable EnumFacing determineTravelDirection(BlockRailBase.EnumRailDirection shape) {
        if (TrackShapeHelper.isStraight(shape)) {
            if (posX - prevPosX > 0)
                return EnumFacing.EAST;
            if (posX - prevPosX < 0)
                return EnumFacing.WEST;
            if (posZ - prevPosZ > 0)
                return EnumFacing.SOUTH;
            if (posZ - prevPosZ < 0)
                return EnumFacing.NORTH;
        } else {
            switch (shape) {
                case SOUTH_EAST:
                    if (prevPosZ > posZ)
                        return EnumFacing.EAST;
                    else
                        return EnumFacing.SOUTH;
                case SOUTH_WEST:
                    if (prevPosZ > posZ)
                        return EnumFacing.WEST;
                    else
                        return EnumFacing.SOUTH;
                case NORTH_WEST:
                    if (prevPosZ > posZ)
                        return EnumFacing.NORTH;
                    else
                        return EnumFacing.WEST;
                case NORTH_EAST:
                    if (prevPosZ > posZ)
                        return EnumFacing.NORTH;
                    else
                        return EnumFacing.EAST;
            }
        }
        return null;
    }

    private @Nullable EnumFacing determineVerticalTravelDirection(BlockRailBase.EnumRailDirection shape) {
        if (shape.isAscending())
            return prevPosY < posY ? EnumFacing.UP : EnumFacing.DOWN;
        return null;
    }

    @Override
    public boolean canPassItemRequests(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canAcceptPushedItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canProvidePulledItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }

    @Override
    public String getGuiID() {
        return "railcraft:" + getCartType().getBaseTag();
    }

    protected void openRailcraftGui(EntityPlayer player) {
        if (getGuiType().isPresent())
            GuiHandler.openGui(getGuiType().get(), player, world, this);
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        if (getGuiType().isPresent())
            return FactoryContainer.build(getGuiType().get(), playerInventory, this, world, (int) posX, (int) posY, (int) posZ);
        throw new UnsupportedOperationException("No GUI defined.");
    }

    /**
     * Gets the GUI type of the minecart.
     */
    protected Optional<EnumGui> getGuiType() {
        return Optional.of(logic).map(Logic::getGUI);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return invMappers.stream().filter(m -> m.containsSlot(index)).allMatch(m -> m.filter().test(stack));
    }

    /**
     * Checks if the entity is in range to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return CartTools.isInRangeToRenderDist(this, distance);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        logic.update();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        logic.writeToNBT(data);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        logic.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        logic.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        logic.readPacketData(data);
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        logic.writeGuiData(data);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        logic.readGuiData(data, sender);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        try {
            writePacketData(new RailcraftOutputStream(new ByteBufOutputStream(buffer)));
        } catch (IOException ignored) {
        }
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        try {
            readPacketData(new RailcraftInputStream(new ByteBufInputStream(buffer)));
        } catch (IOException ignored) {
        }
    }

    @Override
    public void sendUpdateToClient() {
        if (isAddedToWorld() && isEntityAlive())
            PacketBuilder.instance().sendEntitySync(this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                && getLogic(InventoryLogic.class).isPresent())
            return true;
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                && getLogic(IFluidHandler.class).isPresent())
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            Optional<InventoryLogic> inv = getLogic(InventoryLogic.class);
            if (inv.isPresent())
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv.get().getItemHandler(facing));
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            Optional<IFluidHandler> tank = getLogic(IFluidHandler.class);
            if (tank.isPresent())
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank.get());
        }
        return super.getCapability(capability, facing);
    }

    public List<String> getDebugOutput() {
        List<String> debug = new ArrayList<>();
        debug.add("Railcraft Entity Data Dump");
        debug.add("Object: " + this);
        debug.add(String.format("Coordinates: d=%d, %s", world.provider.getDimension(), getPositionVector()));
        debug.add("Owner: " + CartTools.getCartOwnerEntity(this));
        debug.add("LinkA: " + CartTools.getCartOwnerEntity(this));
        return debug;
    }

    @Override
    public @Nullable World theWorld() {
        return world;
    }
}
