/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.aesthetics;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.ISubtypedBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.util.misc.EnumTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by CovertJaguar on 8/26/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockRailcraftSlab<V extends Enum<V> & IVariantEnum> extends BlockSlab implements ISubtypedBlock<V> {

    protected BlockRailcraftSlab(IBlockState baseBlock) {
        super(baseBlock.getMaterial());
        setCreativeTab(CreativePlugin.STRUCTURE_TAB);

        IBlockState state = blockState.getBaseState();
        if (!isDouble()) {
            state = state.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        }
        setDefaultState(state.withProperty(getVariantEnumProperty(), getVariants()[0]));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return isDouble() ? new BlockStateContainer(this, getVariantProperty()) : new BlockStateContainer(this, HALF, getVariantProperty());
    }

    public final BlockRailcraftSlab getHalfBlock() {
        return RailcraftBlocks.byTag(getPath().replace("_double", ""));
    }

    public Item getHalfItem() {
        return Item.getItemFromBlock(getHalfBlock());
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (IVariantEnum variant : getVariants()) {
            items.add(getStack(variant));
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getHalfBlock().getStack();
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return getHalfItem();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getVariant(state).ordinal();
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return getVariantEnumProperty();
    }

    @Override
    public Block getObject() {
        return this;
    }

    @Override
    public Block setTranslationKey(String key) {
        return super.setTranslationKey(key);
    }

    @Override
    public String getTranslationKey(int meta) {
        if (isDouble())
            return getHalfBlock().getTranslationKey();
        return super.getTranslationKey();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState().withProperty(getVariantEnumProperty(), EnumTools.fromOrdinal(meta & 7, getVariants()));

        if (!isDouble()) {
            state = state.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }

        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        meta = meta | getVariant(state).ordinal();

        if (!isDouble() && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
            meta |= 8;
        }

        return meta;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return EnumTools.fromOrdinal(stack.getMetadata() & 7, getVariants());
    }
}
