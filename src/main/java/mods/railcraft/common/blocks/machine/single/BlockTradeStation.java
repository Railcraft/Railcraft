package mods.railcraft.common.blocks.machine.single;

import mods.railcraft.common.blocks.BlockEntityDelegate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 *
 */
public class BlockTradeStation extends BlockEntityDelegate {

    public BlockTradeStation() {
        super(Material.ROCK);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileTradeStation.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        throw new UnsupportedOperationException("not implemented"); //TODO Implement this
    }
}
