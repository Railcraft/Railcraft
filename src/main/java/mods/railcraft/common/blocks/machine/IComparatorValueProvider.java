package mods.railcraft.common.blocks.machine;

import net.minecraft.world.World;

/**
 * @author wshadow
 *
 */
public interface IComparatorValueProvider {
	int getComparatorInputOverride(World world, int x, int y, int z, int side);

}
