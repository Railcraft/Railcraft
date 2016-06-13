package mods.railcraft.common.blocks.machine;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author wshadow
 */
public interface IComparatorValueProvider {

    int getComparatorInputOverride(World world, BlockPos pos);

}
