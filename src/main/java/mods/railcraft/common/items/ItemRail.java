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
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public class ItemRail extends ItemRailcraft {

    public ItemRail() {
        setHasSubtypes(true);
        setMaxDamage(0);
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
    public void getSubItems(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < EnumRail.VALUES.length; i++) {
            list.add(new ItemStack(this, 1, i));
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
                'I', Items.iron_ingot);

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
                'R', new ItemStack(Items.redstone),
                'G', new ItemStack(Items.gold_ingot));

        // Wooden
        CraftingPlugin.addShapelessRecipe(item.getStack(6, EnumRail.WOOD), "ingotIron", RailcraftItems.tie.getRecipeObject(EnumTie.WOOD));

        // Speed
        recipe = new ShapedOreRecipe(item.getStack(8, EnumRail.SPEED),
                "IBG",
                "IBG",
                "IBG",
                'I', "ingotSteel",
                'B', Items.blaze_powder,
                'G', Items.gold_ingot);
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

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage < 0 || damage >= EnumRail.VALUES.length)
            return "";
        switch (EnumRail.VALUES[stack.getItemDamage()]) {
            case STANDARD:
                return "item.railcraft.part.rail.standard";
            case ADVANCED:
                return "item.railcraft.part.rail.advanced";
            case WOOD:
                return "item.railcraft.part.rail.wood";
            case SPEED:
                return "item.railcraft.part.rail.speed";
            case REINFORCED:
                return "item.railcraft.part.rail.reinforced";
            case ELECTRIC:
                return "item.railcraft.part.rail.electric";
            default:
                return "";
        }
    }

    public enum EnumRail implements IVariantEnum {

        STANDARD("ingotIron"), ADVANCED("ingotGold"), WOOD("slabWood"), SPEED("ingotSteel"), REINFORCED("ingotSteel"), ELECTRIC("ingotCopper");
        public static final EnumRail[] VALUES = values();
        private Object alternate;

        EnumRail(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate() {
            return alternate;
        }

        @Nonnull
        @Override
        public Class<? extends ItemRailcraft> getParentClass() {
            return ItemRail.class;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

}
