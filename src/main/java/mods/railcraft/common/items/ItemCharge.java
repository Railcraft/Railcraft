/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;

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
        CraftingPlugin.addRecipe(getStack(COIL),
                "CCC",
                "III",
                "CCC",
                'C', RailcraftItems.CHARGE, SPOOL_SMALL,
                'I', "plateIron");

        CraftingPlugin.addRecipe(getStack(TERMINAL),
                " P ",
                "BBB",
                'B', "ingotBrass",
                'P', "plateBrass");

        CraftingPlugin.addRecipe(getStack(3, SPOOL_SMALL),
                "W",
                'W', getStack(SPOOL_MEDIUM));

        CraftingPlugin.addRecipe(getStack(3, SPOOL_MEDIUM),
                "W",
                'W', getStack(SPOOL_LARGE));

        Ingredient ingotCopper = new OreIngredient("ingotCopper");

        //TODO configure time
        RollingMachineCraftingManager.getInstance().newShapelessRecipeBuilder()
                .output(getStack(SPOOL_SMALL))
                .add(ingotCopper)
                .buildAndRegister();

//        RailcraftCraftingManager.rollingMachine.addRecipe(
//                getStack(SPOOL_SMALL),
//                "C",
//                'C', "ingotCopper");

        Ingredient blockCopper = new OreIngredient("blockCopper");

        //TODO configure time
        RollingMachineCraftingManager.getInstance().newShapelessRecipeBuilder()
                .output(getStack(SPOOL_LARGE))
                .add(blockCopper)
                .buildAndRegister();

//        RailcraftCraftingManager.rollingMachine.addRecipe(
//                getStack(SPOOL_LARGE),
//                "C",
//                'C', "blockCopper");

        CraftingPlugin.addRecipe(getStack(MOTOR),
                " S ",
                "PCP",
                " T ",
                'C', RailcraftItems.CHARGE, COIL,
                'S', "ingotSteel",
                'T', RailcraftItems.CHARGE, TERMINAL,
                'P', "plateTin");

        Ingredient plateNickel = new OreIngredient("plateNickel");

        //TODO configure time
        RollingMachineCraftingManager.getInstance().newShapedRecipeBuilder()
                .output(getStack(ELECTRODE_NICKEL))
                .ingredients(
                        plateNickel,
                        plateNickel,
                        plateNickel
                )
                .width(1)
                .height(3)
                .buildAndRegister();

//        RailcraftCraftingManager.rollingMachine.addRecipe(
//                getStack(ELECTRODE_NICKEL),
//                "P",
//                "P",
//                "P",
//                'P', "plateNickel");

        Ingredient plateIron = new OreIngredient("plateIron");

        RollingMachineCraftingManager.getInstance().newShapedRecipeBuilder()
                .output(getStack(ELECTRODE_IRON))
                .ingredients(
                        plateIron,
                        plateIron,
                        plateIron
                )
                .height(3)
                .width(1)
                .buildAndRegister();

//        RailcraftCraftingManager.rollingMachine.addRecipe(
//                getStack(ELECTRODE_IRON),
//                "P",
//                "P",
//                "P",
//                'P', "plateIron");

        Ingredient plateZinc = new OreIngredient("plateZinc");

        RollingMachineCraftingManager.getInstance().newShapedRecipeBuilder()
                .output(getStack(ELECTRODE_ZINC))
                .ingredients(
                        plateZinc,
                        plateZinc,
                        plateZinc
                )
                .height(3)
                .width(1)
                .buildAndRegister();

//        RailcraftCraftingManager.rollingMachine.addRecipe(
//                getStack(ELECTRODE_ZINC),
//                "P",
//                "P",
//                "P",
//                'P', "plateZinc");
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
        ELECTRODE_ZINC("plateZinc"),;
        public static EnumCharge[] VALUES = values();
        private Object alternate;

        EnumCharge(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate(IRailcraftRecipeIngredient container) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
