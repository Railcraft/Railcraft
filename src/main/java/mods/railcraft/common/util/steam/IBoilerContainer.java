/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.steam;

import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.buildcraft.triggers.ITemperature;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 9/10/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IBoilerContainer extends ITemperature, INeedsFuel {

    @Nullable
    SteamBoiler getBoiler();

    void explode();

    static void onFillWater(IBoilerContainer tileBoiler) {
        SteamBoiler boiler = tileBoiler.getBoiler();
        if (boiler != null && boiler.isSuperHeated() && Steam.BOILERS_EXPLODE) {
            FluidStack water = boiler.getTankWater().getFluid();
            if (Fluids.isEmpty(water)) {
                boiler.setHeat(Steam.SUPER_HEATED - 1);
                tileBoiler.explode();
            }
        }
    }
}
