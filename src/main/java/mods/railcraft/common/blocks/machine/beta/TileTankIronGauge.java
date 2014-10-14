/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.Timer;
import net.minecraftforge.fluids.Fluid;
import java.util.Random;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankIronGauge extends TileTankBase {

    private int lightValue = 0;
    private final Timer timer = new Timer();

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineBeta.TANK_IRON_GAUGE;
    }

    @Override
    public IIcon getIcon(int side) {
        if (!isStructureValid() || getPattern() == null) {
            return getTextureFromMachine(side);
        }

        int px = getPatternPositionX();
        int py = getPatternPositionY();
        int pz = getPatternPositionZ();

        ForgeDirection s = ForgeDirection.getOrientation(side);
        char markerSide = getPattern().getPatternMarkerChecked(MiscTools.getXOnSide(px, s), MiscTools.getYOnSide(py, s), MiscTools.getZOnSide(pz, s));

        if (!isMapPositionOtherBlock(markerSide)) {
            return getTextureFromMachine(9);
        }

        if (s == ForgeDirection.UP || s == ForgeDirection.DOWN) {
            int markerTop = getPattern().getPatternMarkerChecked(px, py + 1, pz);
            if (markerTop == 'A' || markerTop == 'O') {
                int metaUp = worldObj.getBlockMetadata(xCoord, yCoord, zCoord - 1);
                int metaDown = worldObj.getBlockMetadata(xCoord, yCoord, zCoord + 1);
                return getTextureBasedOnNeighbors(metaUp, metaDown);
            }
            return getTextureFromMachine(0);
        }

        int metaUp = worldObj.getBlockMetadata(xCoord, yCoord + 1, zCoord);
        int metaDown = worldObj.getBlockMetadata(xCoord, yCoord - 1, zCoord);
        return getTextureBasedOnNeighbors(metaUp, metaDown);
    }

    private IIcon getTextureBasedOnNeighbors(int metaUp, int metaDown) {
        if (metaUp == getBlockMetadata() && metaDown == getBlockMetadata()) {
            return getTextureFromMachine(7);
        } else if (metaUp == getBlockMetadata()) {
            return getTextureFromMachine(8);
        } else if (metaDown == getBlockMetadata()) {
            return getTextureFromMachine(6);
        }
        return getTextureFromMachine(0);
    }

    private IIcon getTextureFromMachine(int index) {
        return getMachineType().getTexture(index);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random rand) {
        int oldLightValue = lightValue;
        if (timer.hasTriggered(worldObj, 80) && isStructureValid())
            updateLightValue();
        if (oldLightValue != lightValue)
            worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
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
