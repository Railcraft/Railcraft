/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import mods.railcraft.api.crafting.IRollingMachineCrafter;
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

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

        // Standard
        Crafters.rollingMachine().newRecipe(item.getStack(8, EnumRail.STANDARD))
                .shaped(
                        "I I",
                        "I I",
                        "I I",
                        'I', Metal.Form.INGOT, Metal.IRON);

        Crafters.rollingMachine().newRecipe(item.getStack(8, EnumRail.STANDARD)).shaped(
                "I I",
                "I I",
                "I I",
                'I', Metal.Form.INGOT, Metal.BRONZE);

        Crafters.rollingMachine().newRecipe(item.getStack(12, EnumRail.STANDARD))
                .name("railcraft", "rail_invar")
                .time((int) (IRollingMachineCrafter.DEFAULT_PROCESS_TIME * 1.5))
                .shaped(
                        "I I",
                        "I I",
                        "I I",
                        'I', Metal.Form.INGOT, Metal.INVAR);

        Crafters.rollingMachine().newRecipe(item.getStack(16, EnumRail.STANDARD))
                .name("railcraft", "rail_steel")
                .time(IRollingMachineCrafter.DEFAULT_PROCESS_TIME * 2)
                .shaped(
                        "I I",
                        "I I",
                        "I I",
                        'I', Metal.Form.INGOT, Metal.STEEL);

        Crafters.rollingMachine().newRecipe(item.getStack(32, EnumRail.STANDARD))
                .name("railcraft", "rail_tungsten")
                .time(IRollingMachineCrafter.DEFAULT_PROCESS_TIME * 4)
                .shaped(
                        "I I",
                        "I I",
                        "I I",
                        'I', "ingotTungsten");

        Crafters.rollingMachine().newRecipe(item.getStack(32, EnumRail.STANDARD))
                .name("railcraft:rail_titanium")
                .time(IRollingMachineCrafter.DEFAULT_PROCESS_TIME * 4)
                .shaped(
                        "I I",
                        "I I",
                        "I I",
                        'I', "ingotTitanium");

        Crafters.rollingMachine().newRecipe(item.getStack(48, EnumRail.STANDARD))
                .name("railcraft:rail_tungsten_steel")
                .time(IRollingMachineCrafter.DEFAULT_PROCESS_TIME * 6)
                .shaped(
                        "I I",
                        "I I",
                        "I I",
                        'I', "ingotTungstensteel");

        // Advanced
        Crafters.rollingMachine().newRecipe(item.getStack(8, EnumRail.ADVANCED)).shaped(
                "R G",
                "R G",
                "R G",
                'R', new ItemStack(Items.REDSTONE),
                'G', new ItemStack(Items.GOLD_INGOT));

        // Wooden
        CraftingPlugin.addShapelessRecipe(item.getStack(6, EnumRail.WOOD), "ingotIron", RailcraftItems.TIE.getIngredient(EnumTie.WOOD));

        CraftingPlugin.addShapelessRecipe(item.getStack(6, EnumRail.WOOD), "ingotBronze", RailcraftItems.TIE.getIngredient(EnumTie.WOOD));

        // Speed
        Crafters.rollingMachine().newRecipe(item.getStack(8, EnumRail.SPEED)).shaped(
                "IBG",
                "IBG",
                "IBG",
                'I', "ingotSteel",
                'B', Items.BLAZE_POWDER,
                'G', Items.GOLD_INGOT);

        // Reinforced
        Crafters.rollingMachine().newRecipe(item.getStack(8, EnumRail.REINFORCED)).shaped(
                "IDI",
                "IDI",
                "IDI",
                'I', "ingotSteel",
                'D', "dustObsidian");

        Crafters.rollingMachine().newRecipe(item.getStack(8, EnumRail.REINFORCED)).shaped(
                "I I",
                "I I",
                "I I",
                'I', "ingotDarkSteel");

        Crafters.rollingMachine().newRecipe(item.getStack(4, EnumRail.REINFORCED)).shaped(
                "IDI",
                "IDI",
                "IDI",
                'I', "ingotInvar",
                'D', "dustObsidian");

        Crafters.rollingMachine().newRecipe(item.getStack(16, EnumRail.REINFORCED)).shaped(
                "IDI",
                "IDI",
                "IDI",
                'I', "ingotTungstensteel",
                'D', "dustObsidian");

        // Electric
        Crafters.rollingMachine().newRecipe(item.getStack(6, EnumRail.ELECTRIC)).shaped(
                "I I",
                "I I",
                "I I",
                'I', "ingotCopper");

        Crafters.rollingMachine().newRecipe(item.getStack(12, EnumRail.ELECTRIC)).shaped(
                "ICI",
                "ICI",
                "ICI",
                'I', "ingotSteel",
                'C', "ingotCopper");

        Crafters.rollingMachine().newRecipe(item.getStack(12, EnumRail.ELECTRIC)).shaped(
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
