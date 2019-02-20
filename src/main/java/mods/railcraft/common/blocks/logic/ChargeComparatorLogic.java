/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.charge.Charge;

import java.util.Objects;

/**
 * Created by CovertJaguar on 2/20/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeComparatorLogic extends ChargeLogic {

    public ChargeComparatorLogic(Adapter.Tile adapter, Charge network) {
        super(adapter, network);
    }

    private int prevComparatorOutput;

    @Override
    protected void updateServer() {
        super.updateServer();
        if (clock(16)) {
            int newComparatorOutput = access().getComparatorOutput();
            if (prevComparatorOutput != newComparatorOutput)
                theWorldAsserted().updateComparatorOutputLevel(getPos(), Objects.requireNonNull(adapter.tile()).getBlockType());
            prevComparatorOutput = newComparatorOutput;
        }
    }

}
