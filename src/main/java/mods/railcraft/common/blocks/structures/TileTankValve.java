/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.blocks.logic.ValveLogic;
import mods.railcraft.common.blocks.structures.BlockTankMetalValve.OptionalAxis;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileTankValve extends TileTank {

    protected TileTankValve() {
        getLogic(StructureLogic.class).ifPresent(logic -> logic.addSubLogic(new ValveLogic(Logic.Adapter.of(this))));
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return getLogic(StructureLogic.class)
                .filter(StructureLogic::isStructureValid)
                .map(logic ->
                        Arrays.stream(EnumFacing.VALUES)
                                .filter(facing -> logic.isMapPositionOtherBlock(requireNonNull(logic.getPattern()).getPatternMarkerChecked(logic.getPatternPosition().offset(facing))))
                                .findFirst()
                                .map(facing -> base.withProperty(BlockTankIronValve.OPTIONAL_AXIS, OptionalAxis.from(facing.getAxis())))
                                .orElse(base)).orElse(base);
    }
}
