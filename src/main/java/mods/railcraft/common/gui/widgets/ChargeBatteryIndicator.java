/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

import mods.railcraft.api.charge.IBattery;
import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
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
        double current = Math.min(charge, capacity);
        double chargeLevel = capacity <= 0.0 ? 0.0 : (current / capacity) * 100.0;
        tips.clear();
        tips.add(String.format("%.0f%%", chargeLevel));
        tips.add(HumanReadableNumberFormatter.format(current));
        tips.add("/ " + HumanReadableNumberFormatter.format(capacity));
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
    public double getClientValue() {
        return charge;
    }

    @Override
    public void setClientValue(double value) {
        charge = value;
    }

}
