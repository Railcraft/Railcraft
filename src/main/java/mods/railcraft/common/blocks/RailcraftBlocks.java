/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.blocks;

import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.ItemDetector;
import mods.railcraft.common.blocks.tracks.BlockTrack;
import mods.railcraft.common.blocks.tracks.ItemTrack;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum RailcraftBlocks implements IRailcraftObjectContainer {
    detector(BlockDetector.class, ItemDetector.class, "detector"),
    track(BlockTrack.class, ItemTrack.class, "track");
    public static final RailcraftBlocks[] VALUES = values();
    private final Class<? extends Block> blockClass;
    private final Class<? extends ItemBlock> itemClass;
    private final String tag;
    private final Object altRecipeObject;
    private Block block;
    private IRailcraftObject railcraftObject;

    RailcraftBlocks(Class<? extends Block> blockClass, Class<? extends ItemBlock> itemClass, String tag) {
        this(blockClass, itemClass, tag, null);
    }

    RailcraftBlocks(Class<? extends Block> blockClass, Class<? extends ItemBlock> itemClass, String tag, Object alt) {
        this.blockClass = blockClass;
        this.itemClass = itemClass;
        this.tag = tag;
        this.altRecipeObject = alt;
    }

    public static void definePostRecipes() {
        for (RailcraftBlocks type : VALUES) {
            if (type.railcraftObject != null)
                type.railcraftObject.finalizeDefinition();
        }
    }

    @Override
    public void register() {
        if (block != null)
            return;

        if (isEnabled()) {
            try {
                block = blockClass.newInstance();
            } catch (InstantiationException ex) {
                throw new RuntimeException("Invalid Block Constructor");
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Invalid Block Constructor");
            }
            if (!(block instanceof IRailcraftObject))
                throw new RuntimeException("Railcraft Blocks must implement IRailcraftObject");
            railcraftObject = (IRailcraftObject) block;
            block.setUnlocalizedName("railcraft." + tag);
            RailcraftRegistry.register(block, itemClass);
            railcraftObject.initializeDefinintion();
            railcraftObject.defineRecipes();
        }
    }

    @Override
    public boolean isEqual(ItemStack stack) {
        return stack != null && block != null && InvTools.getBlockFromStack(stack) == block;
    }

    public boolean isEqual(Block block) {
        return block != null && this.block == block;
    }

    public Block block() {
        return block;
    }

    @Override
    public String getBaseTag() {
        return tag;
    }

    @Override
    public ItemStack getWildcard() {
        return getStack(1, OreDictionary.WILDCARD_VALUE);
    }

    @Override
    public ItemStack getStack() {
        return getStack(1, 0);
    }

    @Override
    public ItemStack getStack(int qty) {
        return getStack(qty, 0);
    }

    @Override
    public ItemStack getStack(int qty, int meta) {
//        register(); Blocks are not created lazily like items.
        if (block == null)
            return null;
        return new ItemStack(block, qty, meta);
    }

    private void checkVariantObject(IVariantEnum variant) {
        if (variant == null || variant.getParentClass() != blockClass)
            throw new RuntimeException("Incorrect Variant object used.");
    }

    @Override
    public ItemStack getStack(IVariantEnum variant) {
        return getStack(1, variant);
    }

    @Override
    public ItemStack getStack(int qty, IVariantEnum variant) {
        checkVariantObject(variant);
        return getStack(qty, variant.ordinal());
    }

    @Override
    public Object getRecipeObject() {
//        register(); Blocks are not created lazily like items.
        if (railcraftObject != null)
            return railcraftObject.getRecipeObject(null);
        Object obj = altRecipeObject;
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    @Override
    public Object getRecipeObject(IVariantEnum variant) {
        if (variant != null)
            checkVariantObject(variant);
//        register(); Blocks are not created lazily like items.
        if (railcraftObject != null)
            return railcraftObject.getRecipeObject(variant);
        Object obj = null;
        if (variant != null)
            obj = variant.getAlternate();
        if (obj == null)
            obj = altRecipeObject;
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    @Override
    public IRailcraftObject getObject() {
        return railcraftObject;
    }

    @Override
    public boolean isEnabled() {
        return RailcraftConfig.isBlockEnabled(tag);
    }

    @Override
    public boolean isLoaded() {
        return block != null;
    }
}
