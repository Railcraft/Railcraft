/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.sounds;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.generic.BlockGeneric;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SoundRegistry {

    private static final Map<IBlockState, SoundType> customSounds = new HashMap<>();

    @Nullable
    public static SoundType getBlockSound(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return getBlockSound(state, world, pos);
    }

    @Nullable
    public static SoundType getBlockSound(IBlockState blockState, World world, BlockPos pos) {
        Block block = blockState.getBlock();
        if (block instanceof IBlockSoundProvider)
            return ((IBlockSoundProvider) block).getSound(world, pos);
        else {
            return customSounds.get(blockState);
        }
    }

    public static void setupBlockSounds() {
        Block block = RailcraftBlocks.POST.block();
        if (block != null) {
            registerBlockSound(EnumPost.WOOD.getState(), SoundType.WOOD);
            registerBlockSound(EnumPost.STONE.getState(), SoundType.STONE);
            registerBlockSound(EnumPost.METAL_UNPAINTED.getState(), SoundType.METAL);
            registerBlockSound(EnumPost.WOOD_PLATFORM.getState(), SoundType.WOOD);
            registerBlockSound(EnumPost.STONE_PLATFORM.getState(), SoundType.STONE);
            registerBlockSound(EnumPost.METAL_PLATFORM_UNPAINTED.getState(), SoundType.METAL);
        }

        block = BlockGeneric.getBlock();
        if (block != null) {

            for (EnumGeneric cube : EnumGeneric.VALUES) {
                registerBlockSound(cube.getState(), SoundType.STONE);
            }

            registerBlockSound(EnumGeneric.BLOCK_STEEL.getState(), SoundType.METAL);
            registerBlockSound(EnumGeneric.CRUSHED_OBSIDIAN.getState(), SoundType.GROUND);
            registerBlockSound(EnumGeneric.BLOCK_CREOSOTE.getState(), SoundType.WOOD);
        }
    }

    private static void registerBlockSound(@Nullable IBlockState blockState, SoundType sound) {
        if (blockState == null)
            return;
        customSounds.put(blockState, sound);
    }
}
