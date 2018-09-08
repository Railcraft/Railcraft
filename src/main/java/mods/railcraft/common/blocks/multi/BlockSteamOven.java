package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.multi.TileSteamOven.Icon;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 */
public class BlockSteamOven extends BlockMultiBlockInventory {

    public static final IProperty<EnumFacing> FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final IProperty<TileSteamOven.Icon> ICON = PropertyEnum.create("icon", TileSteamOven.Icon.class);

    public BlockSteamOven() {
        super(Material.IRON);
        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(ICON, Icon.DEFAULT));
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public TileMultiBlockInventory<?, ?, ?> createTileEntity(World world, IBlockState state) {
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

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 4);
        CraftingPlugin.addRecipe(stack,
                "SSS",
                "SFS",
                "SSS",
                'F', new ItemStack(Blocks.FURNACE),
                'S', RailcraftItems.PLATE, Metal.STEEL);
    }
}
