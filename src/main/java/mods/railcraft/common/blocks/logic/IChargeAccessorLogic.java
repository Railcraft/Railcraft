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
import mods.railcraft.api.charge.IBatteryBlock;

import java.util.Optional;

/**
 * Created by CovertJaguar on 8/3/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IChargeAccessorLogic extends ILogicContainer {

    default Optional<? extends IBatteryBlock> getBattery() {
        return getLogic(ChargeSourceLogic.class)
                .flatMap(source -> source.access().getBattery());
    }

    default double getCharge() {
        return getBattery().map(IBattery::getCharge).orElse(0.0);
    }

    default double removeCharge(double desired) {
        return getBattery().map(bat -> bat.removeCharge(desired)).orElse(0.0);
    }

    default double getAvailableCharge() {
        return getBattery().map(IBattery::getAvailableCharge).orElse(0.0);
    }
}
