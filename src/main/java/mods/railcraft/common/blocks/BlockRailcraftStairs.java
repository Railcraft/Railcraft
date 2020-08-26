/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 7/29/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockRailcraftStairs extends BlockStairs implements IRailcraftBlock {
    public final IRailcraftBlock baseBlock;

    public BlockRailcraftStairs(IBlockState baseState) {
        super(baseState);
        this.baseBlock = (IRailcraftBlock) baseState.getBlock();
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(8),
                "I  ",
                "II ",
                "III",
                'I', getBaseStack());
        CraftingPlugin.addShapedRecipe(getBaseStack(3),
                "II",
                "II",
                'I', getStack());
    }

    public final ItemStack getBaseStack() {
        return getBaseStack(0);
    }

    public ItemStack getBaseStack(int size) {
        return baseBlock.getStack(size);
    }

    @Override
    public Block getObject() {
        return this;
    }
}
