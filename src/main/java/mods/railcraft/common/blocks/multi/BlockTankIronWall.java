package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

/**
 *
 */
public class BlockTankIronWall extends BlockMultiBlock {

    public BlockTankIronWall() {
        super(Material.ROCK);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public TileMultiBlock<?> createTileEntity(World world, IBlockState state) {
        return new TileTankIronWall();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileTankIronWall.class;
    }
}
