/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockContainerRailcraft<T extends TileRailcraft> extends BlockContainer implements IRailcraftBlockTile<T> {
    protected BlockContainerRailcraft(Material materialIn) {
        super(materialIn);
    }

    protected BlockContainerRailcraft(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    {
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public Block getObject() {
        return this;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SuppressWarnings("unused")
    public void markBlockForUpdate(@Nullable World world, BlockPos pos) {
        if (world != null) {
            IBlockState state = WorldPlugin.getBlockState(world, pos);
            markBlockForUpdate(state, world, pos);
        }
    }

    public void markBlockForUpdate(IBlockState state, @Nullable World world, BlockPos pos) {
        if (world != null) {
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
        try {
            getTileEntity(state, worldIn, pos).ifPresent(t -> t.onNeighborBlockChange(state, neighborBlock, fromPos));
        } catch (StackOverflowError error) {
            Game.log().throwable(Level.ERROR, 10, error, "Stack Overflow Error in {0}#onNeighborBlockChange", getClass());
            if (Game.DEVELOPMENT_VERSION)
                throw error;
        }
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        getTileEntity(state, worldIn, pos).ifPresent(t -> t.onBlockPlacedBy(state, placer, stack));
    }

    @Override
    public @NotNull T createTileEntity(World world, IBlockState state) {
        return IRailcraftBlockTile.super.createNewTileEntity(world, getMetaFromState(state));
    }

    @Override
    @Deprecated
    public final T createNewTileEntity(World worldIn, int meta) {
        return IRailcraftBlockTile.super.createNewTileEntity(worldIn, meta);
    }
}
