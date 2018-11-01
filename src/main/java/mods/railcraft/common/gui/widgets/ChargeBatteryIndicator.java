/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

import mods.railcraft.api.charge.IBattery;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ChargeBatteryIndicator extends IndicatorController {

    private double charge;
    private final IBattery battery;

    public ChargeBatteryIndicator(IBattery battery) {
        this.battery = battery;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void refreshToolTip() {
        double capacity = battery.getCapacity();
        double chargeLevel = capacity <= 0.0 ? 0.0 : (Math.min(charge, capacity) / capacity) * 100.0;
        tip.text = String.format("%.0f%%", chargeLevel);
    }

    @Override
    public double getMeasurement() {
        double capacity = battery.getCapacity();
        if (capacity <= 0.0)
            return 0.0;
        return Math.min(charge, capacity) / capacity;
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
