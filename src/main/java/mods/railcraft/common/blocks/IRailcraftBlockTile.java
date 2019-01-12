/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Code;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Interface for blocks with Tile Entities.
 *
 * It handles creation and adds some helper methods.
 *
 * Created by CovertJaguar on 11/28/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftBlockTile<T extends TileRailcraft> extends IRailcraftBlock, ITileEntityProvider {

    default Class<? extends T> getTileClass(IBlockState state) {
        BlockMeta.Tile annotation = getClass().getAnnotation(BlockMeta.Tile.class);
        Objects.requireNonNull(annotation);
        return Code.cast(annotation.value());
    }

    @Override
    default @NotNull T createNewTileEntity(World worldIn, int meta) {
        try {
            //noinspection deprecation
            return getTileClass(getObject().getStateFromMeta(meta)).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default Optional<? extends T> getTileEntity(IBlockAccess world, BlockPos pos) {
        return getTileEntity(WorldPlugin.getBlockState(world, pos), world, pos);
    }

    default Optional<? extends T> getTileEntity(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos) {
        return WorldPlugin.getTileEntity(world, pos, getTileClass(state));
    }
}
