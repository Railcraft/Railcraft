/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.sounds;

import com.google.common.base.Strings;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.aesthetics.post.BlockPost;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.blocks.aesthetics.wall.BlockRailcraftWall;
import mods.railcraft.common.blocks.aesthetics.wall.EnumWallAlpha;
import mods.railcraft.common.blocks.aesthetics.wall.EnumWallBeta;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SoundRegistry {

    private static final Map<IBlockState, SoundType> customSounds = new HashMap<>();

    public static SoundType getBlockSound(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return getBlockSound(state, world, pos);
    }

    public static SoundType getBlockSound(IBlockState blockState, World world, BlockPos pos) {
        Block block = blockState.getBlock();
        if (block instanceof IBlockSoundProvider)
            return ((IBlockSoundProvider) block).getSound(world, pos);
        else {
            return customSounds.get(blockState);
        }
    }

    public static SoundEvent matchSoundType(String soundPath, SoundType soundType) {
        String typeString = soundPath.substring(soundPath.lastIndexOf(".") + 1);
        switch (typeString) {
            case "break":
                return soundType.getBreakSound();
            case "fall":
                return soundType.getFallSound();
            case "hit":
                return soundType.getHitSound();
            case "place":
                return soundType.getPlaceSound();
            case "step":
                return soundType.getStepSound();
        }
        return soundType.getStepSound();
    }

    public static void setupBlockSounds() {
        Block block = BlockPost.getBlock();
        if (block != null) {
            registerBlockSound(EnumPost.WOOD.getState(), SoundType.WOOD);
            registerBlockSound(EnumPost.STONE.getState(), SoundType.STONE);
            registerBlockSound(EnumPost.METAL_UNPAINTED.getState(), SoundType.METAL);
            registerBlockSound(EnumPost.WOOD_PLATFORM.getState(), SoundType.WOOD);
            registerBlockSound(EnumPost.STONE_PLATFORM.getState(), SoundType.STONE);
            registerBlockSound(EnumPost.METAL_PLATFORM_UNPAINTED.getState(), SoundType.METAL);
        }

        block = BlockCube.getBlock();
        if (block != null) {

            for (EnumCube cube : EnumCube.VALUES) {
                registerBlockSound(cube.getState(), SoundType.STONE);
            }

            registerBlockSound(EnumCube.STEEL_BLOCK.getState(), SoundType.METAL);
            registerBlockSound(EnumCube.CRUSHED_OBSIDIAN.getState(), SoundType.GROUND);
            registerBlockSound(EnumCube.CREOSOTE_BLOCK.getState(), SoundType.WOOD);
        }

        block = BlockRailcraftWall.getBlockAlpha();
        if (block != null) {

            for (EnumWallAlpha wall : EnumWallAlpha.VALUES) {
                registerBlockSound(wall.ordinal(), SoundType.STONE);
            }

            registerBlockSound(EnumWallAlpha.ICE.ordinal(), Block.soundTypeGlass);
            registerBlockSound(EnumWallAlpha.SNOW.ordinal(), Block.soundTypeSnow);
        }

        block = BlockRailcraftWall.getBlockBeta();
        if (block != null) {

            for (EnumWallBeta wall : EnumWallBeta.VALUES) {
                registerBlockSound(wall.ordinal(), SoundType.STONE);
            }
        }
    }

    private static void registerBlockSound(IBlockState blockState, SoundType sound) {
        customSounds.put(blockState, sound);
    }
}
