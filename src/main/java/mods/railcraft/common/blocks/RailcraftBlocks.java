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

import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.ItemCube;
import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.glass.ItemStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.materials.BlockLantern;
import mods.railcraft.common.blocks.aesthetics.materials.BlockRailcraftStairs;
import mods.railcraft.common.blocks.aesthetics.materials.BlockRailcraftWall;
import mods.railcraft.common.blocks.aesthetics.materials.ItemMaterial;
import mods.railcraft.common.blocks.aesthetics.materials.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.materials.slab.ItemSlab;
import mods.railcraft.common.blocks.anvil.BlockRCAnvil;
import mods.railcraft.common.blocks.anvil.ItemAnvil;
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
import mods.railcraft.common.blocks.ore.BlockWorldLogic;
import mods.railcraft.common.blocks.ore.ItemOre;
import mods.railcraft.common.blocks.signals.BlockSignalRailcraft;
import mods.railcraft.common.blocks.signals.ItemSignal;
import mods.railcraft.common.blocks.tracks.BlockTrack;
import mods.railcraft.common.blocks.tracks.BlockTrackElevator;
import mods.railcraft.common.blocks.tracks.ItemTrack;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.firestone.BlockRitual;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum RailcraftBlocks implements IRailcraftObjectContainer {
    anvilSteel("anvil", BlockRCAnvil::new, ItemAnvil::new),
    cube("cube", BlockCube::new, ItemCube::new),
    detector("detector", BlockDetector::new, ItemDetector::new),
    frame("frame", BlockFrame::new, ItemBlockRailcraft::new),
    glass("glass", BlockStrengthGlass::new, ItemStrengthGlass::new),
    lantern("lantern", BlockLantern::new, ItemMaterial::new),
    machine_alpha("machine.alpha", () -> new BlockMachine<EnumMachineAlpha>(EnumMachineAlpha.PROXY, true), ItemMachine::new),
    machine_beta("machine.beta", () -> new BlockMachine<EnumMachineBeta>(EnumMachineBeta.PROXY, false), ItemMachine::new),
    machine_gamma("machine.gamma", () -> new BlockMachine<EnumMachineGamma>(EnumMachineGamma.PROXY, false), ItemMachine::new),
    machine_delta("machine.delta", () -> new BlockMachine<EnumMachineDelta>(EnumMachineDelta.PROXY, false), ItemMachine::new),
    machine_epsilon("machine.epsilon", () -> new BlockMachine<EnumMachineEpsilon>(EnumMachineEpsilon.PROXY, true), ItemMachine::new),
    ore("ore", BlockOre::new, ItemOre::new),
    ritual("ritual", BlockRitual::new, ItemBlockRailcraft::new),
    signal("signal", BlockSignalRailcraft::new, ItemSignal::new),
    slab("slab", BlockRailcraftSlab::new, ItemSlab::new),
    stair("stair", BlockRailcraftStairs::new, ItemMaterial::new),
    track("track", BlockTrack::new, ItemTrack::new),
    trackElevator("track.elevator", BlockTrackElevator::new, ItemBlockRailcraft::new),
    wall("wall", BlockRailcraftWall::new, ItemMaterial::new),
    worldLogic("worldlogic", BlockWorldLogic::new, ItemBlockRailcraft::new);
    public static final RailcraftBlocks[] VALUES = values();
    private final Supplier<Block> blockSupplier;
    private final Function<Block, ItemBlock> itemSupplier;
    private final String tag;
    protected Object altRecipeObject;
    private Block block;
    private ItemBlock item;

    RailcraftBlocks(String tag, Supplier<Block> blockSupplier, Function<Block, ItemBlock> itemSupplier) {
        this.blockSupplier = blockSupplier;
        this.itemSupplier = itemSupplier;
        this.tag = tag;
    }

    public static void finalizeDefinitions() {
        for (RailcraftBlocks type : VALUES) {
            if (type.block != null)
                ((IRailcraftObject) type.block).finalizeDefinition();
            if (type.item != null)
                ((IRailcraftObject) type.item).finalizeDefinition();
        }
    }

    @Override
    public void register() {
        if (block != null)
            return;

        if (isEnabled()) {
            block = blockSupplier.get();
            block.setRegistryName(tag);
            block.setUnlocalizedName("railcraft." + tag);

            item = itemSupplier.apply(block);
            item.setRegistryName(tag);

            RailcraftRegistry.register(block, item);

            if (!(block instanceof IRailcraftObject))
                throw new RuntimeException("Railcraft Blocks must implement IRailcraftObject");
            IRailcraftObject blockObject = (IRailcraftObject) block;
            blockObject.initializeDefinintion();
            blockObject.defineRecipes();

            if (!(item instanceof IRailcraftObject))
                throw new RuntimeException("Railcraft ItemBlocks must implement IRailcraftObject");
            IRailcraftObject itemObject = (IRailcraftObject) item;
            itemObject.initializeDefinintion();
            itemObject.defineRecipes();
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
    public IBlockState getDefaultState() {
        if (block == null)
            return null;
        return block.getDefaultState();
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
    public ItemStack getStack(int qty, int meta) {
//        register(); Blocks are not created lazily like items.
        if (block == null)
            return null;
        return new ItemStack(block, qty, meta);
    }

    private void checkVariantObject(@Nullable IVariantEnum variant) {
        if (block != null)
            ((IRailcraftObject) block).checkVariant(variant);
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty, IVariantEnum variant) {
        checkVariantObject(variant);
        if (block != null)
            return ((IRailcraftObject) block).getStack(qty, variant);
        return null;
    }

    @Nullable
    @Override
    public Object getRecipeObject(@Nullable IVariantEnum variant) {
        checkVariantObject(variant);
//        register(); Blocks are not created lazily like items.
        Object obj = null;
        if (block != null)
            obj = ((IRailcraftObject) block).getRecipeObject(variant);
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
        return ((IRailcraftObject) block);
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
