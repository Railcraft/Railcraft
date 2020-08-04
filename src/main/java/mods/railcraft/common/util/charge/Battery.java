/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.charge;

import mods.railcraft.api.charge.IBattery;

/**
 * Created by CovertJaguar on 1/15/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Battery implements IBattery {
    protected final double capacity;
    protected double charge;
    protected double chargeDrawnThisTick;
//    protected double chargeAddedThisTick;

    public Battery(double capacity) {this.capacity = capacity;}

    @Override
    public double getCharge() {
        return charge;
    }

    @Override
    public void setCharge(double charge) {
        this.charge = charge;
    }

    @Override
    public double getCapacity() {
        return capacity;
    }

    @Override
    public void addCharge(double charge) {
        this.charge += charge;
//        chargeAddedThisTick += charge;
    }

    /**
     * Remove up to the requested amount of charge and returns the amount
     * removed.
     * <p/>
     *
     * @return charge removed
     */
    @Override
    public double removeCharge(double request) {
        double amountToDraw = Math.min(request, getAvailableCharge());
        charge -= amountToDraw / getEfficiency();
        chargeDrawnThisTick += amountToDraw;
        return amountToDraw;
    }
}
