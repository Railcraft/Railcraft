/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RailcraftToolItems {

    private static Item itemSteelShears;
    private static Item itemSteelSword;
    private static Item itemSteelShovel;
    private static Item itemSteelPickaxe;
    private static Item itemSteelAxe;
    private static Item itemSteelHoe;
    private static Item itemSteelHelmet;
    private static Item itemSteelArmor;
    private static Item itemSteelLegs;
    private static Item itemSteelBoots;
    private static Item itemCoalCoke;

    public static void initializeToolsArmor() {
        registerSteelShears();
        registerSteelSword();
        registerSteelShovel();
        registerSteelPickaxe();
        registerSteelAxe();
        registerSteelHoe();

        registerSteelHelmet();
        registerSteelArmor();
        registerSteelLegs();
        registerSteelBoots();

    }

    private static void registerSteelShears() {
        Item item = itemSteelShears;
        if (item == null) {
            String tag = "railcraft.tool.steel.shears";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelShears = new ItemSteelShears();
                RailcraftRegistry.register(item);

                CraftingPlugin.addRecipe(new ItemStack(item), false,
                        " I",
                        "I ",
                        'I', "ingotSteel");

                LootPlugin.addLoot(new ItemStack(item), 1, 1, LootPlugin.Type.TOOL, tag);
            }
        }
    }

    public static ItemStack getSteelShears() {
        if (itemSteelShears == null)
            return new ItemStack(Items.SHEARS);
        return new ItemStack(itemSteelShears);
    }

    private static void registerSteelSword() {
        Item item = itemSteelSword;
        if (item == null) {
            String tag = "railcraft.tool.steel.sword";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelSword = new ItemSteelSword();
                RailcraftRegistry.register(item);

                CraftingPlugin.addRecipe(new ItemStack(item), false,
                        " I ",
                        " I ",
                        " S ",
                        'I', "ingotSteel",
                        'S', "stickWood");

                LootPlugin.addLoot(new ItemStack(item), 1, 1, LootPlugin.Type.WARRIOR, tag);
            }
        }
    }

    public static ItemStack getSteelSword() {
        if (itemSteelSword == null)
            return new ItemStack(Items.IRON_SWORD);
        return new ItemStack(itemSteelSword);
    }

    private static void registerSteelShovel() {
        Item item = itemSteelShovel;
        if (item == null) {
            String tag = "railcraft.tool.steel.shovel";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelShovel = new ItemSteelShovel();
                RailcraftRegistry.register(item);
                HarvestPlugin.setToolClass(item, "shovel", 2);

                IRecipe recipe = new ShapedOreRecipe(new ItemStack(item), false, new Object[]{
                        " I ",
                        " S ",
                        " S ",
                        'I', "ingotSteel",
                        'S', "stickWood"
                });
                CraftingManager.getInstance().getRecipeList().add(recipe);

                LootPlugin.addLoot(new ItemStack(item), 1, 1, LootPlugin.Type.TOOL, tag);
            }
        }
    }

    public static ItemStack getSteelShovel() {
        if (itemSteelShovel == null)
            return new ItemStack(Items.IRON_SHOVEL);
        return new ItemStack(itemSteelShovel);
    }

    private static void registerSteelPickaxe() {
        Item item = itemSteelPickaxe;
        if (item == null) {
            String tag = "railcraft.tool.steel.pickaxe";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelPickaxe = new ItemSteelPickaxe();
                RailcraftRegistry.register(item);
                HarvestPlugin.setToolClass(item, "pickaxe", 2);

                IRecipe recipe = new ShapedOreRecipe(new ItemStack(item), false, new Object[]{
                        "III",
                        " S ",
                        " S ",
                        'I', "ingotSteel",
                        'S', "stickWood"
                });
                CraftingManager.getInstance().getRecipeList().add(recipe);

                LootPlugin.addLoot(new ItemStack(item), 1, 1, LootPlugin.Type.TOOL, tag);
            }
        }
    }

    public static ItemStack getSteelPickaxe() {
        if (itemSteelPickaxe == null)
            return new ItemStack(Items.IRON_PICKAXE);
        return new ItemStack(itemSteelPickaxe);
    }

    private static void registerSteelAxe() {
        Item item = itemSteelAxe;
        if (item == null) {
            String tag = "railcraft.tool.steel.axe";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelAxe = new ItemSteelAxe();
                RailcraftRegistry.register(item);
                HarvestPlugin.setToolClass(item, "axe", 2);

                CraftingPlugin.addRecipe(new ItemStack(item), true,
                        "II ",
                        "IS ",
                        " S ",
                        'I', "ingotSteel",
                        'S', "stickWood");

                LootPlugin.addLoot(new ItemStack(item), 1, 1, LootPlugin.Type.TOOL, tag);
            }
        }
    }

    public static ItemStack getSteelAxe() {
        if (itemSteelAxe == null)
            return new ItemStack(Items.IRON_AXE);
        return new ItemStack(itemSteelAxe);
    }

    private static void registerSteelHoe() {
        Item item = itemSteelHoe;
        if (item == null) {
            String tag = "railcraft.tool.steel.hoe";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelHoe = new ItemSteelHoe();
                RailcraftRegistry.register(item);

                IRecipe recipe = new ShapedOreRecipe(new ItemStack(item), true, new Object[]{
                        "II ",
                        " S ",
                        " S ",
                        'I', "ingotSteel",
                        'S', "stickWood"
                });
                CraftingManager.getInstance().getRecipeList().add(recipe);
            }
        }
    }

    public static ItemStack getSteelHoe() {
        if (itemSteelHoe == null)
            return new ItemStack(Items.IRON_HOE);
        return new ItemStack(itemSteelHoe);
    }

    private static void registerSteelHelmet() {
        Item item = itemSteelHelmet;
        if (item == null) {
            String tag = "railcraft.armor.steel.helmet";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelHelmet = new ItemSteelArmor(EntityEquipmentSlot.HEAD);
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                CraftingPlugin.addRecipe(new ItemStack(item), true, new Object[]{
                        "III",
                        "I I",
                        'I', "ingotSteel",});

                LootPlugin.addLoot(new ItemStack(item), 1, 1, LootPlugin.Type.WARRIOR, tag);
            }
        }
    }

    public static ItemStack getSteelHelm() {
        if (itemSteelHelmet == null)
            return null;
        return new ItemStack(itemSteelHelmet);
    }

    private static void registerSteelArmor() {
        Item item = itemSteelArmor;
        if (item == null) {
            String tag = "railcraft.armor.steel.plate";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelArmor = new ItemSteelArmor(EntityEquipmentSlot.CHEST);
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                CraftingPlugin.addRecipe(new ItemStack(item), true, new Object[]{
                        "I I",
                        "III",
                        "III",
                        'I', "ingotSteel",});

                LootPlugin.addLoot(new ItemStack(item), 1, 1, LootPlugin.Type.WARRIOR, tag);
            }
        }
    }

    public static ItemStack getSteelArmor() {
        if (itemSteelArmor == null)
            return null;
        return new ItemStack(itemSteelArmor);
    }

    private static void registerSteelLegs() {
        Item item = itemSteelLegs;
        if (item == null) {
            String tag = "railcraft.armor.steel.legs";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelLegs = new ItemSteelArmor(EntityEquipmentSlot.LEGS);
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                CraftingPlugin.addRecipe(new ItemStack(item), true, new Object[]{
                        "III",
                        "I I",
                        "I I",
                        'I', "ingotSteel",});

                LootPlugin.addLoot(new ItemStack(item), 1, 1, LootPlugin.Type.WARRIOR, tag);
            }
        }
    }

    public static ItemStack getSteelLegs() {
        if (itemSteelLegs == null)
            return null;
        return new ItemStack(itemSteelLegs);
    }

    private static void registerSteelBoots() {
        Item item = itemSteelBoots;
        if (item == null) {
            String tag = "railcraft.armor.steel.boots";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelBoots = new ItemSteelArmor(EntityEquipmentSlot.FEET);
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                CraftingPlugin.addRecipe(new ItemStack(item), true, new Object[]{
                        "I I",
                        "I I",
                        'I', "ingotSteel",});

                LootPlugin.addLoot(new ItemStack(item), 1, 1, LootPlugin.Type.WARRIOR, tag);
            }
        }
    }

    public static ItemStack getSteelBoots() {
        if (itemSteelBoots == null)
            return null;
        return new ItemStack(itemSteelBoots);
    }

}
