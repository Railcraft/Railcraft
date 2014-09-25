/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.ic2;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface ISinkDelegate {

    double getDemandedEnergy();

    double injectEnergy(ForgeDirection directionFrom, double amount);

    int getSinkTier();
    
    boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction);

    TileEntity getTile();

}
