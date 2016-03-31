/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.triggers.INeedsFuel;
import mods.railcraft.common.plugins.buildcraft.triggers.ITemperature;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.steam.SolidFuelProvider;
import mods.railcraft.common.util.steam.Steam;
import mods.railcraft.common.util.steam.SteamBoiler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileEngineSteamHobby extends TileEngineSteam implements ISidedInventory, INeedsFuel, ITemperature {

    public static final byte SLOT_FUEL = 0;
    public static final byte SLOT_LIQUID_INPUT = 1;
    public static final byte SLOT_LIQUID_OUTPUT = 2;
    private static final int OUTPUT_RF = 20;
    private static final int STEAM_USED = Steam.STEAM_PER_10RF * (OUTPUT_RF / 10);
    private static final float FUEL_PER_CONVERSION_MULTIPLIER = 1.25F;
    private static final byte TICKS_PER_BOILER_CYCLE = 20;
    private static final byte TANK_WATER = 1;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 3);
    private static final int[] NO_SLOTS = new int[0];
    public final SteamBoiler boiler;
    private StandaloneInventory inv = new StandaloneInventory(3, (IInventory) this);
    private boolean explode = false;

    public TileEngineSteamHobby() {
        FilteredTank tankWater = new FilteredTank(4 * FluidHelper.BUCKET_VOLUME, Fluids.WATER.get(), this);
        getTankManager().add(tankWater);
        getTankManager().get(TANK_STEAM).setCapacity(4 * FluidHelper.BUCKET_VOLUME);

        boiler = new SteamBoiler(tankWater, getTankManager().get(TANK_STEAM));
        boiler.setTicksPerCycle(TICKS_PER_BOILER_CYCLE);
        boiler.setEfficiencyModifier(FUEL_PER_CONVERSION_MULTIPLIER);
        boiler.setFuelProvider(new SolidFuelProvider(inv, SLOT_FUEL) {
            @Override
            public double getMoreFuel() {
                if (getEnergyStage() == EnergyStage.OVERHEAT || !isPowered()) return 0;
                return super.getMoreFuel();
            }
        });
    }

    @Override
    public EnumMachineBeta getMachineType() {
        return EnumMachineBeta.ENGINE_STEAM_HOBBY;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.ENGINE_HOBBY, player, worldObj, getPos());
        return true;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumFacing side) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() != Items.bucket)
            if (Game.isHost(worldObj)) {
                if (FluidHelper.handleRightClick(this, side, player, true, false))
                    return true;
            } else if (FluidItemHelper.isContainer(current))
                return true;
        return super.blockActivated(player, side);
    }

    @Override
    public int getMaxOutputRF() {
        return OUTPUT_RF;
    }

    @Override
    public int steamUsedPerTick() {
        return STEAM_USED;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isHost(worldObj)) {
            if (explode) {
                worldObj.createExplosion(null, getX(), getY(), getZ(), 2, true);
                explode = false;
            }
        }
    }

    @Override
    public void burn() {
        super.burn();

        if (clock % FluidHelper.BUCKET_FILL_TIME == 0)
            FluidHelper.drainContainers(this, inv, SLOT_LIQUID_INPUT, SLOT_LIQUID_OUTPUT);

        boiler.tick(1);
    }

    @Override
    public int getSizeInventory() {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return inv.decrStackSize(slot, amount);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inv.setInventorySlotContents(slot, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return RailcraftTileEntity.isUsableByPlayerHelper(this, player);
    }

    @Override
    public int maxEnergy() {
        return 100000;
    }

    @Override
    public int maxEnergyReceived() {
        return 3000;
    }

    @Override
    public boolean needsFuel() {
        ItemStack fuel = inv.getStackInSlot(SLOT_FUEL);
        return fuel == null || fuel.stackSize < 8;
    }

    @Override
    public float getTemperature() {
        return (float) boiler.getHeat();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("Items", data);

        boiler.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inv.readFromNBT("Items", data);

        boiler.readFromNBT(data);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (getOrientation() == side)
            return NO_SLOTS;
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == SLOT_LIQUID_OUTPUT;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return inv.removeStackFromSlot(index);
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.getSizeInventory(); i++) {
            setInventorySlotContents(i, null);
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case SLOT_FUEL:
                return FuelPlugin.getBurnTime(stack) > 0;
            case SLOT_LIQUID_INPUT:
                return Fluids.WATER.is(FluidContainerRegistry.getFluidForFilledItem(stack));
        }
        return false;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (isPowered() && Fluids.STEAM.is(resource))
            return fill(0, resource, doFill);
        if (Fluids.WATER.is(resource))
            return fill(1, resource, doFill);
        return 0;
    }

    private int fill(int tankIndex, FluidStack resource, boolean doFill) {
        if (tankIndex == 1)
            if (boiler.isSuperHeated() && Steam.BOILERS_EXPLODE) {
                FluidStack water = getTankManager().get(TANK_WATER).getFluid();
                if (water == null || water.amount <= 0)
                    explode();
            }
        return getTankManager().fill(tankIndex, resource, doFill);
    }

    public void explode() {
        explode = true;
    }
}
