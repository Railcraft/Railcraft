/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.blocks.interfaces.ITileLit;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankIronGauge extends TileTankMetal implements ITileLit {

    private int lightValue;
    private final Timer timer = new Timer();

    @Override
    public TankDefinition getTankDefinition() {
        return TankDefinition.IRON;
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
        StandardTank tank = getTank();
        if (tank == null) {
            lightValue = 0;
        } else {
            Fluid fluid = getTank().getFluidType();
            lightValue = fluid != null ? fluid.getLuminosity() : 0;
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state) {
        state = super.getActualState(state);
        if (!isStructureValid()) {
            return state;
        }

        EnumSet<BlockStrengthGlass.Position> neighbors = EnumSet.noneOf(BlockStrengthGlass.Position.class);

        if (WorldPlugin.getBlockState(world, pos.up()) == state)
            neighbors.add(BlockStrengthGlass.Position.TOP);

        if (WorldPlugin.getBlockState(world, pos.down()) == state)
            neighbors.add(BlockStrengthGlass.Position.BOTTOM);

        state = state.withProperty(BlockTankIronGauge.POSITION, BlockStrengthGlass.Position.patterns.get(neighbors));
        return state;
    }
}
