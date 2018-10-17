package mods.railcraft.common.util.entity;

import net.minecraft.util.ITickable;
import net.minecraft.world.World;

/**
 * A logic shared by block entities and entities.
 */
public interface EntityLogic extends ITickable {

    World getWorld();

}
