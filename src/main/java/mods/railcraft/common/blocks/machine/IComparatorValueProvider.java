package mods.railcraft.common.blocks.machine;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * @author wshadow
 */
public interface IComparatorValueProvider {

    int getComparatorInputOverride(World world, BlockPos pos, EnumFacing face);

}
