/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.steam;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.widgets.IIndicatorController;
import mods.railcraft.common.gui.widgets.IndicatorController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class SteamBoiler {

    public double burnTime;
    public double currentItemBurnTime;
    protected boolean isBurning = false;
    protected byte burnCycle;
    private final StandardTank tankWater;
    private final StandardTank tankSteam;
    private double partialConversions = 0;
    private double heat = Steam.COLD_TEMP;
    private double maxHeat = Steam.MAX_HEAT_LOW;
    private double efficiencyModifier = 1;
    private int ticksPerCycle = 16;
    private RailcraftTileEntity tile;
    private IFuelProvider fuelProvider;

    public SteamBoiler(StandardTank tankWater, StandardTank tankSteam) {
        this.tankWater = tankWater;
        this.tankSteam = tankSteam;
    }

    public SteamBoiler setFuelProvider(IFuelProvider fuelProvider) {
        this.fuelProvider = fuelProvider;
        return this;
    }

    public SteamBoiler setTile(RailcraftTileEntity tile) {
        this.tile = tile;
        return this;
    }

    public SteamBoiler setTicksPerCycle(int ticks) {
        this.ticksPerCycle = ticks;
        return this;
    }

    public SteamBoiler setEfficiencyModifier(double modifier) {
        this.efficiencyModifier = modifier;
        return this;
    }

    public SteamBoiler setMaxHeat(double maxHeat) {
        this.maxHeat = maxHeat;
        return this;
    }

    public double getMaxHeat() {
        return maxHeat;
    }

    public double getHeatStep() {
        if (fuelProvider != null)
            return fuelProvider.getHeatStep();
        return Steam.HEAT_STEP;
    }

    public void reset() {
        heat = Steam.COLD_TEMP;
    }

    public double getHeat() {
        return heat;
    }

    public void setHeat(double heat) {
        this.heat = heat;
        if (this.heat < Steam.COLD_TEMP)
            this.heat = Steam.COLD_TEMP;
    }

    public double getHeatLevel() {
        return heat / getMaxHeat();
    }

    public void increaseHeat(int numTanks) {
        double max = getMaxHeat();
        if (heat == max)
            return;
        double step = getHeatStep();
        double change = step + (((max - heat) / max) * step * 3);
        change /= numTanks;
        heat += change;
        heat = Math.min(heat, max);
    }

    public void reduceHeat(int numTanks) {
        if (heat == Steam.COLD_TEMP)
            return;
        double step = Steam.HEAT_STEP;
        double change = step + ((heat / getMaxHeat()) * step * 3);
        change /= numTanks;
        heat -= change;
        heat = Math.max(heat, Steam.COLD_TEMP);
    }

    public boolean isBoiling() {
        return getHeat() >= Steam.BOILING_POINT;
    }

    public boolean isSuperHeated() {
        return getHeat() >= Steam.SUPER_HEATED;
    }

    public boolean isBurning() {
        return isBurning;
    }

    public void setBurning(boolean isBurning) {
        this.isBurning = isBurning;
    }

    public boolean hasFuel() {
        return burnTime > 0;
    }

    public int getBurnProgressScaled(int i) {
        if (!isBoiling())
            return 0;
        int scale = (int) ((burnTime / currentItemBurnTime) * i);
        scale = Math.max(0, scale);
        scale = Math.min(i, scale);
        return scale;
    }

    private boolean addFuel() {
        if (fuelProvider == null)
            return false;
        double fuel = fuelProvider.getMoreFuel();
        if (fuel <= 0)
            return false;
        burnTime += fuel;
        currentItemBurnTime = burnTime;
        return true;
    }

    public double getFuelPerCycle(int numTanks) {
        double fuel = Steam.FUEL_PER_BOILER_CYCLE;
        fuel -= numTanks * Steam.FUEL_PER_BOILER_CYCLE * 0.0125F;
        fuel += Steam.FUEL_HEAT_INEFFICIENCY * getHeatLevel();
        fuel += Steam.FUEL_PRESSURE_INEFFICIENCY * (getMaxHeat() / Steam.MAX_HEAT_HIGH);
        fuel *= numTanks;
        fuel *= efficiencyModifier;
        fuel *= RailcraftConfig.fuelPerSteamMultiplier();
        return fuel;
    }

    public void tick(int numTanks) {
        burnCycle++;
        if (burnCycle >= ticksPerCycle) {
            burnCycle = 0;
            double fuelNeeded = getFuelPerCycle(numTanks);
            while (burnTime < fuelNeeded) {
                boolean addedFuel = addFuel();
                if (!addedFuel)
                    break;
            }
            boolean wasBurning = isBurning;
            isBurning = burnTime >= fuelNeeded;
            if (isBurning)
                burnTime -= fuelNeeded;
            if (tile != null && isBurning != wasBurning)
                tile.sendUpdateToClient();
            convertSteam(numTanks);
        }

        if (isBurning)
            increaseHeat(numTanks);
        else
            reduceHeat(numTanks);
    }

    public int convertSteam(int numTanks) {
        if (!isBoiling())
            return 0;

        partialConversions += numTanks * getHeatLevel();
        int waterCost = (int) partialConversions;
        if (waterCost <= 0)
            return 0;
        partialConversions -= waterCost;

        FluidStack water = tankWater.drain(waterCost, false);
        if (water == null)
            return 0;

        waterCost = Math.min(waterCost, water.amount);
        FluidStack steam = Fluids.STEAM.get(Steam.STEAM_PER_UNIT_WATER * waterCost);
        if (steam == null)
            return 0;

        tankWater.drain(waterCost, true);
        tankSteam.fill(steam, true);

        return steam.amount;
    }

    public void writeToNBT(NBTTagCompound data) {
        data.setFloat("heat", (float) heat);
        data.setFloat("maxHeat", (float) maxHeat);
        data.setFloat("burnTime", (float) burnTime);
        data.setFloat("currentItemBurnTime", (float) currentItemBurnTime);
    }

    public void readFromNBT(NBTTagCompound data) {
        heat = data.getFloat("heat");
        maxHeat = data.getFloat("maxHeat");
        burnTime = data.getFloat("burnTime");
        currentItemBurnTime = data.getFloat("currentItemBurnTime");
    }

    public final IIndicatorController heatIndicator = new HeatIndicator();

    private class HeatIndicator extends IndicatorController {

        @Override
        protected void refreshToolTip() {
            tip.text = String.format("%.0f\u00B0C", getHeat());
        }

        @Override
        public int getScaledLevel(int size) {
            return (int) ((getHeat() - Steam.COLD_TEMP) * size / (getMaxHeat() - Steam.COLD_TEMP));
        }

    };
}
