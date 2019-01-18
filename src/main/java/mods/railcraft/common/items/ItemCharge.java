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
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.item.crafting.Ingredient;

import java.util.Locale;

import static mods.railcraft.common.items.ItemCharge.EnumCharge.*;

/**
 * Created by CovertJaguar on 6/17/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCharge extends ItemRailcraftSubtyped {

    public ItemCharge() {
        super(EnumCharge.class);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(COIL),
                "CCC",
                "III",
                "CCC",
                'C', RailcraftItems.CHARGE, SPOOL_SMALL,
                'I', "plateIron");

        CraftingPlugin.addShapedRecipe(getStack(TERMINAL),
                " P ",
                "BBB",
                'B', "ingotBrass",
                'P', "plateBrass");

        CraftingPlugin.addShapedRecipe(getStack(3, SPOOL_SMALL),
                "W",
                'W', getStack(SPOOL_MEDIUM));

        CraftingPlugin.addShapedRecipe(getStack(3, SPOOL_MEDIUM),
                "W",
                'W', getStack(SPOOL_LARGE));


        //TODO configure time
        Crafters.rollingMachine().newRecipe(getStack(SPOOL_SMALL))
                .shapeless("ingotCopper");

        Crafters.rollingMachine().newRecipe(getStack(SPOOL_SMALL))
                .shapeless("ingotConductiveIron");

        Crafters.rollingMachine().newRecipe(getStack(SPOOL_LARGE))
                .time(300)
                .shapeless("blockCopper");

        Crafters.rollingMachine().newRecipe(getStack(SPOOL_LARGE))
                .time(300)
                .shapeless("blockConductiveIron");

        CraftingPlugin.addShapedRecipe(getStack(MOTOR),
                " S ",
                "PCP",
                " T ",
                'C', RailcraftItems.CHARGE, COIL,
                'S', "ingotSteel",
                'T', RailcraftItems.CHARGE, TERMINAL,
                'P', "plateTin");

        Crafters.rollingMachine().newRecipe(
                getStack(ELECTRODE_NICKEL)).shaped(
                " P ",
                " P ",
                " P ",
                'P', "plateNickel");

        Crafters.rollingMachine().newRecipe(
                getStack(ELECTRODE_IRON)).shaped(
                " P ",
                " P ",
                " P ",
                'P', "plateIron");

        Crafters.rollingMachine().newRecipe(
                getStack(ELECTRODE_ZINC)).shaped(
                " P ",
                " P ",
                " P ",
                'P', "plateZinc");

        Crafters.rollingMachine().newRecipe(
                getStack(ELECTRODE_CARBON)).shaped(
                " P ",
                " P ",
                " P ",
                'P', "coal");

        Crafters.rollingMachine().newRecipe(
                getStack(ELECTRODE_SILVER)).shaped(
                " P ",
                " P ",
                " P ",
                'P', "plateSilver");
    }

    public enum EnumCharge implements IVariantEnum {

        COIL("blockCopper"),
        TERMINAL("plateBrass"),
        SPOOL_SMALL("ingotCopper"),
        SPOOL_MEDIUM("blockCopper"),
        SPOOL_LARGE("blockCopper"),
        MOTOR("blockCopper"),
        ELECTRODE_NICKEL("plateNickel"),
        ELECTRODE_IRON("plateIron"),
        ELECTRODE_ZINC("plateZinc"),
        ELECTRODE_CARBON("coal"),
        ELECTRODE_SILVER("plateSilver"),
        ;
        public static EnumCharge[] VALUES = values();
        private final Ingredient alternate;

        EnumCharge(Object alt) {
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
