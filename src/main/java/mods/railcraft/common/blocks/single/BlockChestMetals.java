package mods.railcraft.common.blocks.single;

import mods.railcraft.client.render.tesr.TESRChest;
import mods.railcraft.common.blocks.RailcraftBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockChestMetals extends BlockChestRailcraft {

    public BlockChestMetals() {
        super(Material.IRON);
        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileChestMetals.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileChestMetals();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initializeClient() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileChestMetals.class, new TESRChest(RailcraftBlocks.CHEST_METALS));
    }
}
