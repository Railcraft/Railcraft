/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IBatteryBlock;
import mods.railcraft.common.util.misc.Game;

/**
 * This class exists to so that there is a ChargeLogic which is not assignable to ChargeComparatorLogic.
 *
 * Created by CovertJaguar on 2/20/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeSourceLogic extends ChargeLogic {

    public ChargeSourceLogic(Adapter.Tile adapter, Charge network) {
        super(adapter, network);
    }

    @Override
    public void onStructureChanged(boolean isComplete, boolean isMaster, Object[] data) {
        super.onStructureChanged(isComplete, isMaster, data);
        enableSource(isMaster);
    }

    public void enableSource(boolean enabled) {
        if (Game.isHost(theWorldAsserted()))
            if (enabled) {
                getBattery().setState(IBatteryBlock.State.SOURCE);
            } else {
                getBattery().setState(IBatteryBlock.State.DISABLED);
            }
    }

}
