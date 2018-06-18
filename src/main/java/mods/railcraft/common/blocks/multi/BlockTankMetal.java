/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.ISubtypedBlock;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 6/11/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = EnumColor.class, propertyName = "color")
public abstract class BlockTankMetal extends BlockMultiBlock implements ColorPlugin.IColoredBlock, ISubtypedBlock<EnumColor> {
    private VariantData<EnumColor> variantData;

    protected BlockTankMetal(Material material) {
        super(material);
        setDefaultState(blockState.getBaseState().withProperty(getVariantProperty(), EnumColor.WHITE));
    }

    @Override
    public VariantData<EnumColor> getVariantData() {
        if (variantData == null)
            variantData = ISubtypedBlock.super.getVariantData();
        return variantData;
    }

    @Override
    public void defineRecipes() {
        addColorRecipes();
    }

    private void addColorRecipes() {
        for (EnumColor color : EnumColor.VALUES) {
            ItemStack output = getStack(8, color);
            CraftingPlugin.addRecipe(output,
                    "OOO",
                    "ODO",
                    "OOO",
                    'O', getStack(),
                    'D', color.getDyeOreDictTag());
        }
    }

    protected void addRecipe(Object... recipe) {
        CraftingPlugin.addRecipe(getStack(8), recipe);
    }

    @Override
    public void finalizeDefinition() {
        ColorPlugin.instance.register(this, this);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty());
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return convertMetaToState(meta);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(getVariantProperty()).ordinal();
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    @Override
    public StateMapperBase getStateMapper() {
        return new StateMap.Builder().ignore(getVariantProperty()).build();
    }

    @Override
    public IBlockColor colorHandler() {
        return (state, worldIn, pos, tintIndex) -> getVariant(state).getHexColor();
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (getVariant(state).getDye() != color) {
            world.setBlockState(pos, getDefaultState().withProperty(getVariantProperty(), EnumColor.fromDye(color)));
            return true;
        }
        return false;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @Override
    public MapColor getMapColor(IBlockState state) {
        return getVariant(state).getMapColor();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getVariant(state).ordinal();
    }
}
