/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.widgets;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ChargeIndicator extends IndicatorController {

    private double charge;
    private final double maxCharge;

    public ChargeIndicator(double maxEnergy) {
        this.maxCharge = maxEnergy;
    }

    @Override
    protected void refreshToolTip() {
        tip.text = String.format("%.0f%%", (charge / maxCharge) * 100.0);
    }

    @Override
    public int getScaledLevel(int size) {
        double e = Math.min(charge, maxCharge);
        return (int) (e * size / maxCharge);
    }

    public void setCharge(double energy) {
        this.charge = energy;
    }

    public void updateCharge(double energy) {
        this.charge = (this.charge * 9.0 + energy) / 10.0;
    }

}
