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
public class MJEnergyIndicator extends IndicatorController {

    private double energy;
    private final double maxEnergy;

    public MJEnergyIndicator(double maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    @Override
    protected void refreshToolTip() {
        tip.text = String.format("%.0f MJ", energy);
    }

    @Override
    public int getScaledLevel(int size) {
        double e = Math.min(energy, maxEnergy);
        return (int) (e * size / maxEnergy);
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void updateEnergy(double energy) {
        this.energy = (this.energy * 9.0 + energy) / 10.0;
    }

}