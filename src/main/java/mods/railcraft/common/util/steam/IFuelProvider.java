/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.steam;

import mods.railcraft.api.fuel.INeedsFuel;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IFuelProvider extends INeedsFuel {

    default double burnFuelUnit() {return 0.0;}

    default void manageFuel() {}

    default double getThermalEnergyLevel() {return 1.0;}

    @Override
    default boolean needsFuel() {return false;}
}
