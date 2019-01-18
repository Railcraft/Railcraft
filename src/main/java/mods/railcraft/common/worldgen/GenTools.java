/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.worldgen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 4/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class GenTools {
    public static final Predicate<IBlockState> STONE = input -> input != null && input.getBlock() == Blocks.STONE;
    public static final Predicate<IBlockState> GRAVEL = input -> input != null && input.getBlock() == Blocks.GRAVEL;
    public static final Predicate<IBlockState> DIRT = input -> input != null && input.getBlock() == Blocks.DIRT;
    public static final Predicate<IBlockState> SAND = input -> input != null && input.getBlock() == Blocks.SAND;
    public static final Predicate<IBlockState> NETHERRACK = input -> input != null && input.getBlock() == Blocks.NETHERRACK;
    public static final Predicate<IBlockState> AIR_STONE = input -> input != null && (input.getBlock() == Blocks.AIR || STONE.test(input));

    private GenTools() {
    }
}
