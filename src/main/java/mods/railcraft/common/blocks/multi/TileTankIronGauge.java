/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.machine.interfaces.ITileLit;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static net.minecraft.util.EnumFacing.Axis.X;
import static net.minecraft.util.EnumFacing.Axis.Z;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankIronGauge<T extends TileTankBase<T, M>, M extends TileTankBase<M, M>> extends TileTankBase<T, M> implements ITileLit {

    private int lightValue = 0;
    private final Timer timer = new Timer();

    @SuppressWarnings("unchecked")
    @Override
    protected Class<T> defineSelfClass() {
        return (Class<T>) (Class<?>) TileTankIronGauge.class; // Intellij bug
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random rand) {
        if (!isStructureValid())
            return;
        int oldLightValue = lightValue;
        if (timer.hasTriggered(world, 80))
            updateLightValue();
        if (oldLightValue != lightValue)
            world.checkLightFor(EnumSkyBlock.BLOCK, getPos());
    }

    @Override
    public int getLightValue() {
        return lightValue;
    }

    private void updateLightValue() {
        Fluid fluid = getTank().getFluidType();
        lightValue = fluid != null ? fluid.getLuminosity() : 0;
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        if (!isStructureValid()) {
            return base;
        }

        boolean upConnected = WorldPlugin.getBlock(world, this.pos.offset(EnumFacing.UP)) == getBlockType();
        boolean downConnected = WorldPlugin.getBlock(world, this.pos.offset(EnumFacing.DOWN)) == getBlockType();

        if (upConnected) {
            if (downConnected) {
                base = base.withProperty(BlockTankIronGauge.POSITION, BlockTankIronGauge.ColumnPosition.MIDDLE);
            } else {
                base = base.withProperty(BlockTankIronGauge.POSITION, BlockTankIronGauge.ColumnPosition.BOTTOM);
            }
        } else {
            if (downConnected) {
                base = base.withProperty(BlockTankIronGauge.POSITION, BlockTankIronGauge.ColumnPosition.TOP);
            } else {
                base = base.withProperty(BlockTankIronGauge.POSITION, BlockTankIronGauge.ColumnPosition.SINGLE);
            }
        }

        char c = getPattern().getPatternMarkerChecked(getPatternPosition().north());
        base = base.withProperty(BlockTankIronGauge.AXIS, c == 'A' || c == MultiBlockPattern.EMPTY_PATTERN ? X : Z);

        return base;
    }
}
