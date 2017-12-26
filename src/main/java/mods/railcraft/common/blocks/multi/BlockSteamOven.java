package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 *
 */
public class BlockSteamOven extends BlockMultiBlockInventory {

    public static final IProperty<EnumFacing> FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final IProperty<Integer> ICON = PropertyInteger.create("icon", 0, 3);

    public BlockSteamOven() {
        super(Material.ROCK);
    }

    @Override
    public TileMultiBlockInventory createTileEntity(World world, IBlockState state) {
        return new TileSteamOven();
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileSteamOven.class;
    }
}
