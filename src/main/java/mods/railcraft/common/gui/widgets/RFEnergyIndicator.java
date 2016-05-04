/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.widgets;

import cofh.api.energy.IEnergyHandler;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class RFEnergyIndicator extends IndicatorController {

    private int energy;
    private final int maxEnergy;

    public RFEnergyIndicator(IEnergyHandler energyHandler) {
        this.maxEnergy = energyHandler.getMaxEnergyStored(ForgeDirection.UNKNOWN);
    }

    public RFEnergyIndicator(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    @Override
    protected void refreshToolTip() {
        tip.text = String.format("%,d / %,d RF", energy, maxEnergy);
    }

    @Override
    public int getScaledLevel(int size) {
        double e = Math.min(energy, maxEnergy);
        return (int) (e * size / maxEnergy);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void updateEnergy(int energy) {
        this.energy = (int) ((this.energy * 9 + energy) / 10.0);
    }

}
