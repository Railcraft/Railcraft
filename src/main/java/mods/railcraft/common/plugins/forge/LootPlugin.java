/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IItemMetaEnum;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LootPlugin {

    public static final String WORKSHOP = "railcraft:workshop";

    public static void init() {
        LootPlugin.increaseLootGen(1, 2,
                ChestGenHooks.MINESHAFT_CORRIDOR,
                ChestGenHooks.VILLAGE_BLACKSMITH);
        LootPlugin.increaseLootGen(10, 16, WORKSHOP);
        addLoot(new ItemStack(Items.coal), 8, 16, Type.WORKSHOP, "fuel.coal");
    }

    public static void increaseLootGen(int min, int max, String... locations) {
        for (String location : locations) {
            ChestGenHooks lootInfo = ChestGenHooks.getInfo(location);
            lootInfo.setMin(lootInfo.getMin() + min);
            lootInfo.setMax(lootInfo.getMax() + max);
        }
    }

    private static void addLoot(ItemStack loot, int minStack, int maxStack, String tag, String... locations) {
        if (loot == null) {
            if (Game.IS_DEBUG)
                throw new RuntimeException("Invalid Loot");
            return;
        }
        WeightedRandomChestContent contents = new WeightedRandomChestContent(loot, minStack, maxStack, RailcraftConfig.getLootChance(tag));
        addLoot(contents, locations);
    }

    private static void addLoot(WeightedRandomChestContent loot, String... locations) {
        for (String location : locations) {
            ChestGenHooks.addItem(location, loot);
        }
    }

    public static void addLoot(ItemStack loot, int minStack, int maxStack, Type type, String tag) {
        addLoot(loot, minStack, maxStack, tag, type.locations);
    }

    public static void addLoot(ItemStack loot, int minStack, int maxStack, Type type) {
        addLoot(loot, minStack, maxStack, loot.getUnlocalizedName(), type.locations);
    }

    public static void addLoot(RailcraftItem item, IItemMetaEnum meta, int minStack, int maxStack, Type type) {
        addLoot(item.getStack(meta), minStack, maxStack, item.getBaseTag(), type.locations);
    }

    public static void addLootUnique(RailcraftItem item, IItemMetaEnum meta, int minStack, int maxStack, Type type) {
        ItemStack stack = item.getStack(meta);
        addLoot(stack, minStack, maxStack, stack.getUnlocalizedName(), type.locations);
    }

    public static void addLoot(RailcraftItem item, int minStack, int maxStack, Type type) {
        addLoot(item.getStack(), minStack, maxStack, item.getBaseTag(), type.locations);
    }

    public enum Type {
        WARRIOR(ChestGenHooks.VILLAGE_BLACKSMITH,
                ChestGenHooks.DUNGEON_CHEST,
                ChestGenHooks.PYRAMID_DESERT_CHEST,
                ChestGenHooks.PYRAMID_JUNGLE_CHEST,
                ChestGenHooks.STRONGHOLD_CORRIDOR,
                ChestGenHooks.STRONGHOLD_CROSSING),
        RAILWAY(ChestGenHooks.MINESHAFT_CORRIDOR,
                LootPlugin.WORKSHOP),
        WORKSHOP(LootPlugin.WORKSHOP),
        TOOL(ChestGenHooks.MINESHAFT_CORRIDOR,
                ChestGenHooks.VILLAGE_BLACKSMITH);
        private final String[] locations;

        Type(String... locations) {
            this.locations = locations;
        }

    }

}
