/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.aesthetics.concrete;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.interfaces.IBlockColored;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.block.Block;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

@BlockMeta.Variant(EnumColor.class)
public class BlockReinforcedConcrete extends BlockRailcraftSubtyped<EnumColor> implements ColorPlugin.IColorHandlerBlock, IBlockColored {

    public BlockReinforcedConcrete() {
        super(Material.ROCK, MapColor.STONE);
        setResistance(45);
        setHardness(4);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativePlugin.STRUCTURE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(getVariantEnumProperty(), EnumColor.SILVER));
    }

    @Override
    public Block getObject() {
        return this;
    }

    @Override
    public void initializeDefinition() {
        ForestryPlugin.addBackpackItem("forestry.builder", this);

        for (int meta = 0; meta < 16; meta++) {
            MicroBlockPlugin.addMicroBlockCandidate(this, meta);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public @Nullable StateMapperBase getStateMapper() {
        return new StateMap.Builder().ignore(getVariantEnumProperty()).build();
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        IBlockState state = getDefaultState();
        if (variant != null) {
            checkVariant(variant);
            state = state.withProperty(getVariantEnumProperty(), (EnumColor) variant);
        }
        return state;
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        for (EnumColor color : EnumColor.VALUES) {
            CraftingPlugin.addShapedRecipe(getStack(8, color),
                    "GGG",
                    "GDG",
                    "GGG",
                    'G', RailcraftBlocks.REINFORCED_CONCRETE.getWildcard(),
                    'D', color.getDyeOreDictTag());
        }

        //TODO: Make it craft a powder ALA Vanilla? World interaction may not be such a bad idea, and we can get rid of the fluid crafting in exchange for crafting colors directly...
        FluidStack water = Fluids.WATER.get(FluidTools.BUCKET_VOLUME);
        CraftingPlugin.addShapedRecipe(getStack(8, EnumColor.SILVER),
                "SIS",
                "IWI",
                "SIS",
                'W', water,
                'I', RailcraftItems.REBAR,
                'S', RailcraftItems.CONCRETE);
    }

    @Override
    public EnumColor getColor(IBlockState state) {
        return state.getValue(getVariantEnumProperty());
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getColor(state).ordinal();
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public ColorPlugin.IColorFunctionBlock colorHandler() {
        return (state, worldIn, pos, tintIndex) -> getColor(state).getHexColor();
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (getColor(state).getDye() != color) {
            world.setBlockState(pos, getDefaultState().withProperty(getVariantEnumProperty(), EnumColor.fromDye(color)));
            return true;
        }
        return false;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int meta = 0; meta < 16; meta++) {
            list.add(new ItemStack(this, 1, meta));
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(getVariantEnumProperty(), EnumColor.fromOrdinal(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return getColor(state).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty());
    }
}
