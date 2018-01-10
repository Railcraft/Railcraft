/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.multi;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface MetalTank {

    String getTitle();

    boolean isTankBlock(IBlockState meta);

    boolean isWallBlock(IBlockState meta);

    float getResistance(Entity exploder);

}
