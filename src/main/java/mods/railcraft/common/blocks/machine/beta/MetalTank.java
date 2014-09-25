/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import net.minecraft.entity.Entity;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class MetalTank {

    public abstract String getTitle();

    public abstract boolean isTankBlock(int meta);

    public abstract boolean isWallBlock(int meta);

    public abstract float getResistance(Entity exploder);
}
