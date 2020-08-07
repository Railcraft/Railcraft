/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Locale;

public class ItemGear extends ItemRailcraftSubtyped {

    public ItemGear() {
        super(EnumGear.class);
    }

    @Override
    public void initializeDefinition() {
        for (EnumGear gear : EnumGear.VALUES) {
            ItemStack stack = new ItemStack(this, 1, gear.ordinal());
            RailcraftRegistry.register(this, gear, stack);
        }

        OreDictionary.registerOre("gearIron", RailcraftItems.GEAR.getStack(1, EnumGear.IRON));
        OreDictionary.registerOre("gearSteel", RailcraftItems.GEAR.getStack(1, EnumGear.STEEL));
        OreDictionary.registerOre("gearBronze", RailcraftItems.GEAR.getStack(1, EnumGear.BRONZE));
        OreDictionary.registerOre("gearBrass", RailcraftItems.GEAR.getStack(1, EnumGear.BRASS));
        OreDictionary.registerOre("gearInvar", RailcraftItems.GEAR.getStack(1, EnumGear.INVAR));
        OreDictionary.registerOre("gearBushing", RailcraftItems.GEAR.getStack(1, EnumGear.BUSHING));
    }

    @Override
    public void defineRecipes() {
        RailcraftItems gear = RailcraftItems.GEAR;

        CraftingPlugin.addShapedRecipe(gear.getStack(1, EnumGear.BUSHING),
                "TT",
                "TT",
                'T', "ingotBronze");
        CraftingPlugin.addShapedRecipe(gear.getStack(1, EnumGear.BUSHING),
                "TT",
                "TT",
                'T', "ingotBrass");

        Crafters.rollingMachine().newRecipe(getStack(4, EnumGear.BUSHING))
                .time(100)
                .shaped(
                        "II",
                        "II",
                        'I', Ingredients.from("plateBronze", "plateBrass", "barBronze", "barBrass"));

        CraftingPlugin.addShapedRecipe(gear.getStack(EnumGear.BRASS),
                " I ",
                "IBI",
                " I ",
                'I', "ingotBrass",
                'B', "gearBushing");
        CraftingPlugin.addShapedRecipe(gear.getStack(EnumGear.IRON),
                " I ",
                "IBI",
                " I ",
                'I', "ingotIron",
                'B', "gearBushing");

        CraftingPlugin.addShapedRecipe(gear.getStack(EnumGear.STEEL),
                " I ",
                "IBI",
                " I ",
                'I', "ingotSteel",
                'B', "gearBushing");
        CraftingPlugin.addShapedRecipe(gear.getStack(EnumGear.BRONZE),
                " I ",
                "IBI",
                " I ",
                'I', "ingotBronze",
                'B', "gearBushing");
        CraftingPlugin.addShapedRecipe(gear.getStack(EnumGear.INVAR),
                " I ",
                "IBI",
                " I ",
                'I', "ingotInvar",
                'B', "gearBushing");
    }

    public enum EnumGear implements IVariantEnum {
        BRASS("blockBrass"),
        IRON("blockIron"),
        STEEL("blockSteel"),
        BUSHING("ingotBronze"),
        BRONZE("blockBronze"),
        INVAR("blockInvar"),
        ;
        public static final EnumGear[] VALUES = values();
        private Ingredient alternate;

        EnumGear(Object alt) {
            this.alternate = Ingredients.from(alt);
        }

        @Override
        public Ingredient getAlternate(IIngredientSource container) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH).replace('_', '.');
        }
    }

}
