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

import mods.railcraft.common.blocks.aesthetics.materials.ItemMaterial;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.ItemCube;
import mods.railcraft.common.blocks.aesthetics.materials.BlockLantern;
import mods.railcraft.common.blocks.aesthetics.materials.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.materials.slab.ItemSlab;
import mods.railcraft.common.blocks.aesthetics.materials.BlockRailcraftStairs;
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
import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.ItemOre;
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
import net.minecraft.item.Item;
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
    anvil_steel("anvil", BlockRCAnvil.class, ItemAnvilBlock.class),
    detector("detector", BlockDetector.class, ItemDetector.class),
    cube("cube", BlockCube.class, ItemCube.class),
    frame("frame", BlockFrame.class, ItemBlockRailcraft.class),
    lantern("lantern", BlockLantern.class, ItemMaterial.class),
    machine_alpha("machine.alpha", ObjectDef.define(BlockMachine.class, EnumMachineAlpha.PROXY, true), ItemMachine.class),
    machine_beta("machine.beta", ObjectDef.define(BlockMachine.class, EnumMachineBeta.PROXY, false), ItemMachine.class),
    machine_gamma("machine.gamma", ObjectDef.define(BlockMachine.class, EnumMachineGamma.PROXY, false), ItemMachine.class),
    machine_delta("machine.delta", ObjectDef.define(BlockMachine.class, EnumMachineDelta.PROXY, false), ItemMachine.class),
    machine_epsilon("machine.epsilon", ObjectDef.define(BlockMachine.class, EnumMachineEpsilon.PROXY, true), ItemMachine.class),
    ore("ore", BlockOre.class, ItemOre.class),
    signal("signal", BlockSignalRailcraft.class, ItemSignal.class),
    slab("slab", BlockRailcraftSlab.class, ItemSlab.class),
    stair("stair", BlockRailcraftStairs.class, ItemMaterial.class),
    track("track", BlockTrack.class, ItemTrack.class);
    public static final RailcraftBlocks[] VALUES = values();
    private final ObjectDef<Block> blockDef;
    private final ObjectDef<ItemBlock> itemDef;
    private final String tag;
    protected Object altRecipeObject;
    private Block block;
    private ItemBlock item;
    private IRailcraftObject railcraftObject;

    private static class ObjectDef<T> {
        private final Class<? extends T> clazz;
        private Object[] args;

        private ObjectDef(Class<? extends T> clazz, Object... args) {
            this.clazz = clazz;
            this.args = args;
        }

        public static <T> ObjectDef<T> define(Class<? extends T> itemClass, Object... args) {
            return new ObjectDef<T>(itemClass, args);
        }

        public T create() {
            try {
                Class<?>[] classes = new Class<?>[args.length];
                for (int i = 1; i < classes.length; i++) {
                    classes[i] = args[i].getClass();
                }
                Constructor<? extends T> constructor = clazz.getConstructor(classes);
                return constructor.newInstance((Object[]) classes);
            } catch (Exception ex) {
                throw new RuntimeException("Invalid Constructor");
            }
        }
    }

    RailcraftBlocks(String tag, Class<? extends Block> blockClass, Class<? extends ItemBlock> itemClass) {
        this(tag, ObjectDef.define(blockClass), ObjectDef.define(itemClass));
    }

    RailcraftBlocks(String tag, Class<? extends Block> blockClass, ObjectDef<ItemBlock> itemDef) {
        this(tag, ObjectDef.define(blockClass), itemDef);
    }

    RailcraftBlocks(String tag, ObjectDef<Block> blockDef, Class<? extends ItemBlock> itemClass) {
        this(tag, blockDef, ObjectDef.define(itemClass));
    }

    RailcraftBlocks(String tag, ObjectDef<Block> blockDef, ObjectDef<ItemBlock> itemDef) {
        this.blockDef = blockDef;
        this.itemDef = itemDef;
        this.tag = tag;
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
            block = blockDef.create();
            if (!(block instanceof IRailcraftObject))
                throw new RuntimeException("Railcraft Blocks must implement IRailcraftObject");
            railcraftObject = (IRailcraftObject) block;
            block.setRegistryName(tag);
            block.setUnlocalizedName("railcraft." + tag);

            Object[] itemArgs = itemDef.args;
            itemDef.args = new Object[itemArgs.length + 1];
            itemDef.args[0] = block;
            System.arraycopy(itemArgs, 0, itemDef.args, 1, itemArgs.length);
            item = itemDef.create();
            item.setRegistryName(tag);

            RailcraftRegistry.register(block, item);
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

    @Nullable
    public Item item() {
        return item;
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
        IVariantEnum.tools.checkVariantObject(blockDef.clazz, variant);
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
        return railcraftObject.getStack(qty, variant);
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
            obj = variant.getAlternate(this);
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
