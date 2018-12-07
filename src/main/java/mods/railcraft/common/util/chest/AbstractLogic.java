/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.chest;

import net.minecraft.world.World;

/**
 * A base logic with a world defined.
 */
public abstract class AbstractLogic implements IEntityLogic {
    protected World world;

    AbstractLogic(World world) {
        this.world = world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }
}
