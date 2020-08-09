/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.blocks.interfaces.ITileCompare;
import mods.railcraft.common.blocks.logic.Logic.Adapter.Tile;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by CovertJaguar on 2/20/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FluidComparatorLogic extends Logic implements ITileCompare {
    private final int tankIndex;

    public FluidComparatorLogic(Tile adapter, int tankIndex) {
        super(adapter);
        this.tankIndex = tankIndex;
    }

    private int prevComparatorOutput;

    @Override
    protected void updateServer() {
        super.updateServer();
        if (clock(16)) {
            int newComparatorOutput = getComparatorInputOverride();
            if (prevComparatorOutput != newComparatorOutput)
                theWorldAsserted().updateComparatorOutputLevel(getPos(), adapter.tile().map(TileEntity::getBlockType).orElse(Blocks.AIR));
            prevComparatorOutput = newComparatorOutput;
        }
    }

    @Override
    public int getComparatorInputOverride() {
        return getLogic(FluidLogic.class).map(logic -> {
            FluidTank tank = logic.getTankManager().get(tankIndex);
            double fullness = (double) tank.getFluidAmount() / (double) tank.getCapacity();
            return (int) Math.ceil(fullness * 15.0);
        }).orElse(0);
    }
}
