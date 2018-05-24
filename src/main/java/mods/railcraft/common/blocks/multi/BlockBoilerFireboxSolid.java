package mods.railcraft.common.blocks.multi;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

/**
 *
 */
public final class BlockBoilerFireboxSolid extends BlockBoilerFirebox {
    @Override
    public TileMultiBlock<?> createTileEntity(World world, IBlockState state) {
        return new TileBoilerFireboxSolid();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(3, 1);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileBoilerFireboxSolid.class;
    }
}
