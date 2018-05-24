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
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Timer;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankIronGauge extends TileTankBase implements ITileLit {

    private int lightValue = 0;
    private final Timer timer = new Timer();

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

    @NotNull
    @Override
    public EnumGui getGui() {
        return EnumGui.TANK;
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        if (!isStructureValid()) {
            return base.getBlock().getDefaultState();
        }
        for (Map.Entry<EnumFacing, PropertyEnum<BlockTankIronGauge.RenderState>> entry : BlockTankIronGauge.TOUCHES.entrySet()) {
            EnumFacing face = entry.getKey();
            PropertyEnum<BlockTankIronGauge.RenderState> property = entry.getValue();
            if (WorldPlugin.getBlock(world, getPos().offset(face)) == getBlockType()) {
                base = base.withProperty(property, BlockTankIronGauge.RenderState.TRANSPARENT);
                continue;
            }

            if (face.getAxis() == EnumFacing.Axis.Y) {
                base = base.withProperty(property, BlockTankIronGauge.RenderState.DEFAULT);
            } else {
                boolean upConnected = WorldPlugin.getBlock(world, this.pos.offset(EnumFacing.UP)) == getBlockType();
                boolean downConnected = WorldPlugin.getBlock(world, this.pos.offset(EnumFacing.DOWN)) == getBlockType();
                if (upConnected) {
                    if (downConnected) {
                        base = base.withProperty(property, BlockTankIronGauge.RenderState.MIDDLE);
                    } else {
                        base = base.withProperty(property, BlockTankIronGauge.RenderState.BOTTOMMOST);
                    }
                } else {
                    if (downConnected) {
                        base = base.withProperty(property, BlockTankIronGauge.RenderState.TOPMOST);
                    } else {
                        base = base.withProperty(property, BlockTankIronGauge.RenderState.DEFAULT);
                    }
                }
            }
        }
        return base;
    }
}
