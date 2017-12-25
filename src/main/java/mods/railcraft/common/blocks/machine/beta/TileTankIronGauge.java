/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.blocks.machine.interfaces.ITileLit;
import mods.railcraft.common.util.misc.Timer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankIronGauge extends TileTankBase implements ITileLit {

    private int lightValue = 0;
    private final Timer timer = new Timer();

//    @Override
//    public EnumMachineBeta getMachineType() {
//        return EnumMachineBeta.TANK_IRON_GAUGE;
//    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random rand) {
        int oldLightValue = lightValue;
        if (timer.hasTriggered(worldObj, 80) && isStructureValid())
            updateLightValue();
        if (oldLightValue != lightValue)
            worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPos());
    }

    @Override
    public int getLightValue() {
        return lightValue;
    }

    private void updateLightValue() {
        Fluid fluid = getTank().getFluidType();
        lightValue = fluid != null ? fluid.getLuminosity() : 0;
    }
}
