/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.glass;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.ColorPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockStrengthGlass extends BlockGlass implements ColorPlugin.IColoredBlock {

    public static final PropertyEnum<EnumColor> COLOR = PropertyEnum.create("color", EnumColor.class);
    public static boolean renderingHighlight;
    private static BlockStrengthGlass instance;

    public BlockStrengthGlass() {
        super(Material.GLASS, false);
        setResistance(5);
        setHardness(1);
        setSoundType(SoundType.GLASS);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setUnlocalizedName("railcraft.glass");
        this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumColor.WHITE));
        ColorPlugin.instance.register(this, this);
    }

    public static BlockStrengthGlass getBlock() {
        return instance;
    }

    public static void registerBlock() {
        if (instance == null)
            if (RailcraftConfig.isBlockEnabled("glass")) {
                instance = new BlockStrengthGlass();
                RailcraftRegistry.register(instance, ItemStrengthGlass.class);

                ForestryPlugin.addBackpackItem("builder", instance);

                for (int meta = 0; meta < 16; meta++) {
                    MicroBlockPlugin.addMicroBlockCandidate(instance, meta);
                }
            }
    }

    public EnumColor getColor(IBlockState state) {
        return state.getValue(COLOR);
    }

    public static ItemStack getItem(int meta) {
        return getItem(1, meta);
    }

    public static ItemStack getItem(int qty, int meta) {
        if (instance == null) return null;
        return new ItemStack(instance, qty, meta);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getColor(state).inverse().ordinal();
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerBlockIcons(IIconRegister iconRegister) {
//        icons = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:glass", 1, 5);
//
//        patterns.put(EnumSet.noneOf(Neighbors.class), icons[0]);
//        patterns.put(EnumSet.of(Neighbors.BOTTOM), icons[1]);
//        patterns.put(EnumSet.of(Neighbors.TOP, Neighbors.BOTTOM), icons[2]);
//        patterns.put(EnumSet.of(Neighbors.TOP), icons[3]);
//    }
//
//    private enum Neighbors {
//
//        TOP, BOTTOM;
//    }
//
//    @Override
//    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
//        if (renderingHighlight)
//            return icons[4];
//        if (side <= 1)
//            return icons[0];
//        int meta = world.getBlockMetadata(x, y, z);
//
//        EnumSet neighbors = EnumSet.noneOf(Neighbors.class);
//
//        if (WorldPlugin.getBlock(world, x, y + 1, z) == this && world.getBlockMetadata(x, y + 1, z) == meta)
//            neighbors.add(Neighbors.TOP);
//
//        if (WorldPlugin.getBlock(world, x, y - 1, z) == this && world.getBlockMetadata(x, y - 1, z) == meta)
//            neighbors.add(Neighbors.BOTTOM);
//        return patterns.get(neighbors);
//    }
//
//    @Override
//    public IIcon getIcon(int side, int meta) {
//        if (renderingHighlight)
//            return icons[4];
//        return icons[0];
//    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int meta = 0; meta < 16; meta++) {
            list.add(new ItemStack(item, 1, meta));
        }
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public IBlockColor colorHandler() {
        return (state, worldIn, pos, tintIndex) -> {
            if (renderingHighlight)
                return EnumColor.WHITE.getHexColor();
            return getColor(state).getHexColor();
        };
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (getColor(state).getDye() != color) {
            world.setBlockState(pos, getDefaultState().withProperty(COLOR, EnumColor.fromDye(color)));
            return true;
        }
        return false;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state) {
        return getColor(state).getMapColor();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(COLOR, EnumColor.fromOrdinal(meta).inverse());
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return getColor(state).inverse().ordinal();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COLOR);
    }

}
