/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.lantern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mods.railcraft.common.blocks.aesthetics.brick.BlockBrick;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.brick.EnumBrick;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumLanternStone implements LanternInfo {

    ABYSSAL,
    BLEACHEDBONE,
    BLOODSTAINED,
    FROSTBOUND,
    INFERNAL,
    NETHER,
    QUARRIED,
    SANDY,
    SANDSTONE,
    STONE;
    public static final EnumLanternStone[] VALUES = values();
    public static final Map<String, EnumLanternStone> NAMES = new HashMap<String, EnumLanternStone>();
    public static final List<EnumLanternStone> creativeList = new ArrayList<EnumLanternStone>();
    private ItemStack source;

    public static void initialize() {
        ABYSSAL.source = EnumBrick.ABYSSAL.get(BrickVariant.BLOCK, 1);
        BLEACHEDBONE.source = EnumBrick.BLEACHEDBONE.get(BrickVariant.BLOCK, 1);
        BLOODSTAINED.source = EnumBrick.BLOODSTAINED.get(BrickVariant.BLOCK, 1);
        FROSTBOUND.source = EnumBrick.FROSTBOUND.get(BrickVariant.BLOCK, 1);
        INFERNAL.source = EnumBrick.INFERNAL.get(BrickVariant.BLOCK, 1);
        NETHER.source = EnumBrick.NETHER.get(BrickVariant.BLOCK, 1);
        QUARRIED.source = EnumBrick.QUARRIED.get(BrickVariant.BLOCK, 1);
        SANDY.source = EnumBrick.SANDY.get(BrickVariant.BLOCK, 1);
        SANDSTONE.source = new ItemStack(Blocks.stone_slab, 1, 1);
        STONE.source = new ItemStack(Blocks.stone_slab, 1, 0);

        for (EnumLanternStone lamp : VALUES) {
            NAMES.put(lamp.name(), lamp);

            if (lamp.isEnabled() && lamp.source != null)
                CraftingPlugin.addShapedRecipe(lamp.getItem(1), " S ", " T ", " S ", 'S', lamp.getSource(), 'T', new ItemStack(Blocks.torch));
            creativeList.add(lamp);
        }
    }

    public static EnumLanternStone fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return VALUES[0];
        return VALUES[id];
    }

    public static EnumLanternStone fromName(String name) {
        EnumLanternStone lamp = NAMES.get(name);
        if (lamp != null)
            return lamp;
        return ABYSSAL;
    }

    @Override
    public IIcon getTexture(int side) {
        return InvTools.getBlockFromStack(source).getIcon(ForgeDirection.UP.ordinal(), source.getItemDamage());
    }

    @Override
    public Block getBlock() {
        return BlockLantern.getBlockStone();
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
        return "railcraft.lantern.stone." + name().replace("_", ".").toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean isEnabled() {
        return ModuleManager.isModuleLoaded(ModuleManager.Module.STRUCTURES) && RailcraftConfig.isSubBlockEnabled(getTag()) && getBlock() != null;
    }

}
