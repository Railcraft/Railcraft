/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.steam;

import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.buildcraft.triggers.ITemperature;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 9/10/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IBoilerContainer extends ITemperature, INeedsFuel {

    @Nullable
    SteamBoiler getBoiler();

    void steamExplosion(FluidStack resource);

    default @Nullable FluidStack onFillWater(@Nullable FluidStack resource) {
        if (!Fluids.isEmpty(resource)) {
            SteamBoiler boiler = getBoiler();
            if (boiler != null && boiler.isSuperHeated()) {
                FluidStack water = boiler.getTankWater().getFluid();
                if (Fluids.isEmpty(water)) {
                    steamExplosion(resource);
                    return null;
                }
            }
        }
        return resource;
    }
}
