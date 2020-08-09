/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.ISubtypedBlock;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Created by CovertJaguar on 6/11/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(EnumColor.class)
public abstract class BlockTankMetal<T extends TileTank> extends BlockStructure<T> implements ColorPlugin.IColorHandlerBlock, ISubtypedBlock<EnumColor> {
    private VariantData<EnumColor> variantData;

    protected BlockTankMetal(Material material) {
        super(material);
        setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState().withProperty(getVariantEnumProperty(), EnumColor.WHITE));
    }

    public abstract TankDefinition getTankType();

    @Override
    public VariantData<EnumColor> getVariantData() {
        if (variantData == null)
            variantData = ISubtypedBlock.super.getVariantData();
        return variantData;
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void defineRecipes() {
        getTankType().defineRecipes(this);
        addColorRecipes();
    }

    private void addColorRecipes() {
        for (EnumColor color : EnumColor.VALUES) {
            ItemStack output = getStack(8, color);
            CraftingPlugin.addShapedRecipe(output,
                    "OOO",
                    "ODO",
                    "OOO",
                    'O', getWildcard(),
                    'D', color.getDyeOreDictTag());
        }
    }

    protected void addRecipe(Object... recipe) {
        CraftingPlugin.addShapedRecipe(getStack(8), recipe);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty());
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return convertMetaToState(meta);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(getVariantEnumProperty()).ordinal();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public @Nullable StateMapperBase getStateMapper() {
        return new StateMap.Builder().ignore(getVariantEnumProperty()).build();
    }

    @Override
    public ColorPlugin.IColorFunctionBlock colorHandler() {
        return (state, worldIn, pos, tintIndex) -> getVariant(state).getHexColor();
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (getVariant(state).getDye() != color) {
            world.setBlockState(pos, getDefaultState().withProperty(getVariantEnumProperty(), EnumColor.fromDye(color)));
            return true;
        }
        return false;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @SuppressWarnings("deprecation")
    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return getVariant(state).getMapColor();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getVariant(state).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (int meta = 0; meta < 16; meta++) {
            items.add(new ItemStack(this, 1, meta));
        }
    }
}
