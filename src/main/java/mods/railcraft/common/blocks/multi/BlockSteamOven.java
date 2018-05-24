package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 */
public  class BlockSteamOven extends BlockMultiBlockInventory {

    public static final IProperty<EnumFacing> FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final IProperty<TileSteamOven.Icon> ICON = PropertyEnum.create("icon", TileSteamOven.Icon.class);

    public BlockSteamOven() {
        super(Material.ROCK);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public TileMultiBlockInventory<?> createTileEntity(World world, IBlockState state) {
        return new TileSteamOven();
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileSteamOven.class;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ICON);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(4, 2);
    }
}
