/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Items;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LootPlugin {

    public static final String WORKSHOP = "railcraft:workshop";

    public static void init() {
        LootPlugin.increaseLootGen(1, 2,
                ChestGenHooks.MINESHAFT_CORRIDOR,
                ChestGenHooks.VILLAGE_BLACKSMITH);
        LootPlugin.increaseLootGen(10, 16, WORKSHOP);
        addLootWorkshop(new ItemStack(Items.coal), 8, 16, "fuel.coal");
    }

    public static void increaseLootGen(int min, int max, String... locations) {
        for (String location : locations) {
            ChestGenHooks lootInfo = ChestGenHooks.getInfo(location);
            lootInfo.setMin(lootInfo.getMin() + min);
            lootInfo.setMax(lootInfo.getMax() + max);
        }
    }

    public static void addLoot(ItemStack loot, int minStack, int maxStack, String tag, String... locations) {
        if (loot == null) {
            if (Game.IS_DEBUG)
                throw new RuntimeException("Invalid Loot");
            return;
        }
        WeightedRandomChestContent contents = new WeightedRandomChestContent(loot, minStack, maxStack, RailcraftConfig.getLootChance(tag));
        addLoot(contents, locations);
    }

    public static void addLoot(WeightedRandomChestContent loot, String... locations) {
        for (String location : locations) {
            ChestGenHooks.addItem(location, loot);
        }
    }

    public static void addLootWarrior(ItemStack loot, int minStack, int maxStack, String tag) {
        addLoot(loot, minStack, maxStack, tag,
                ChestGenHooks.VILLAGE_BLACKSMITH,
                ChestGenHooks.DUNGEON_CHEST,
                ChestGenHooks.PYRAMID_DESERT_CHEST,
                ChestGenHooks.PYRAMID_JUNGLE_CHEST,
                ChestGenHooks.STRONGHOLD_CORRIDOR,
                ChestGenHooks.STRONGHOLD_CROSSING);
    }

    public static void addLootRailway(ItemStack loot, int minStack, int maxStack, String tag) {
        addLoot(loot, minStack, maxStack, tag, ChestGenHooks.MINESHAFT_CORRIDOR, WORKSHOP);
    }

    public static void addLootWorkshop(ItemStack loot, int minStack, int maxStack, String tag) {
        addLoot(loot, minStack, maxStack, tag, WORKSHOP);
    }

    public static void addLootTool(ItemStack loot, int minStack, int maxStack, String tag) {
        addLoot(loot, minStack, maxStack, tag,
                ChestGenHooks.MINESHAFT_CORRIDOR,
                ChestGenHooks.VILLAGE_BLACKSMITH);
    }

}
