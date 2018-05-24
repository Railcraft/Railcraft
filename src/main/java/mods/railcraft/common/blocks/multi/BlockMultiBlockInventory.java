package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

/**
 *
 */
public abstract class BlockMultiBlockInventory extends BlockMultiBlock {
    protected BlockMultiBlockInventory(Material materialIn) {
        super(materialIn);
    }

    protected BlockMultiBlockInventory(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    @Override
    public abstract TileMultiBlockInventory<?> createTileEntity(World world, IBlockState state);
}
