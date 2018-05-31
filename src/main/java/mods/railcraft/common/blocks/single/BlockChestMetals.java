package mods.railcraft.common.blocks.single;

import mods.railcraft.client.render.tesr.TESRChest;
import mods.railcraft.common.blocks.BlockEntityDelegate;
import mods.railcraft.common.blocks.RailcraftBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockChestMetals extends BlockEntityDelegate {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockChestMetals() {
        super(Material.IRON);
        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileChestMetals.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileChestMetals();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initializeClient() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileChestMetals.class, new TESRChest(RailcraftBlocks.CHEST_METALS));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
