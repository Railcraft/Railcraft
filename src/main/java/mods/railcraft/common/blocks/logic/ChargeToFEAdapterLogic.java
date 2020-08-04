/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.charge.IBattery;
import mods.railcraft.common.plugins.forge.EnergyPlugin;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Created by CovertJaguar on 8/1/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeToFEAdapterLogic extends Logic implements IEnergyStorage, IChargeAccessorLogic {
    private final int maxOutput;

    public ChargeToFEAdapterLogic(Adapter adapter, int maxExtract) {
        super(adapter);
        this.maxOutput = maxExtract;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return getBattery().map(battery -> {
            double desired = EnergyPlugin.forgeEnergyToCharge(Math.min(maxExtract, maxOutput));
            if (!simulate)
                return battery.removeCharge(desired);
            return Math.min(desired, battery.getAvailableCharge());
        }).map(EnergyPlugin::chargeToForgeEnergy).orElse(0);
    }

    @Override
    public int getEnergyStored() {
        return EnergyPlugin.chargeToForgeEnergy(getCharge());
    }

    @Override
    public int getMaxEnergyStored() {
        return getBattery().map(IBattery::getCharge).map(EnergyPlugin::chargeToForgeEnergy).map(MathHelper::floor).orElse(0);
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
