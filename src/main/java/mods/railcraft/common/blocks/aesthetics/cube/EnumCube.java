/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.cube;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IBlockVariantEnum;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleFactory;
import mods.railcraft.common.modules.ModuleStructures;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum EnumCube implements IBlockVariantEnum<EnumCube> {

    COKE_BLOCK(ModuleFactory.class, "coke", new FlammableCube(5, 10), 2f, 10f),
    CONCRETE_BLOCK(ModuleStructures.class, "concrete", new SimpleCube(), 3f, 15f),
    STEEL_BLOCK(ModuleFactory.class, "steel", new SimpleCube(), 5f, 15f),
    @Deprecated
    INFERNAL_BRICK(ModuleStructures.class, "brick.infernal", new ReplacerCube(), 3f, 15f),
    CRUSHED_OBSIDIAN(ModuleFactory.class, "crushed.obsidian", new CrushedObsidian(), 2f, 45f),
    @Deprecated
    SANDY_BRICK(ModuleStructures.class, "brick.sandy", new ReplacerCube(), 2f, 10f),
    ABYSSAL_STONE(ModuleWorld.class, "stone.abyssal", new SimpleCube(), 2f, 10f),
    QUARRIED_STONE(ModuleWorld.class, "stone.quarried", new SimpleCube(), 2f, 10f),
    CREOSOTE_BLOCK(ModuleStructures.class, "creosote", new FlammableCube(5, 300), 3f, 10f),
    COPPER_BLOCK(ModuleFactory.class, "copper", new SimpleCube(), 3f, 10f),
    TIN_BLOCK(ModuleFactory.class, "tin", new SimpleCube(), 3f, 10f),
    LEAD_BLOCK(ModuleFactory.class, "lead", new SimpleCube(), 2f, 20f),;
    public static final EnumCube[] VALUES = values();
    private static final List<EnumCube> creativeList = new ArrayList<EnumCube>();

    static {
        creativeList.add(COKE_BLOCK);
        creativeList.add(COPPER_BLOCK);
        creativeList.add(TIN_BLOCK);
        creativeList.add(LEAD_BLOCK);
        creativeList.add(STEEL_BLOCK);
        creativeList.add(CONCRETE_BLOCK);
        creativeList.add(CREOSOTE_BLOCK);
        creativeList.add(CRUSHED_OBSIDIAN);
        creativeList.add(ABYSSAL_STONE);
        creativeList.add(QUARRIED_STONE);

        ((ReplacerCube) SANDY_BRICK.blockDef).replacementState = Blocks.STONEBRICK.getDefaultState();
        ((ReplacerCube) INFERNAL_BRICK.blockDef).replacementState = Blocks.STONEBRICK.getDefaultState();
    }

    private final Class<? extends IRailcraftModule> module;
    private final String tag;
    private final SimpleCube blockDef;
    private final float hardness;
    private final float resistance;

    EnumCube(Class<? extends IRailcraftModule> module, String tag, SimpleCube blockDef, float hardness, float resistance) {
        this.module = module;
        this.tag = tag;
        this.blockDef = blockDef;
        this.hardness = hardness;
        this.resistance = resistance;
    }

    public static List<EnumCube> getCreativeList() {
        return creativeList;
    }

    public static EnumCube fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return CONCRETE_BLOCK;
        return VALUES[id];
    }

    @Override
    public boolean isValidBaseObject(Class<?> clazz) {
        return clazz == BlockCube.class;
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftObjectContainer container) {
        return null;
    }

    public Class<? extends IRailcraftModule> getModule() {
        return module;
    }

    public String getTag() {
        return "tile.railcraft.cube." + tag;
    }

    public SimpleCube getBlockDef() {
        return blockDef;
    }

    @Override
    public Block getBlock() {
        return BlockCube.getBlock();
    }

    @Override
    public IBlockState getState() {
        if (BlockCube.getBlock() == null) return null;
        return BlockCube.getBlock().getState(this);
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistance() {
        return resistance;
    }

    @Override
    public boolean isEnabled() {
        return getModule() != null && RailcraftModuleManager.isModuleEnabled(getModule()) && RailcraftConfig.isSubBlockEnabled(getTag()) && BlockCube.getBlock() != null;
    }

    public ItemStack getStack() {
        return getStack(1);
    }

    public ItemStack getStack(int qty) {
        if (!isEnabled())
            return null;
        return new ItemStack(BlockCube.getBlock(), qty, ordinal());
    }

    @Nonnull
    @Override
    public String getName() {
        return tag;
    }
}
