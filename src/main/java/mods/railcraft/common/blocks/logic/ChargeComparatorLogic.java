/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.common.blocks.interfaces.ITileCompare;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by CovertJaguar on 2/20/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeComparatorLogic extends ChargeLogic implements ITileCompare {

    public ChargeComparatorLogic(Adapter.Tile adapter, Charge network) {
        super(adapter, network);
    }

    private int prevComparatorOutput;

    @Override
    protected void updateServer() {
        super.updateServer();
        clock().onInterval(16, () -> {
            int newComparatorOutput = getComparatorInputOverride();
            if (prevComparatorOutput != newComparatorOutput)
                theWorldAsserted().updateComparatorOutputLevel(getPos(), adapter.tile().map(TileEntity::getBlockType).orElse(Blocks.AIR));
            prevComparatorOutput = newComparatorOutput;
        });
    }

    @Override
    public int getComparatorInputOverride() {
        return access().getComparatorOutput();
    }
}
