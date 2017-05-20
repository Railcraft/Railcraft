/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

import mods.railcraft.api.charge.IChargeBattery;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ChargeIndicator extends IndicatorController {

    private double charge;
    private final IChargeBattery battery;

    public ChargeIndicator(IChargeBattery battery) {
        this.battery = battery;
    }

    @Override
    protected void refreshToolTip() {
        tip.text = String.format("%.0f%%", (charge / battery.getCapacity()) * 100.0);
    }

    @Override
    public double getMeasurement() {
        return Math.min(charge, battery.getCapacity()) / battery.getCapacity();
    }

    @Override
    public double getServerValue() {
        return battery.getCharge();
    }

    @Override
    public void setClientValue(double value) {
        charge = value;
    }

}
