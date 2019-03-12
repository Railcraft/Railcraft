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
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.Locale;

public class ItemTie extends ItemRailcraftSubtyped {

    public ItemTie() {
        super(EnumTie.class);
    }

    @Override
    public void initializeDefinition() {
        for (EnumTie tie : EnumTie.VALUES) {
            RailcraftRegistry.register(this, tie, new ItemStack(this, 1, tie.ordinal()));
        }
    }

    @Override
    public void defineRecipes() {
        ItemStack tieStone = RailcraftItems.TIE.getStack(1, EnumTie.STONE);
        FluidStack water = Fluids.WATER.getB(1);
        CraftingPlugin.addShapedRecipe(tieStone,
                " O ",
                "#r#",
                'O', water,
                'r', RailcraftItems.REBAR,
                '#', RailcraftItems.CONCRETE);

        ItemStack tieWood = RailcraftItems.TIE.getStack(1, EnumTie.WOOD);
        FluidStack creosote = Fluids.CREOSOTE.getB(1);
        CraftingPlugin.addShapedRecipe(tieWood,
                " O ",
                "###",
                'O', creosote,
                '#', "slabWood");
    }

    public enum EnumTie implements IVariantEnum {
        WOOD("slabWood"),
        STONE(Blocks.STONE_SLAB);
        public static final EnumTie[] VALUES = values();
        private Ingredient alternate;

        EnumTie(Object alt) {
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
