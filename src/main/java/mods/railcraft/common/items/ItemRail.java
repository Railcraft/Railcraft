/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Locale;

public class ItemRail extends ItemRailcraftSubtyped {

    public ItemRail() {
        super(EnumRail.class);
    }

    @Override
    public void initializeDefinintion() {
        for (EnumRail rail : EnumRail.VALUES) {
            ItemStack stack = new ItemStack(this, 1, rail.ordinal());
            RailcraftRegistry.register(stack);
            LootPlugin.addLoot(RailcraftItems.rail, rail, 6, 18, LootPlugin.Type.RAILWAY);
        }
    }

    @Override
    public void defineRecipes() {
        RailcraftItems item = RailcraftItems.rail;

        // Standard
        RailcraftCraftingManager.rollingMachine.addRecipe(item.getStack(8, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', Items.IRON_INGOT);

        IRecipe recipe = new ShapedOreRecipe(item.getStack(6, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotBronze");
        RollingMachineCraftingManager.instance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(item.getStack(16, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotSteel");
        RollingMachineCraftingManager.instance().getRecipeList().add(recipe);

        // Advanced
        RailcraftCraftingManager.rollingMachine.addRecipe(item.getStack(8, EnumRail.ADVANCED),
                "IRG",
                "IRG",
                "IRG",
                'I', item.getRecipeObject(EnumRail.STANDARD),
                'R', new ItemStack(Items.REDSTONE),
                'G', new ItemStack(Items.GOLD_INGOT));

        // Wooden
        CraftingPlugin.addShapelessRecipe(item.getStack(6, EnumRail.WOOD), "ingotIron", RailcraftItems.tie.getRecipeObject(EnumTie.WOOD));

        // Speed
        recipe = new ShapedOreRecipe(item.getStack(8, EnumRail.SPEED),
                "IBG",
                "IBG",
                "IBG",
                'I', "ingotSteel",
                'B', Items.BLAZE_POWDER,
                'G', Items.GOLD_INGOT);
        RollingMachineCraftingManager.instance().getRecipeList().add(recipe);

        // Reinforced
        recipe = new ShapedOreRecipe(item.getStack(8, EnumRail.REINFORCED),
                "IDI",
                "IDI",
                "IDI",
                'I', "ingotSteel",
                'D', "dustObsidian");
        RollingMachineCraftingManager.instance().getRecipeList().add(recipe);

        // Electric
        recipe = new ShapedOreRecipe(item.getStack(6, EnumRail.ELECTRIC),
                "ICI",
                "ICI",
                "ICI",
                'I', item.getRecipeObject(EnumRail.STANDARD),
                'C', "ingotCopper");
        RollingMachineCraftingManager.instance().getRecipeList().add(recipe);
    }

    public enum EnumRail implements IVariantEnum {

        STANDARD("ingotIron"), ADVANCED("ingotGold"), WOOD("slabWood"), SPEED("ingotSteel"), REINFORCED("ingotSteel"), ELECTRIC("ingotCopper");
        public static final EnumRail[] VALUES = values();
        private Object alternate;

        EnumRail(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate(IRailcraftObjectContainer container) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
