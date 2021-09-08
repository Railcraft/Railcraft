/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2021
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.glass;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.interfaces.IBlockColored;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockStrengthGlass extends BlockGlass implements IRailcraftBlock, ColorPlugin.IColorHandlerBlock, IBlockColored {

    public static final PropertyEnum<Position> POSITION = PropertyEnum.create("position", Position.class);

    public BlockStrengthGlass() {
        super(Material.GLASS, false);
        setResistance(5);
        setHardness(1);
        setSoundType(SoundType.GLASS);
        setCreativeTab(CreativePlugin.STRUCTURE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(EnumColor.PROPERTY, EnumColor.WHITE).withProperty(POSITION, Position.SINGLE));
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
        return new StateMap.Builder().ignore(EnumColor.PROPERTY).build();
    }

    @Override
    public void defineRecipes() {
        for (EnumColor color : EnumColor.VALUES) {
            CraftingPlugin.addShapedRecipe(getStack(8, color),
                    "GGG",
                    "GDG",
                    "GGG",
                    'G', RailcraftBlocks.GLASS.getWildcard(),
                    'D', color.getDyeOreDictTag());
        }

        Object[] frameTypes = {"ingotTin", "ingotNickel", "ingotInvar", "ingotBrass", Items.IRON_INGOT};
        FluidStack water = Fluids.WATER.get(FluidTools.BUCKET_VOLUME);
        for (Object frame : frameTypes) {
            CraftingPlugin.addShapedRecipe(getStack(6, EnumColor.WHITE),
                    "GFG",
                    "GSG",
                    "GWG",
                    'G', "blockGlassColorless",
                    'F', frame,
                    'S', "dustSaltpeter",
                    'W', water);
        }
    }

    @Override
    public @Nullable Class<? extends IVariantEnum> getVariantEnumClass() {
        return EnumColor.class;
    }

    @Override
    public @Nullable IVariantEnum[] getVariants() {
        return EnumColor.VALUES;
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        IBlockState state = getDefaultState();
        if (variant != null) {
            checkVariant(variant);
            state = state.withProperty(EnumColor.PROPERTY, (EnumColor) variant);
        }
        return state;
    }

    @Override
    public EnumColor getColor(IBlockState state) {
        return state.getValue(EnumColor.PROPERTY);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getColor(state).ordinal();
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 5);
    }

    public enum Position implements IStringSerializable {
        SINGLE,
        TOP,
        CENTER,
        BOTTOM;
        public static final Map<EnumSet<Position>, Position> patterns = new HashMap<>();

        static {
            patterns.put(EnumSet.noneOf(Position.class), SINGLE);
            patterns.put(EnumSet.of(BOTTOM), TOP);
            patterns.put(EnumSet.of(TOP, BOTTOM), CENTER);
            patterns.put(EnumSet.of(TOP), BOTTOM);
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getActualState(state, world, pos);

        EnumSet<Position> neighbors = EnumSet.noneOf(Position.class);

        if (WorldPlugin.getBlockState(world, pos.up()) == state)
            neighbors.add(Position.TOP);

        if (WorldPlugin.getBlockState(world, pos.down()) == state)
            neighbors.add(Position.BOTTOM);

        state = state.withProperty(POSITION, Position.patterns.get(neighbors));
        return state;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int meta = 0; meta < 16; meta++) {
            list.add(new ItemStack(this, 1, meta));
        }
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
            world.setBlockState(pos, getDefaultState().withProperty(EnumColor.PROPERTY, EnumColor.fromDye(color)));
            return true;
        }
        return false;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @SuppressWarnings("deprecation")
    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getColor(state).getMapColor();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(EnumColor.PROPERTY, EnumColor.fromOrdinal(meta));
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
        return new BlockStateContainer(this, EnumColor.PROPERTY, POSITION);
    }

}
