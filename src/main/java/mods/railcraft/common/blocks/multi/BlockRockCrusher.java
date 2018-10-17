/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.property.PropertyCharacter;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 *
 */
public final class BlockRockCrusher extends BlockMultiBlockInventory implements IChargeBlock {

    public static final IProperty<Character> ICON = PropertyCharacter.create("icon", new char[]{'O', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'B', 'D'});
    private static final ChargeDef DEFINITION = new ChargeDef(ConnectType.BLOCK, 0.025D);

    public BlockRockCrusher() {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setDefaultState(getDefaultState().withProperty(ICON, 'O'));
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public ChargeDef getChargeDef(IBlockState state, IBlockAccess world, BlockPos pos) {
        return DEFINITION;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ICON);
    }

    @Override
    public TileMultiBlockInventory createTileEntity(World world, IBlockState state) {
        return new TileRockCrusher();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public Class<TileRockCrusher> getTileClass(IBlockState state) {
        return TileRockCrusher.class;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        deregisterNode(worldIn, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(4, 3);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 4);
        CraftingPlugin.addRecipe(stack,
                "DPD",
                "PSP",
                "DMD",
                'D', "gemDiamond",
                'P', new ItemStack(Blocks.PISTON),
                'M', RailcraftItems.CHARGE, ItemCharge.EnumCharge.MOTOR,
                'S', "blockSteel");
    }
}
