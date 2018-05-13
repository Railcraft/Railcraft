/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockPostMetal extends BlockPostMetalBase {

    public BlockPostMetal() {
        setDefaultState(blockState.getBaseState()
                .withProperty(COLUMN, Column.FULL)
                .withProperty(COLOR, EnumColor.BLACK)
                .withProperty(NORTH, IPostConnection.ConnectStyle.NONE)
                .withProperty(SOUTH, IPostConnection.ConnectStyle.NONE)
                .withProperty(EAST, IPostConnection.ConnectStyle.NONE)
                .withProperty(WEST, IPostConnection.ConnectStyle.NONE));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = state.withProperty(COLUMN, getColumnStyle(worldIn, state, pos));
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COLOR, COLUMN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public void defineRecipes() {
        // TODO: Temp getRecipes, remove
        RailcraftCraftingManager.rollingMachine.addRecipe(RailcraftBlocks.POST_METAL.getStack(16, EnumColor.BLACK),
                "III",
                " I ",
                "III",
                'I', "ingotIron");
        RailcraftCraftingManager.rollingMachine.addRecipe(RailcraftBlocks.POST_METAL.getStack(16, EnumColor.BLACK),
                "I I",
                "III",
                "I I",
                'I', "ingotIron");
    }
}
