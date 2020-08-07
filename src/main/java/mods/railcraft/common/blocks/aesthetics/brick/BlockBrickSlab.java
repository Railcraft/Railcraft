/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.ISubtypedBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.brick.BlockBrickSlab.SlabVariant;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RockCrusherCrafter;
import mods.railcraft.common.util.misc.EnumTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
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

import java.util.Locale;
import java.util.Random;

/**
 * Created by CovertJaguar on 8/6/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(SlabVariant.class)
public abstract class BlockBrickSlab extends BlockSlab implements ISubtypedBlock<SlabVariant> {
    public final BrickTheme brickTheme;

    protected BlockBrickSlab(BrickTheme brickTheme) {
        super(Material.ROCK);
        this.brickTheme = brickTheme;
        IBlockState state = blockState.getBaseState();

        if (!isDouble()) {
            state = state.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        }

        setDefaultState(state.withProperty(getVariantEnumProperty(), SlabVariant.BRICK));
    }

    @Override
    public void defineRecipes() {
        for (SlabVariant variant : SlabVariant.VALUES) {
            CraftingPlugin.addShapedRecipe(getStack(6, variant),
                    "III",
                    'I', brickTheme, variant.brickVariant);
            CraftingPlugin.addShapedRecipe(brickTheme.getStack(2, variant.brickVariant),
                    "I",
                    "I",
                    'I', getStack(variant));
            RockCrusherCrafter.INSTANCE.makeRecipe(getStack(variant))
                    .addOutput(brickTheme.getStack(BrickVariant.COBBLE), 0.5F).register();
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return isDouble() ? new BlockStateContainer(this, getVariantProperty()) : new BlockStateContainer(this, HALF, getVariantProperty());
    }

    public RailcraftBlocks getHalf() {
        return RailcraftBlocks.byTag(getPath().replace("_double", ""));
    }

    public BlockBrickSlab getHalfBlock() {
        return (BlockBrickSlab) getHalf().block();
    }

    @SuppressWarnings("ConstantConditions")
    public ItemBrickSlab getHalfItem() {
        return (ItemBrickSlab) getHalf().item();
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
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getHalfBlock().getStack(getVariant(state));
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
        return getTranslationKey();
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return getVariantEnumProperty();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState().withProperty(getVariantEnumProperty(), EnumTools.fromOrdinal(meta & 7, SlabVariant.VALUES));

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
        return EnumTools.fromOrdinal(stack.getMetadata() & 7, SlabVariant.VALUES);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (SlabVariant variant : SlabVariant.VALUES) {
            items.add(getStack(variant));
        }
    }

    public static class Double extends BlockBrickSlab {
        public Double(BrickTheme brickTheme) {
            super(brickTheme);
        }

        @Override
        public boolean isDouble() {
            return true;
        }

        @Override
        public String getTranslationKey() {
            return getHalfBlock().getTranslationKey();
        }
    }

    public static class Half extends BlockBrickSlab {
        public Half(BrickTheme brickTheme) {
            super(brickTheme);
        }

        @Override
        public boolean isDouble() {
            return false;
        }

    }

    public enum SlabVariant implements IVariantEnum {
        BRICK(BrickVariant.BRICK),
        PAVER(BrickVariant.PAVER);
        private final BrickVariant brickVariant;
        public static final SlabVariant[] VALUES = values();

        SlabVariant(BrickVariant brickVariant) {
            this.brickVariant = brickVariant;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
