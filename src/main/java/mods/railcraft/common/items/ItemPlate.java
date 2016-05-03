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
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;
import java.util.Locale;

public class ItemPlate extends ItemRailcraft {

    public enum EnumPlate implements IItemMetaEnum {

        IRON("ingotIron"), STEEL("ingotSteel"), TIN("ingotTin"), COPPER("ingotCopper"), LEAD("ingotLead");
        public static final EnumPlate[] VALUES = values();
        private IIcon icon;
        private final Object alternate;

        EnumPlate(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate() {
            return alternate;
        }

        @Override
        public Class<? extends ItemRailcraft> getItemClass() {
            return ItemPlate.class;
        }

    }

    public ItemPlate() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void initItem() {
        for (EnumPlate p : EnumPlate.VALUES) {
            ItemStack stack = new ItemStack(this, 1, p.ordinal());
            RailcraftRegistry.register(stack);
        }
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (EnumPlate plate : EnumPlate.VALUES) {
            plate.icon = iconRegister.registerIcon("railcraft:part.plate." + plate.name().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        for (EnumPlate plate : EnumPlate.VALUES) {
            list.add(new ItemStack(this, 1, plate.ordinal()));
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage < 0 || damage >= EnumPlate.VALUES.length)
            return EnumPlate.IRON.icon;
        return EnumPlate.VALUES[damage].icon;
    }

    @Override
    public void defineRecipes() {
        RailcraftItem plate = RailcraftItem.plate;

        // Iron Plate
        IRecipe recipe = new ShapedOreRecipe(plate.getStack(4, EnumPlate.IRON),
                "II",
                "II",
                'I', "ingotIron");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        // Steel Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, EnumPlate.STEEL),
                "II",
                "II",
                'I', "ingotSteel");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        // Tin Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, EnumPlate.TIN),
                "IT",
                "TI",
                'I', "ingotIron",
                'T', "ingotTin");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        // Copper Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, EnumPlate.COPPER),
                "II",
                "II",
                'I', "ingotCopper");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        // Lead Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, EnumPlate.LEAD),
                "II",
                "II",
                'I', "ingotLead");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        RailcraftCraftingManager.blastFurnace.addRecipe(plate.getStack(EnumPlate.IRON), true, false, 1280, RailcraftItem.ingot.getStack(ItemIngot.EnumIngot.STEEL));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage < 0 || damage >= EnumPlate.VALUES.length)
            return "";
        switch (EnumPlate.VALUES[damage]) {
            case IRON:
                return "item.railcraft.part.plate.iron";
            case STEEL:
                return "item.railcraft.part.plate.steel";
            case TIN:
                return "item.railcraft.part.plate.tin";
            case COPPER:
                return "item.railcraft.part.plate.copper";
            case LEAD:
                return "item.railcraft.part.plate.lead";
            default:
                return "";
        }
    }

}
