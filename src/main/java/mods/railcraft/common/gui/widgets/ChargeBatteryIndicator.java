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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ChargeBatteryIndicator extends IndicatorController {

    private double charge;
    private final IChargeBattery battery;

    public ChargeBatteryIndicator(IChargeBattery battery) {
        this.battery = battery;
    }

    @SideOnly(Side.CLIENT)
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
