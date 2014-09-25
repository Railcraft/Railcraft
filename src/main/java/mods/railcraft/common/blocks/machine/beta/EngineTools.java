/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EngineTools {

    public static boolean isPoweredTile(TileEntity tile, ForgeDirection side) {
        if (tile instanceof IPowerReceptor) {
            IPowerReceptor receptor = (IPowerReceptor) tile;
            PowerReceiver provider = receptor.getPowerReceiver(side);

            return provider instanceof PowerReceiver;
        }

        return false;
    }

}
