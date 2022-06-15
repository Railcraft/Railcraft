/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.widgets.IIndicatorController;
import mods.railcraft.common.gui.widgets.IndicatorController;
import mods.railcraft.common.modules.ModuleSteam;
import mods.railcraft.common.plugins.buildcraft.triggers.ITemperature;
import mods.railcraft.common.util.steam.IFuelProvider;
import mods.railcraft.common.util.steam.SteamConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BoilerLogic extends FurnaceLogic implements ITemperature {

    public final IIndicatorController heatIndicator = new HeatIndicator();
    public final StandardTank tankWater;
    public final StandardTank tankSteam;
    protected byte burnCycle;
    private double partialConversions;
    private double temp = SteamConstants.COLD_TEMP;
    private double maxTemp = SteamConstants.MAX_HEAT_LOW;
    private TileRailcraft tile;
    private IFuelProvider fuelProvider = new IFuelProvider() {};

    private BoilerData boilerData = BoilerData.EMPTY;

    public BoilerLogic(Adapter adapter) {
        super(adapter);

        tankWater = new FilteredTank(FluidTools.BUCKET_VOLUME * 6) {
            @Override
            public int fillInternal(@Nullable FluidStack resource, boolean doFill) {
                return super.fillInternal(
                        getLogic(ExploderLogic.class).map(exploderLogic -> {
                            if (!Fluids.isEmpty(resource)) {
                                if (isSuperHeated()) {
                                    FluidStack water = getTankWater().getFluid();
                                    if (Fluids.isEmpty(water)) {
                                        exploderLogic.primeToExplode();
                                        return null;
                                    }
                                }
                            }
                            return resource;
                        }).orElse(resource), doFill);
            }
        }.setFilterFluid(Fluids.WATER);
        tankSteam = new FilteredTank(FluidTools.BUCKET_VOLUME * 16)
                .setFilterFluid(Fluids.STEAM)
                .canFill(false);

        addLogic(new FluidLogic(adapter)
                .addTank(tankWater)
                .addTank(tankSteam)
        );
        addLogic(new BucketInteractionLogic(adapter));
    }

    @Override
    public void onStructureChanged(boolean isComplete, boolean isMaster, Object[] data) {
        super.onStructureChanged(isComplete, isMaster, data);
        if (isComplete) {
            boilerData = ((BoilerData) data[0]);
            tankSteam.setCapacity(FluidTools.BUCKET_VOLUME * boilerData.steam);
            tankWater.setCapacity(FluidTools.BUCKET_VOLUME * boilerData.water);
        } else boilerData = BoilerData.EMPTY;
    }

    @Override
    protected void updateServer() {
        clock().onInterval(4, () ->
                fuelProvider.manageFuel());


        burnCycle++;
        if (burnCycle >= boilerData.ticksPerCycle) {
            burnCycle = 0;
            if (isBurning())
                setBurnTime(getBurnTime() - getFuelPerCycle());
            while (!isBurning()) {
                boolean addedFuel = addFuel();
                if (!addedFuel)
                    break;
            }
            convertSteam();
        }

        if (isBurning())
            increaseTemp();
        else
            reduceTemp();

    }

    public StandardTank getTankWater() {
        return tankWater;
    }

    public StandardTank getTankSteam() {
        return tankSteam;
    }

    public BoilerData getBoilerData() {
        return boilerData;
    }

    public void setBoilerData(BoilerData boilerData) {
        this.boilerData = boilerData;
    }

    public BoilerLogic setFuelProvider(IFuelProvider fuelProvider) {
        this.fuelProvider = fuelProvider;
        return this;
    }

    public BoilerLogic setTile(TileRailcraft tile) {
        this.tile = tile;
        return this;
    }

    public double getMaxTemp() {
        return boilerData.maxHeat;
    }

    public double getHeatStep() {
        return SteamConstants.HEAT_STEP * fuelProvider.getThermalEnergyLevel();
    }

    public void reset() {
        temp = SteamConstants.COLD_TEMP;
    }

    @Override
    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
        if (this.temp < SteamConstants.COLD_TEMP)
            this.temp = SteamConstants.COLD_TEMP;
    }

    public double getHeatLevel() {
        return temp / getMaxTemp();
    }

    public void increaseTemp() {
        double max = getMaxTemp();
        if (temp == max)
            return;
        double step = getHeatStep();
        double change = step + (((max - temp) / max) * step * 3);
        change /= boilerData.numTanks;
        temp += change;
        temp = Math.min(temp, max);
    }

    public void reduceTemp() {
        if (temp == SteamConstants.COLD_TEMP)
            return;
        double step = SteamConstants.HEAT_STEP;
        double change = step + ((temp / getMaxTemp()) * step * 3);
        change /= boilerData.numTanks;
        temp -= change;
        temp = Math.max(temp, SteamConstants.COLD_TEMP);
    }

    @Override
    public boolean isHot() {
        return getTemp() >= SteamConstants.BOILING_POINT;
    }

    public boolean isSuperHeated() {
        return getTemp() >= SteamConstants.SUPER_HEATED;
    }

    @Override
    public boolean isBurning() {
        return getBurnTime() >= getFuelPerCycle();
    }

    @Override
    public boolean needsFuel() {
        FluidStack water = tankWater.getFluid();
        if (water == null || water.amount < tankWater.getCapacity() / 3)
            return true;
        return fuelProvider.needsFuel();
    }

    public boolean hasFuel() {
        return getBurnTime() > 0;
    }

    private boolean addFuel() {
        double fuel = fuelProvider.burnFuelUnit();
        if (fuel <= 0)
            return false;
        setBurnTime(getBurnTime() + fuel);
        setCurrentItemBurnTime(getBurnTime());
        return true;
    }

    public double getFuelPerCycle() {
        double fuel = SteamConstants.FUEL_PER_BOILER_CYCLE;
        fuel -= boilerData.numTanks * SteamConstants.FUEL_PER_BOILER_CYCLE * 0.0125F;
        fuel += SteamConstants.FUEL_HEAT_INEFFICIENCY * getHeatLevel();
        fuel += SteamConstants.FUEL_PRESSURE_INEFFICIENCY * (getMaxTemp() / SteamConstants.MAX_HEAT_HIGH);
        fuel *= boilerData.numTanks;
        fuel *= boilerData.efficiency;
        fuel *= ModuleSteam.config.fuelPerSteamMultiplier;
        return fuel;
    }

    public int convertSteam() {
        if (!isHot())
            return 0;

        partialConversions += boilerData.numTanks * getHeatLevel();
        int waterCost = (int) partialConversions;
        if (waterCost <= 0)
            return 0;
        partialConversions -= waterCost;

        FluidStack water = tankWater.drainInternal(waterCost, false);
        if (water == null)
            return 0;

        waterCost = Math.min(waterCost, water.amount);
        FluidStack steam = Fluids.STEAM.get(SteamConstants.STEAM_PER_UNIT_WATER * waterCost);
        if (steam == null)
            return 0;

        tankWater.drainInternal(waterCost, true);
        tankSteam.fillInternal(steam, true);

        return steam.amount;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setFloat("heat", (float) temp);
        data.setFloat("maxHeat", (float) maxTemp);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        temp = data.getFloat("heat");
        maxTemp = data.getFloat("maxHeat");
    }

    private class HeatIndicator extends IndicatorController {

        @Override
        protected void refreshToolTip() {
            tip.text = String.format("%.0f\u00B0C", getTemp());
        }

        @Override
        public double getMeasurement() {
            return (getTemp() - SteamConstants.COLD_TEMP) / (getMaxTemp() - SteamConstants.COLD_TEMP);
        }

        @Override
        public double getServerValue() {
            return getTemp();
        }

        @Override
        public double getClientValue() {
            return getTemp();
        }

        @Override
        public void setClientValue(double value) {
            setTemp(value);
        }
    }

    public static final class BoilerData {

        public static final BoilerData EMPTY = new BoilerData(0, 0, 1.0, 0f, 0, 0);

        public final int numTanks;
        public final int ticksPerCycle;
        private final double efficiency;
        public final float maxHeat;
        public final int water;
        public final int steam;

        public BoilerData(int tanks, int ticks, double efficiency, float heat, int water, int steam) {
            this.numTanks = tanks;
            this.ticksPerCycle = ticks;
            this.efficiency = efficiency;
            this.maxHeat = heat;
            this.water = water;
            this.steam = steam;
        }
    }
}
