package mods.railcraft.common.blocks.machine;

import net.minecraft.world.World;

/**
 * @author wshadow
 *
 */
public interface IComparatorValueProvider {
	public int getComparatorInputOverride(World world, int x, int y, int z, int side);

}
