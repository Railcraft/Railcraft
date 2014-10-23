/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EngineTools {

    public static boolean isPoweredTile(TileEntity tile, ForgeDirection side) {
        if(tile instanceof IEnergyHandler){
            IEnergyHandler handler = (IEnergyHandler)tile;
            return handler.canConnectEnergy(side);
        }
        return false;
    }

}
