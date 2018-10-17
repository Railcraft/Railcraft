package mods.railcraft.common.util.entity;

import net.minecraft.world.World;

/**
 * A base logic with a world defined.
 */
public abstract class AbstractLogic implements EntityLogic {
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
