/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.interfaces.IDropsInv;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.items.ItemTurbineRotor;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by CovertJaguar on 8/1/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SteamTurbineLogic extends FluidLogic implements IChargeAccessorLogic, IDropsInv {
    public static final int CHARGE_OUTPUT = 225;
    private static final int STEAM_USAGE = 360;
    public static final int TANK_STEAM = 0;
    public static final int TANK_WATER = 1;
    protected final StandardTank tankSteam;
    protected final StandardTank tankWater;
    private final InventoryAdvanced inv = new InventoryAdvanced(1);
    public double operatingRatio;
    private double energy;

    public SteamTurbineLogic(Adapter adapter) {
        super(adapter);
    }

    {
        TileRailcraft tile = adapter.tile().orElse(null);
        tankSteam = new FilteredTank(FluidTools.BUCKET_VOLUME * 4, tile).setFilterFluid(Fluids.STEAM).canDrain(false);
        tankWater = new FilteredTank(FluidTools.BUCKET_VOLUME * 4, tile).setFilterFluid(Fluids.WATER).canFill(false);
        addTank(tankSteam);
        addTank(tankWater);
        if (tile != null)
            inv.callbackTile(tile);
    }

    @Override
    protected void updateServer() {
        super.updateServer();
        boolean addedEnergy = false;
        if (energy < CHARGE_OUTPUT) {
            FluidStack steam = tankSteam.drainInternal(STEAM_USAGE, false);
//                if(steam != null) System.out.println("steam=" + steam.amount);
            if (steam != null && steam.amount >= STEAM_USAGE) {
                ItemStack rotor = inv.getStackInSlot(0);
                if (RailcraftItems.TURBINE_ROTOR.isEqual(rotor) /*&& rotor.getItemDamage() < rotor.getMaxDamage() - 5*/) {
                    addedEnergy = true;
                    energy += CHARGE_OUTPUT;
                    tankSteam.drainInternal(STEAM_USAGE, true);
                    tankWater.fillInternal(Fluids.WATER.get(2), true);

                    inv.setInventorySlotContents(0, ((ItemTurbineRotor) rotor.getItem()).useRotor(rotor));
                }
            }
        }

        double thisTick = addedEnergy ? 1.0 : 0.0;
        operatingRatio = (thisTick - operatingRatio) * 0.05 + operatingRatio;

//                System.out.println("output=" + output);
//                System.out.println("addedEnergy=" + addedEnergy);
        if (clock(4)) {
            adapter.tile().map(TileEntity::getBlockType).ifPresent(block ->
                    WorldPlugin.addBlockEvent(theWorldAsserted(), getPos(), block, 1, (byte) (operatingRatio * 100.0)));
        }

        getBattery().ifPresent(battery -> {
            if (battery.needsCharging()) {
                battery.addCharge(energy);
                energy = 0;
            }
        });
    }

    public IInventory getInventory() {
        return inv;
    }

    @Override
    public void spewInventory(World world, BlockPos pos) {
        InvTools.spewInventory(inv, world, getPos());
    }

    public boolean needsMaintenance() {
        ItemStack rotor = inv.getStackInSlot(0);
        if (InvTools.isEmpty(rotor))
            return true;
        if (!RailcraftItems.TURBINE_ROTOR.isEqual(rotor))
            return true;
        return rotor.getItemDamage() / (double) rotor.getMaxDamage() > 0.75f;
    }

    public double readGauge(double previousValue) {
        return (previousValue * 14.0 + operatingRatio) / 15.0;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("rotor", data);
        energy = data.getFloat("energy");
        operatingRatio = data.getDouble("operatingRatio");
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inv.readFromNBT("rotor", data);
        data.setFloat("energy", (float) energy);
        data.setDouble("operatingRatio", operatingRatio);
    }
}
