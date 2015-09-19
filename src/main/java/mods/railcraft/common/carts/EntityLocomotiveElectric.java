/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.api.electricity.IElectricMinecart;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityLocomotiveElectric extends EntityLocomotive implements ISidedInventory, IElectricMinecart {

    private static final int CHARGE_USE_PER_TICK = 20;
    private static final int FUEL_PER_REQUEST = 1;
    private static final int CHARGE_USE_PER_REQUEST = CHARGE_USE_PER_TICK * FUEL_PER_REQUEST;
    public static final double MAX_CHARGE = 5000.0;
    private static final int SLOT_TICKET = 0;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 1);
    private final IInventory invTicket = new InventoryMapper(this, SLOT_TICKET, 2, false);
    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.Type.USER, MAX_CHARGE);

    public EntityLocomotiveElectric(World world) {
        super(world);
    }

    public EntityLocomotiveElectric(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.LOCO_ELECTRIC;
    }

    @Override
    public String getWhistle() {
        return SoundHelper.SOUND_LOCOMOTIVE_ELECTRIC_WHISTLE;
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    protected void openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.LOCO_ELECTRIC, player, worldObj, this);
    }

    @Override
    public void setMode(LocoMode mode) {
        if (mode == LocoMode.IDLE)
            mode = LocoMode.SHUTDOWN;
        super.setMode(mode);
    }

    @Override
    protected int getIdleFuelUse() {
        return 0;
    }

    @Override
    public int getMoreGoJuice() {
        if (chargeHandler.getCharge() > CHARGE_USE_PER_REQUEST) {
            chargeHandler.removeCharge(CHARGE_USE_PER_REQUEST);
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
        return EnumCart.LOCO_ELECTRIC.getCartItem();
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 0.92f;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isNotHost(worldObj))
            return;
        chargeHandler.tick();
    }

    @Override
    protected void func_145821_a(int trackX, int trackY, int trackZ, double maxSpeed, double slopeAdjustement, Block trackBlock, int trackMeta) {
        super.func_145821_a(trackX, trackY, trackZ, maxSpeed, slopeAdjustement, trackBlock, trackMeta);
        if (Game.isNotHost(worldObj))
            return;
        chargeHandler.tickOnTrack(trackX, trackY, trackZ);
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
    public int[] getAccessibleSlotsFromSide(int var1) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot == SLOT_TICKET;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case SLOT_TICKET:
                return ItemTicket.FILTER.matches(stack);
            default:
                return false;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        chargeHandler.writeToNBT(data);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        chargeHandler.readFromNBT(data);
    }

}
