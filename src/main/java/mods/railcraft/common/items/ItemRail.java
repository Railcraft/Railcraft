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
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;
import java.util.Locale;

public class ItemRail extends ItemRailcraft {

    public enum EnumRail implements IItemMetaEnum {

        STANDARD("ingotIron"), ADVANCED("ingotGold"), WOOD("slabWood"), SPEED("ingotSteel"), REINFORCED("ingotSteel"), ELECTRIC("ingotCopper");
        public static final EnumRail[] VALUES = values();
        private IIcon icon;
        private Object alternate;

        EnumRail(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate() {
            return alternate;
        }

        @Override
        public Class<? extends ItemRailcraft> getItemClass() {
            return ItemRail.class;
        }

    }

    ;

    public ItemRail() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void initItem() {
        for (int meta = 0; meta < 5; meta++) {
            ItemStack stack = new ItemStack(this, 1, meta);
            RailcraftRegistry.register(stack);
            LootPlugin.addLootWorkshop(stack, 6, 18, "rail.part");
        }
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (EnumRail rail : EnumRail.VALUES) {
            rail.icon = iconRegister.registerIcon("railcraft:part.rail." + rail.name().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        for (int i = 0; i < EnumRail.VALUES.length; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public void defineRecipes() {
        RailcraftItem item = RailcraftItem.rail;

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
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(item.getStack(16, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotSteel");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        // Advanced
        RailcraftCraftingManager.rollingMachine.addRecipe(item.getStack(8, EnumRail.ADVANCED),
                "IRG",
                "IRG",
                "IRG",
                'I', item.getRecipeObject(EnumRail.STANDARD),
                'R', new ItemStack(Items.redstone),
                'G', new ItemStack(Items.gold_ingot));

        // Wooden
        CraftingPlugin.addShapelessRecipe(item.getStack(6, EnumRail.WOOD), "ingotIron", RailcraftItem.tie.getRecipeObject(EnumTie.WOOD));

        // Speed
        recipe = new ShapedOreRecipe(item.getStack(8, EnumRail.SPEED),
                "IBG",
                "IBG",
                "IBG",
                'I', "ingotSteel",
                'B', Items.blaze_powder,
                'G', Items.gold_ingot);
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        // Reinforced
        recipe = new ShapedOreRecipe(item.getStack(8, EnumRail.REINFORCED),
                "IDI",
                "IDI",
                "IDI",
                'I', "ingotSteel",
                'D', "dustObsidian");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        // Electric
        recipe = new ShapedOreRecipe(item.getStack(6, EnumRail.ELECTRIC),
                "ICI",
                "ICI",
                "ICI",
                'I', item.getRecipeObject(EnumRail.STANDARD),
                'C', "ingotCopper");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage < 0 || damage >= EnumRail.VALUES.length)
            return EnumRail.STANDARD.icon;
        return EnumRail.VALUES[damage].icon;
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

}
