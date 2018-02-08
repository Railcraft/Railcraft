package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.BlockEntityDelegate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;

public class BlockAdminSteamProducer extends BlockEntityDelegate {

    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockAdminSteamProducer() {
        super(Material.ROCK);
        setBlockUnbreakable();
        setResistance(6000000f);
        disableStats();
        setDefaultState(getDefaultState().withProperty(POWERED, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileAdminSteamProducer.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileAdminSteamProducer();
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        // No drops!
    }

    @Nullable
    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return emptyStack();
    }
}
