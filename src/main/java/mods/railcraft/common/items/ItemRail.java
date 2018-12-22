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
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;

import java.util.Locale;

public class ItemRail extends ItemRailcraftSubtyped {

    public ItemRail() {
        super(EnumRail.class);
    }

    @Override
    public void initializeDefinition() {
        for (EnumRail rail : EnumRail.VALUES) {
            ItemStack stack = new ItemStack(this, 1, rail.ordinal());
            RailcraftRegistry.register(this, rail, stack);
        }
    }

    @Override
    public void defineRecipes() {
        RailcraftItems item = RailcraftItems.RAIL;

        Ingredient ironIngot = new OreIngredient("ingotIron");
        Crafters.rollingMachine().newShapedRecipeBuilder()
                .grid(new Ingredient[][]{
                        {ironIngot, Ingredient.EMPTY, ironIngot},
                        {ironIngot, Ingredient.EMPTY, ironIngot},
                        {ironIngot, Ingredient.EMPTY, ironIngot},
                })
                .output(item.getStack(8, EnumRail.STANDARD))
                .buildAndRegister();

        // Standard
//        Crafters.rollingMachine().addRecipe(item.getStack(8, EnumRail.STANDARD),
//                "I I",
//                "I I",
//                "I I",
//                'I', Items.IRON_INGOT);

        Crafters.rollingMachine().addRecipe(item.getStack(8, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotBronze");

        Crafters.rollingMachine().addRecipe(item.getStack(12, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotInvar");

        Crafters.rollingMachine().addRecipe(item.getStack(16, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotSteel");

        Crafters.rollingMachine().addRecipe(item.getStack(32, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotTungsten");

        Crafters.rollingMachine().addRecipe(item.getStack(32, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotTitanium");

        Crafters.rollingMachine().addRecipe(item.getStack(48, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotTungstensteel");

        // Advanced
        Crafters.rollingMachine().addRecipe(item.getStack(8, EnumRail.ADVANCED),
                "R G",
                "R G",
                "R G",
                'R', new ItemStack(Items.REDSTONE),
                'G', new ItemStack(Items.GOLD_INGOT));

        // Wooden
        CraftingPlugin.addShapelessRecipe(item.getStack(6, EnumRail.WOOD), "ingotIron", RailcraftItems.TIE.getIngredient(EnumTie.WOOD));

        CraftingPlugin.addShapelessRecipe(item.getStack(6, EnumRail.WOOD), "ingotBronze", RailcraftItems.TIE.getIngredient(EnumTie.WOOD));

        // Speed
        Crafters.rollingMachine().addRecipe(item.getStack(8, EnumRail.SPEED),
                "IBG",
                "IBG",
                "IBG",
                'I', "ingotSteel",
                'B', Items.BLAZE_POWDER,
                'G', Items.GOLD_INGOT);

        // Reinforced
        Crafters.rollingMachine().addRecipe(item.getStack(8, EnumRail.REINFORCED),
                "IDI",
                "IDI",
                "IDI",
                'I', "ingotSteel",
                'D', "dustObsidian");

        Crafters.rollingMachine().addRecipe(item.getStack(8, EnumRail.REINFORCED),
                "I I",
                "I I",
                "I I",
                'I', "ingotDarkSteel");

        Crafters.rollingMachine().addRecipe(item.getStack(4, EnumRail.REINFORCED),
                "IDI",
                "IDI",
                "IDI",
                'I', "ingotInvar",
                'D', "dustObsidian");

        Crafters.rollingMachine().addRecipe(item.getStack(16, EnumRail.REINFORCED),
                "IDI",
                "IDI",
                "IDI",
                'I', "ingotTungstensteel",
                'D', "dustObsidian");

        // Electric
        Crafters.rollingMachine().addRecipe(item.getStack(6, EnumRail.ELECTRIC),
                "I I",
                "I I",
                "I I",
                'I', "ingotCopper");

        Crafters.rollingMachine().addRecipe(item.getStack(12, EnumRail.ELECTRIC),
                "ICI",
                "ICI",
                "ICI",
                'I', "ingotSteel",
                'C', "ingotCopper");

        Crafters.rollingMachine().addRecipe(item.getStack(12, EnumRail.ELECTRIC),
                "ICI",
                "ICI",
                "ICI",
                'I', "ingotElectricalSteel",
                'C', "ingotConductiveIron");
    }

    public enum EnumRail implements IVariantEnum {

        STANDARD("ingotIron"),
        ADVANCED("ingotGold"),
        WOOD("slabWood"),
        SPEED("ingotSteel"),
        REINFORCED(Blocks.OBSIDIAN),
        ELECTRIC("ingotCopper");
        public static final EnumRail[] VALUES = values();
        private Ingredient alternate;

        EnumRail(Object alt) {
            this.alternate = Ingredients.from(alt);
        }

        @Override
        public Ingredient getAlternate(IIngredientSource container) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
