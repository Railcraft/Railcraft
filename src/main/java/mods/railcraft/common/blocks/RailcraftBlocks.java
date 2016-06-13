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

import mods.railcraft.common.blocks.anvil.BlockRCAnvil;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.ItemDetector;
import mods.railcraft.common.blocks.frame.BlockFrame;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.ItemMachine;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.signals.BlockSignalRailcraft;
import mods.railcraft.common.blocks.signals.ItemSignal;
import mods.railcraft.common.blocks.tracks.BlockTrack;
import mods.railcraft.common.blocks.tracks.ItemTrack;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum RailcraftBlocks implements IRailcraftObjectContainer {
    anvil_steel(BlockRCAnvil.class, ItemAnvilBlock.class, "anvil"),
    detector(BlockDetector.class, ItemDetector.class, "detector"),
    frame(BlockFrame.class, ItemBlockRailcraft.class, "frame"),
    machine_alpha(BlockMachine.class, ItemMachine.class, "machine.alpha", EnumMachineAlpha.PROXY, true),
    machine_beta(BlockMachine.class, ItemMachine.class, "machine.beta", EnumMachineBeta.PROXY, false),
    machine_gamma(BlockMachine.class, ItemMachine.class, "machine.gamma", EnumMachineGamma.PROXY, false),
    machine_delta(BlockMachine.class, ItemMachine.class, "machine.delta", EnumMachineDelta.PROXY, false),
    machine_epsilon(BlockMachine.class, ItemMachine.class, "machine.epsilon", EnumMachineEpsilon.PROXY, true),
    signal(BlockSignalRailcraft.class, ItemSignal.class, "signal"),
    track(BlockTrack.class, ItemTrack.class, "track");
    public static final RailcraftBlocks[] VALUES = values();
    private final Class<? extends Block> blockClass;
    private final Class<? extends ItemBlock> itemClass;
    private final String tag;
    private final Object[] blockArgs;
    protected Object altRecipeObject;
    private Block block;
    private IRailcraftObject railcraftObject;

    RailcraftBlocks(Class<? extends Block> blockClass, Class<? extends ItemBlock> itemClass, String tag, Object... blockArgs) {
        this.blockClass = blockClass;
        this.itemClass = itemClass;
        this.tag = tag;
        this.blockArgs = blockArgs;
    }

    public static void definePostRecipes() {
        for (RailcraftBlocks type : VALUES) {
            if (type.railcraftObject != null)
                type.railcraftObject.finalizeDefinition();
        }
    }

    Block createBlock() {
        try {
            Class<?>[] classes = new Class<?>[blockArgs.length];
            for (int i = 1; i < classes.length; i++) {
                classes[i] = blockArgs[i].getClass();
            }
            Constructor<? extends Block> constructor = blockClass.getConstructor(classes);
            return constructor.newInstance((Object[]) classes);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid Block Constructor");
        }
    }

    @Override
    public void register() {
        if (block != null)
            return;

        if (isEnabled()) {
            block = createBlock();
            if (!(block instanceof IRailcraftObject))
                throw new RuntimeException("Railcraft Blocks must implement IRailcraftObject");
            railcraftObject = (IRailcraftObject) block;
            block.setUnlocalizedName("railcraft." + tag);
            RailcraftRegistry.register(block, itemClass);
            railcraftObject.initializeDefinintion();
            railcraftObject.defineRecipes();
        }
    }

    protected void setAltRecipeObject(Object obj) {
        altRecipeObject = obj;
    }

    @Override
    public boolean isEqual(@Nullable ItemStack stack) {
        return stack != null && block != null && InvTools.getBlockFromStack(stack) == block;
    }

    public boolean isEqual(@Nullable Block block) {
        return block != null && this.block == block;
    }

    @Nullable
    public Block block() {
        return block;
    }

    @Override
    public String getBaseTag() {
        return tag;
    }

    @Nullable
    @Override
    public ItemStack getWildcard() {
        return getStack(1, OreDictionary.WILDCARD_VALUE);
    }

    @Nullable
    @Override
    public ItemStack getStack() {
        return getStack(1, 0);
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty) {
        return getStack(qty, 0);
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty, int meta) {
//        register(); Blocks are not created lazily like items.
        if (block == null)
            return null;
        return new ItemStack(block, qty, meta);
    }

    private void checkVariantObject(@Nullable IVariantEnum variant) {
        IVariantEnum.tools.checkVariantObject(itemClass, variant);
    }

    @Nullable
    @Override
    public ItemStack getStack(IVariantEnum variant) {
        return getStack(1, variant);
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty, IVariantEnum variant) {
        checkVariantObject(variant);
        return getStack(qty, variant.getItemMeta());
    }

    @Nullable
    @Override
    public Object getRecipeObject() {
        return getRecipeObject(null);
    }

    @Nullable
    @Override
    public Object getRecipeObject(@Nullable IVariantEnum variant) {
        checkVariantObject(variant);
//        register(); Blocks are not created lazily like items.
        Object obj = null;
        if (railcraftObject != null)
            obj = railcraftObject.getRecipeObject(variant);
        if (obj == null && variant != null)
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
