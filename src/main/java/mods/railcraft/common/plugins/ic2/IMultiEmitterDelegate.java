/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.ic2;

import java.util.List;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IMultiEmitterDelegate extends IEmitterDelegate {

    List<TileEntity> getSubTiles();

}
