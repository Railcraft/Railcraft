/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.charge.CapabilitiesCharge;
import mods.railcraft.api.charge.IBatteryCart;
import mods.railcraft.client.render.carts.LocomotiveRenderType;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.util.charge.CartBattery;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.RailcraftSoundEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityLocomotiveElectric extends EntityLocomotive implements ISidedInventory {

    private static final int CHARGE_USE_PER_TICK = 20;
    private static final int FUEL_PER_REQUEST = 1;
    private static final int CHARGE_USE_PER_REQUEST = CHARGE_USE_PER_TICK * FUEL_PER_REQUEST;
    public static final double MAX_CHARGE = 5000.0;
    private static final int SLOT_TICKET = 0;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 1);
    private final IInventory invTicket = new InventoryMapper(this, SLOT_TICKET, 2).ignoreItemChecks();
    private final IBatteryCart cartBattery = new CartBattery(IBatteryCart.Type.USER, MAX_CHARGE);

    public EntityLocomotiveElectric(World world) {
        super(world);
    }

    public EntityLocomotiveElectric(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    {
        setAllowedModes(EnumSet.of(LocoMode.RUNNING, LocoMode.SHUTDOWN));
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.LOCO_ELECTRIC;
    }

    @Override
    public SoundEvent getWhistle() {
        return RailcraftSoundEvents.ENTITY_LOCOMOTIVE_ELECTRIC_WHISTLE.getSoundEvent();
    }

    @Override
    protected int getIdleFuelUse() {
        return 0;
    }

    @Override
    public int getMoreGoJuice() {
        if (cartBattery.getCharge() > CHARGE_USE_PER_REQUEST) {
            cartBattery.removeCharge(CHARGE_USE_PER_REQUEST);
            return FUEL_PER_REQUEST;
        }
        return 0;
    }

    @Override
    public LocomotiveRenderType getRenderType() {
        return LocomotiveRenderType.ELECTRIC;
    }

    @Override
    protected ItemStack getCartItemBase() {
        return RailcraftCarts.LOCO_ELECTRIC.getStack();
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 0.92f;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isClient(world))
            return;
        cartBattery.tick(this);
    }

    @Override
    protected void moveAlongTrack(BlockPos pos, IBlockState state) {
        super.moveAlongTrack(pos, state);
        if (Game.isClient(world))
            return;
        cartBattery.tickOnTrack(this, pos);
    }

    @Override
    protected IInventory getTicketInventory() {
        return invTicket;
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return slot == SLOT_TICKET;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nullable ItemStack stack) {
        switch (slot) {
            case SLOT_TICKET:
                return ItemTicket.FILTER.test(stack);
            default:
                return false;
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilitiesCharge.CART_BATTERY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilitiesCharge.CART_BATTERY)
            return (T) cartBattery;
        return super.getCapability(capability, facing);
    }

    @Override
    protected EnumGui getGuiType() {
        return EnumGui.LOCO_ELECTRIC;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        cartBattery.writeToNBT(data);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        cartBattery.readFromNBT(data);
    }
}
