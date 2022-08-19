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
import mods.railcraft.common.plugins.buildcraft.triggers.ITemperature;
import mods.railcraft.common.util.steam.SteamConstants;
import net.minecraft.nbt.NBTTagCompound;
//import net.minecraftforge.fluids.FluidStack;

public class DieselMotorLogic extends Logic implements ITemperature {

    public final IIndicatorController heatIndicator = new HeatIndicator();
    public final StandardTank tankDiesel;
    public static final int TICKS_PER_CYCLE = 10;
    private double temp = SteamConstants.COLD_TEMP;
    private double maxTemp = SteamConstants.MAX_HEAT_LOW;
    private static final int maxConsumption = 10;
    private int consumption = 0;
    protected byte burnCycle;

    private TileRailcraft tile;
    private DieselMotorData motorData = DieselMotorData.EMPTY;

    public DieselMotorLogic(Adapter adapter) {
        super(adapter);

        tankDiesel = new FilteredTank(FluidTools.BUCKET_VOLUME * 16)
                .setFilterFluid(Fluids.DIESEL);

        addLogic(new FluidLogic(adapter)
                .addTank(tankDiesel));
        addLogic(new BucketInteractionLogic(adapter));
    }

    @Override
    public void onStructureChanged(boolean isComplete, boolean isMaster, Object[] data) {
        super.onStructureChanged(isComplete, isMaster, data);
        if (isComplete) {
            motorData = ((DieselMotorData) data[0]);
            tankDiesel.setCapacity(FluidTools.BUCKET_VOLUME * motorData.diesel);
        } else
            motorData = DieselMotorData.EMPTY;
    }

    @Override
    protected void updateServer() {
        /**
         * speed  consumption
         * idle   0.5 mB/sec
         *  1       4 mB/sec
         *  2       8 mB/sec
         *  3      12 mB/sec
         *  4      16 mB/sec
         * 
         *  this is calculated to reach 10 km of distance at speed 4 (10 m/s) 
         *  with tank size 16 buckets
         */

        if (consumption > 0) {
            burnCycle++;
            //idle mode should use 1 mB per 2 seconds
            if (burnCycle >= TICKS_PER_CYCLE * (consumption == 1 ? 4 : 1)) {
                burnCycle = 0;
                //FluidStack diesel = 
                tankDiesel.drainInternal(consumption, true);
                // if (diesel == null) {
                //     //shut down engine
                //     this.consumption = 0;
                //     return;
                // }
                // increaseTemp();
            }
        } 
        else{
            // reduceTemp();
        }

    }

    public StandardTank getTankDiesel() {
        return tankDiesel;
    }

    public DieselMotorData getMotorData() {
        return motorData;
    }

    public void setMotorData(DieselMotorData motorData) {
        this.motorData = motorData;
    }

    public DieselMotorLogic setTile(TileRailcraft tile) {
        this.tile = tile;
        return this;
    }

    public double getMaxTemp() {
        return motorData.maxHeat;
    }

    public void reset() {
        temp = SteamConstants.COLD_TEMP;
        consumption = 0;
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

    public boolean isRunning() {
        if (this.consumption > 0 && !this.tankDiesel.isEmpty()) {
            return true;
        }
        return false;
    }

    public int getConsumption(){
        return consumption;
    }

    public void setConsumption(int consumption) {
        this.consumption = consumption;
        if (this.consumption < 0)
            this.consumption = 0;
        if (this.consumption > maxConsumption)
            this.consumption = maxConsumption;
    }

    // public double getHeatLevel() {
    //     return temp / getMaxTemp();
    // }

    // public void increaseTemp() {
    //     double max = getMaxTemp();
    //     if (temp == max)
    //         return;
    //     double step = getHeatStep();
    //     double change = step + (((max - temp) / max) * step * 3);
    //     change /= boilerData.numTanks;
    //     temp += change;
    //     temp = Math.min(temp, max);
    // }

    // public void reduceTemp() {
    //     if (temp == SteamConstants.COLD_TEMP)
    //         return;
    //     double step = SteamConstants.HEAT_STEP;
    //     double change = step + ((temp / getMaxTemp()) * step * 3);
    //     change /= boilerData.numTanks;
    //     temp -= change;
    //     temp = Math.max(temp, SteamConstants.COLD_TEMP);
    // }

    // @Override
    // public boolean isHot() {
    //     return getTemp() >= SteamConstants.BOILING_POINT;
    // }

    // public boolean isSuperHeated() {
    //     return getTemp() >= SteamConstants.SUPER_HEATED;
    // }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setFloat("heat", (float) temp);
        data.setFloat("maxHeat", (float) maxTemp);
        data.setInteger("consumption", consumption);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        temp = data.getFloat("heat");
        maxTemp = data.getFloat("maxHeat");
        consumption = data.getInteger("consumption");
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

    public static final class DieselMotorData {

        public static final DieselMotorData EMPTY = new DieselMotorData(0, 0f, 0, 0);

        public final int ticksPerCycle;
        public final float maxHeat;
        public final int diesel;
        public final int consumption;

        public DieselMotorData(int ticks, float heat, int diesel, int consumption) {
            this.ticksPerCycle = ticks;
            this.maxHeat = heat;
            this.diesel = diesel;
            this.consumption = consumption;
        }
    }
}
