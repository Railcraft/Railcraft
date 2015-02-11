/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.cube;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;

/**
 *
 * @author CovertJaguar
 */
public enum EnumCube {

    COKE_BLOCK(Module.FACTORY, "coke", new FlammableCube(5, 10), 2f, 10f),
    CONCRETE_BLOCK(Module.STRUCTURES, "concrete", new SimpleCube(), 3f, 15f),
    STEEL_BLOCK(Module.FACTORY, "steel", new SimpleCube(), 5f, 15f),
    @Deprecated
    INFERNAL_BRICK(Module.STRUCTURES, "brick.infernal", new ReplacerCube(), 3f, 15f),
    CRUSHED_OBSIDIAN(Module.FACTORY, "crushed.obsidian", new CrushedObsidian(), 2f, 45f),
    @Deprecated
    SANDY_BRICK(Module.STRUCTURES, "brick.sandy", new ReplacerCube(), 2f, 10f),
    ABYSSAL_STONE(Module.WORLD, "stone.abyssal", new SimpleCube(), 2f, 10f),
    QUARRIED_STONE(Module.WORLD, "stone.quarried", new SimpleCube(), 2f, 10f),
    CREOSOTE_BLOCK(Module.STRUCTURES, "creosote", new FlammableCube(5, 300), 3f, 10f),
    COPPER_BLOCK(Module.FACTORY, "copper", new SimpleCube(), 3f, 10f),
    TIN_BLOCK(Module.FACTORY, "tin", new SimpleCube(), 3f, 10f),
    LEAD_BLOCK(Module.FACTORY, "lead", new SimpleCube(), 2f, 20f),;
    public static final EnumCube[] VALUES = values();
    private final Module module;
    private final String tag;
    private final SimpleCube blockDef;
    private final float hardness;
    private final float resistance;
    private IIcon icon;
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
    }

    private EnumCube(Module module, String tag, SimpleCube blockDef, float hardness, float resistance) {
        this.module = module;
        this.tag = tag;
        this.blockDef = blockDef;
        this.hardness = hardness;
        this.resistance = resistance;
    }

    public static List<EnumCube> getCreativeList() {
        return creativeList;
    }

    public Module getModule() {
        return module;
    }

    public String getTag() {
        return "tile.railcraft.cube." + tag;
    }

    public SimpleCube getBlockDef() {
        return blockDef;
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistance() {
        return resistance;
    }

    public boolean isEnabled() {
        return getModule() != null && ModuleManager.isModuleLoaded(getModule()) && RailcraftConfig.isSubBlockEnabled(getTag()) && BlockCube.getBlock() != null;
    }

    public static EnumCube fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return CONCRETE_BLOCK;
        return VALUES[id];
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int qty) {
        if (!isEnabled())
            return null;
        return new ItemStack(BlockCube.getBlock(), qty, ordinal());
    }

    public void setIcon(IIcon tex) {
        this.icon = tex;
    }

    public IIcon getIcon() {
        return icon;
    }

}
