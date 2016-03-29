/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockPostMetal extends BlockPostBase {

    public static final PropertyEnum<EnumColor> COLOR = PropertyEnum.create("color", EnumColor.class);
    public static BlockPostMetal post;
    public static BlockPostMetal platform;
    public final boolean isPlatform;

    private BlockPostMetal(int renderType, boolean isPlatform) {
        super(renderType);
        setStepSound(Block.soundTypeMetal);
        this.isPlatform = isPlatform;
        this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumColor.WHITE));
    }

    public static void registerPost() {
        if (post == null && RailcraftConfig.isBlockEnabled("post.metal"))
            post = BlockPostMetal.make("post.metal", false);
    }

    public static void registerPlatform() {
        if (platform == null && RailcraftConfig.isBlockEnabled("post.metal.platform"))
            platform = BlockPostMetal.make("post.metal.platform", true);
    }

    private static BlockPostMetal make(String tag, boolean isPlatform) {
        BlockPostMetal block = new BlockPostMetal(Railcraft.getProxy().getRenderId(), isPlatform);
        block.setUnlocalizedName("railcraft." + tag);
        RailcraftRegistry.register(block, ItemPostMetal.class);

//        HarvestPlugin.setBlockHarvestLevel(block, "crowbar", 0);
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 2, block);

        ForestryPlugin.addBackpackItem("builder", block);

        for (EnumColor color : EnumColor.VALUES) {
            ItemStack stack = block.getItem(1, color.ordinal());
            RailcraftRegistry.register(stack);
        }

        return block;
    }

    public EnumColor getColor(IBlockState state) {
        return state.getValue(COLOR);
    }

    @Override
    public boolean isPlatform(IBlockState state) {
        return isPlatform;
    }

    public ItemStack getItem() {
        return getItem(1, 3);
    }

    public ItemStack getItem(int qty) {
        return getItem(qty, 3);
    }

    public ItemStack getItem(int qty, int color) {
        return new ItemStack(this, qty, color);
    }

    public ItemStack getItem(int qty, EnumColor color) {
        return new ItemStack(this, qty, color.ordinal());
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumColor color : EnumColor.VALUES) {
            list.add(getItem(1, color.ordinal()));
        }
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        if (isPlatform)
            list.add(EnumPost.METAL_PLATFORM_UNPAINTED.getItem());
        else
            list.add(EnumPost.METAL_UNPAINTED.getItem());
        return list;
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
        return this.getDefaultState().withProperty(COLOR, EnumColor.fromOrdinal(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return getColor(state).ordinal();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, COLOR);
    }
}
