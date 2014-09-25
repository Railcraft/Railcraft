/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartTNT extends CartExplosiveBase {

    public EntityCartTNT(World world) {
        super(world);
    }

    public EntityCartTNT(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
}
