/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class HarvestPlugin {

    public static void setToolClass(Item item, String toolClass, int level) {
        item.setHarvestLevel(toolClass, level);
    }

    public static void setHarvestLevel(Block block, String toolClass, int level) {
        block.setHarvestLevel(toolClass, level);
    }

    public static void setHarvestLevel(Block block, IBlockState blockState, String toolClass, int level) {
        block.setHarvestLevel(toolClass, level, blockState);
    }

    public static int getBlockHarvestLevel(Block block,IBlockState blockState, String toolClass) {
//        return block.getHarvestLevel(blockState, toolClass);
        return 0;
    }
}
