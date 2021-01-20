/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2021
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.widgets;

import buildcraft.api.mj.MjAPI;
import buildcraft.api.mj.MjBattery;

public class MJEnergyIndicator extends IndicatorController {

    protected final MjBattery energyStorage;
    protected long energy;

    public MJEnergyIndicator(MjBattery energyStorage) {
        this.energyStorage = energyStorage;
    }

    @Override
    public double getMeasurement() {
        double e = Math.min(energy, energyStorage.getCapacity());
        return e / energyStorage.getCapacity();
    }

    @Override
    public void setClientValue(double value) {
        energy = Math.round(value);
    }

    @Override
    public double getClientValue() {
        return energy;
    }

    @Override
    public double getServerValue() {
        return energyStorage.getStored();
    }

    @Override
    protected void refreshToolTip() {
        tip.text = String.format("%,d / %,d MJ", energy / MjAPI.MJ, energyStorage.getCapacity() / MjAPI.MJ);
    }
}
