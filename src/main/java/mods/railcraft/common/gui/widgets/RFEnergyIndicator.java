/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

import net.minecraftforge.energy.EnergyStorage;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class RFEnergyIndicator extends IndicatorController {

    private final EnergyStorage energyStorage;
    private int rf;

    public RFEnergyIndicator(EnergyStorage energyStorage) {
        this.energyStorage = energyStorage;
    }

    @Override
    protected void refreshToolTip() {
        tip.text = String.format("%,d / %,d RF", rf, energyStorage.getMaxEnergyStored());
    }

    @Override
    public double getMeasurement() {
        double e = Math.min(rf, energyStorage.getMaxEnergyStored());
        return e / energyStorage.getMaxEnergyStored();
    }

    @Override
    public void setClientValue(double value) {
        rf = (int) value;
    }

    @Override
    public double getServerValue() {
        return energyStorage.getEnergyStored();
    }
}
