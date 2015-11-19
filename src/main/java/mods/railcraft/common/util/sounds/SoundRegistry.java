/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.sounds;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.aesthetics.post.BlockPost;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.blocks.aesthetics.wall.BlockRailcraftWall;
import mods.railcraft.common.blocks.aesthetics.wall.EnumWallAlpha;
import mods.railcraft.common.blocks.aesthetics.wall.EnumWallBeta;
import net.minecraft.block.Block.SoundType;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SoundRegistry {

    private final static Map<Block, Map<Integer, SoundType>> customSounds = new HashMap<Block, Map<Integer, SoundType>>();

    public static SoundType getSound(Block block, int meta) {
        Map<Integer, SoundType> blockSounds = customSounds.get(block);
        if (blockSounds != null) {
            return blockSounds.get(meta);
        }
        return null;
    }

    public static void setupBlockSounds() {
        Block block = BlockPost.block;
        if (block != null) {
            registerCustomStepSound(block, EnumPost.WOOD.ordinal(), Block.soundTypeWood);
            registerCustomStepSound(block, EnumPost.STONE.ordinal(), Block.soundTypeStone);
            registerCustomStepSound(block, EnumPost.METAL_UNPAINTED.ordinal(), Block.soundTypeMetal);
            registerCustomStepSound(block, EnumPost.WOOD_PLATFORM.ordinal(), Block.soundTypeWood);
            registerCustomStepSound(block, EnumPost.STONE_PLATFORM.ordinal(), Block.soundTypeStone);
            registerCustomStepSound(block, EnumPost.METAL_PLATFORM_UNPAINTED.ordinal(), Block.soundTypeMetal);
        }

        block = BlockCube.getBlock();
        if (block != null) {

            for (EnumCube cube : EnumCube.VALUES) {
                registerCustomStepSound(block, cube.ordinal(), Block.soundTypeStone);
            }

            registerCustomStepSound(block, EnumCube.STEEL_BLOCK.ordinal(), Block.soundTypeMetal);
            registerCustomStepSound(block, EnumCube.CRUSHED_OBSIDIAN.ordinal(), Block.soundTypeGravel);
            registerCustomStepSound(block, EnumCube.CREOSOTE_BLOCK.ordinal(), Block.soundTypeWood);
        }

        block = BlockRailcraftWall.getBlockAlpha();
        if (block != null) {

            for (EnumWallAlpha wall : EnumWallAlpha.VALUES) {
                registerCustomStepSound(block, wall.ordinal(), Block.soundTypeStone);
            }

            registerCustomStepSound(block, EnumWallAlpha.ICE.ordinal(), Block.soundTypeGlass);
            registerCustomStepSound(block, EnumWallAlpha.SNOW.ordinal(), Block.soundTypeSnow);
        }

        block = BlockRailcraftWall.getBlockBeta();
        if (block != null) {

            for (EnumWallBeta wall : EnumWallBeta.VALUES) {
                registerCustomStepSound(block, wall.ordinal(), Block.soundTypeStone);
            }
        }
    }

    private static void registerCustomStepSound(Block block, int metadata, SoundType sound) {
        Map<Integer, SoundType> blockSounds = customSounds.get(block);
        if (blockSounds == null) {
            blockSounds = new HashMap<Integer, SoundType>();
            customSounds.put(block, blockSounds);
        }
        blockSounds.put(metadata, sound);
    }
}
