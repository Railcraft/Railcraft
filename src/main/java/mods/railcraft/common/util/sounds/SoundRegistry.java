/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.sounds;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SoundRegistry {

    private static final Map<IBlockState, SoundType> customSounds = new HashMap<>();

    public static @Nullable SoundType getBlockSound(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return getBlockSound(state, world, pos);
    }

    public static @Nullable SoundType getBlockSound(IBlockState blockState, World world, BlockPos pos) {
        SoundType soundType = customSounds.get(blockState);
        if (soundType == null) {
            return blockState.getBlock().getSoundType(blockState, world, pos, null);
        }
        return soundType;
    }

    public static void setupBlockSounds() {
        Block block = RailcraftBlocks.POST.block();
        if (block != null) {
            registerBlockSound(EnumPost.WOOD.getDefaultState(), SoundType.WOOD);
            registerBlockSound(EnumPost.STONE.getDefaultState(), SoundType.STONE);
            registerBlockSound(EnumPost.METAL_UNPAINTED.getDefaultState(), SoundType.METAL);
            registerBlockSound(EnumPost.WOOD_PLATFORM.getDefaultState(), SoundType.WOOD);
            registerBlockSound(EnumPost.STONE_PLATFORM.getDefaultState(), SoundType.STONE);
            registerBlockSound(EnumPost.METAL_PLATFORM_UNPAINTED.getDefaultState(), SoundType.METAL);
        }
    }

    private static void registerBlockSound(@Nullable IBlockState blockState, SoundType sound) {
        if (blockState == null)
            return;
        customSounds.put(blockState, sound);
    }
}
