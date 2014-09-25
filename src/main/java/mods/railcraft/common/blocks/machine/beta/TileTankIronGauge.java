/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.util.misc.MiscTools;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankIronGauge extends TileTankBase {

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
}
