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
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public class ItemPlate extends ItemRailcraft {

    public ItemPlate() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void initializeDefinintion() {
        for (EnumPlate p : EnumPlate.VALUES) {
            ItemStack stack = new ItemStack(this, 1, p.ordinal());
            RailcraftRegistry.register(stack);

            LootPlugin.addLoot(RailcraftItems.plate, p, 6, 18, LootPlugin.Type.WORKSHOP);
        }
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (EnumPlate plate : EnumPlate.VALUES) {
            list.add(new ItemStack(this, 1, plate.ordinal()));
        }
    }

    @Override
    public void defineRecipes() {
        RailcraftItems plate = RailcraftItems.plate;

        // Iron Plate
        IRecipe recipe = new ShapedOreRecipe(plate.getStack(4, EnumPlate.IRON),
                "II",
                "II",
                'I', "ingotIron");
        RailcraftCraftingManager.rollingMachine.getRecipeList().add(recipe);

        // Steel Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, EnumPlate.STEEL),
                "II",
                "II",
                'I', "ingotSteel");
        RailcraftCraftingManager.rollingMachine.addRecipe(recipe);

        // Tin Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, EnumPlate.TIN),
                "IT",
                "TI",
                'I', "ingotIron",
                'T', "ingotTin");
        RailcraftCraftingManager.rollingMachine.addRecipe(recipe);

        // Copper Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, EnumPlate.COPPER),
                "II",
                "II",
                'I', "ingotCopper");
        RailcraftCraftingManager.rollingMachine.addRecipe(recipe);

        // Lead Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, EnumPlate.LEAD),
                "II",
                "II",
                'I', "ingotLead");
        RailcraftCraftingManager.rollingMachine.addRecipe(recipe);

        RailcraftCraftingManager.blastFurnace.addRecipe(plate.getStack(EnumPlate.IRON), true, false, 1280, RailcraftItems.ingot.getStack(ItemIngot.EnumIngot.STEEL));
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

    public enum EnumPlate implements IVariantEnum {

        IRON("ingotIron"),
        STEEL("ingotSteel"),
        TIN("ingotTin"),
        COPPER("ingotCopper"),
        LEAD("ingotLead");
        public static final EnumPlate[] VALUES = values();
        private final Object alternate;

        EnumPlate(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate() {
            return alternate;
        }

        @Nonnull
        @Override
        public Class<? extends ItemRailcraft> getParentClass() {
            return ItemPlate.class;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }

        @Override
        public int getItemMeta() {
            return ordinal();
        }
    }

}
