/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
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
import net.minecraftforge.oredict.OreIngredient;

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

        CraftingPlugin.addRecipe(gear.getStack(1, EnumGear.BUSHING),
                "TT",
                "TT",
                'T', "ingotBronze");
        CraftingPlugin.addRecipe(gear.getStack(1, EnumGear.BUSHING),
                "TT",
                "TT",
                'T', "ingotBrass");

        Ingredient ingotBronze = new OreIngredient("ingotBronze");
        Ingredient plateBronze = new OreIngredient("plateBronze");
        Ingredient ingotBrass = new OreIngredient("ingotBrass");
        Ingredient plateBrass = new OreIngredient("plateBrass");

        Crafters.rollingMachine().newShapedRecipeBuilder()
                .output(getStack(2, EnumGear.BUSHING))
                .ingredients(
                        ingotBronze, ingotBronze,
                        ingotBronze, ingotBronze
                )
                .height(2)
                .width(2)
                .time(200)
                .buildAndRegister();

        Crafters.rollingMachine().newShapedRecipeBuilder()
                .output(getStack(2, EnumGear.BUSHING))
                .ingredients(
                        ingotBrass, ingotBrass,
                        ingotBrass, ingotBrass
                )
                .height(2)
                .width(2)
                .time(200)
                .buildAndRegister();

        Crafters.rollingMachine().newShapedRecipeBuilder()
                .output(getStack(2, EnumGear.BUSHING))
                .ingredients(
                        plateBronze, plateBronze,
                        plateBronze, plateBronze
                )
                .height(2)
                .width(2)
                .time(100)
                .buildAndRegister();

        Crafters.rollingMachine().newShapedRecipeBuilder()
                .output(getStack(2, EnumGear.BUSHING))
                .ingredients(
                        plateBrass, plateBrass,
                        plateBrass, plateBrass
                )
                .height(2)
                .width(2)
                .time(100)
                .buildAndRegister();

        CraftingPlugin.addRecipe(gear.getStack(EnumGear.BRASS),
                " I ",
                "IBI",
                " I ",
                'I', "ingotBrass",
                'B', "gearBushing");
        CraftingPlugin.addRecipe(gear.getStack(EnumGear.IRON),
                " I ",
                "IBI",
                " I ",
                'I', "ingotIron",
                'B', "gearBushing");

        CraftingPlugin.addRecipe(gear.getStack(EnumGear.STEEL),
                " I ",
                "IBI",
                " I ",
                'I', "ingotSteel",
                'B', "gearBushing");
        CraftingPlugin.addRecipe(gear.getStack(EnumGear.BRONZE),
                " I ",
                "IBI",
                " I ",
                'I', "ingotBronze",
                'B', "gearBushing");
        CraftingPlugin.addRecipe(gear.getStack(EnumGear.INVAR),
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
