/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.lantern;

import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.blocks.aesthetics.slab.BlockRailcraftSlab;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumLanternMetal implements LanternInfo {

    IRON,
    GOLD,
    COPPER,
    TIN,
    LEAD,
    STEEL;
    public static final EnumLanternMetal[] VALUES = values();
    public static final Map<String, EnumLanternMetal> NAMES = new HashMap<String, EnumLanternMetal>();
    public static final List<EnumLanternMetal> creativeList = new ArrayList<EnumLanternMetal>();
    private ItemStack source;

    public static void initialize() {
        IRON.source = BlockRailcraftSlab.getItem(EnumBlockMaterial.IRON);
        GOLD.source = BlockRailcraftSlab.getItem(EnumBlockMaterial.GOLD);
        COPPER.source = BlockRailcraftSlab.getItem(EnumBlockMaterial.COPPER);
        TIN.source = BlockRailcraftSlab.getItem(EnumBlockMaterial.TIN);
        LEAD.source = BlockRailcraftSlab.getItem(EnumBlockMaterial.LEAD);
        STEEL.source = BlockRailcraftSlab.getItem(EnumBlockMaterial.STEEL);

        for (EnumLanternMetal lamp : VALUES) {
            NAMES.put(lamp.name(), lamp);

            if (lamp.isEnabled() && lamp.source != null)
                CraftingPlugin.addShapedRecipe(lamp.getItem(1), " S ", " T ", " S ", 'S', lamp.getSource(), 'T', new ItemStack(Blocks.torch));
            creativeList.add(lamp);
        }
    }

    public static LanternInfo fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return VALUES[0];
        return VALUES[id];
    }

    public static EnumLanternMetal fromName(String name) {
        EnumLanternMetal lamp = NAMES.get(name);
        if (lamp != null)
            return lamp;
        return IRON;
    }


    @Override
    public IIcon getTexture(int side) {
        return InvTools.getBlockFromStack(source).getIcon(ForgeDirection.UP.ordinal(), source.getItemDamage());
    }

    @Override
    public Block getBlock() {
        return BlockLantern.getBlockMetal();
    }

    @Override
    public ItemStack getSource() {
        if (source == null) return null;
        return source.copy();
    }

    @Override
    public ItemStack getItem() {
        return getItem(1);
    }

    @Override
    public ItemStack getItem(int qty) {
        Block block = getBlock();
        if (block == null) return null;
        return new ItemStack(block, qty, ordinal());
    }

    @Override
    public String getTag() {
        return "railcraft.lantern.metal." + name().replace("_", ".").toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean isEnabled() {
        return ModuleManager.isModuleLoaded(ModuleManager.Module.STRUCTURES) && RailcraftConfig.isSubBlockEnabled(getTag()) && getBlock() != null;
    }

}
