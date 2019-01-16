/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRail;
import mods.railcraft.common.items.ItemRailbed;
import mods.railcraft.common.items.ItemTie;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 9/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum TrackIngredients implements IIngredientSource {
    RAIL_STRAP_IRON(() -> Ingredients.from("slabWood"), RailcraftItems.RAIL, ItemRail.EnumRail.WOOD),
    RAIL_STANDARD(() -> Ingredients.from("ingotIron"), RailcraftItems.RAIL, ItemRail.EnumRail.STANDARD),
    RAIL_ADVANCED(() -> Ingredients.from("ingotGold"), RailcraftItems.RAIL, ItemRail.EnumRail.ADVANCED),
    RAIL_SPEED(() -> Ingredients.from("ingotSteel"), RailcraftItems.RAIL, ItemRail.EnumRail.SPEED),
    RAIL_REINFORCED(() -> Ingredients.from(Blocks.OBSIDIAN), RailcraftItems.RAIL, ItemRail.EnumRail.REINFORCED),
    RAIL_ELECTRIC(() -> Ingredients.from("ingotCopper"), RailcraftItems.RAIL, ItemRail.EnumRail.ELECTRIC),
    TIE_WOOD(RailcraftItems.TIE, ItemTie.EnumTie.WOOD),
    TIE_STONE(RailcraftItems.TIE, ItemTie.EnumTie.STONE),
    RAILBED_WOOD(() -> Ingredients.from("stickWood"), RailcraftItems.RAILBED, ItemRailbed.EnumRailbed.WOOD),
    RAILBED_STONE(() -> Ingredients.from(Blocks.STONE_SLAB), RailcraftItems.RAILBED, ItemRailbed.EnumRailbed.STONE);
    private final Supplier<Ingredient> ingredientSupplierVanilla;
    private final RailcraftItems itemContainer;
    private final IVariantEnum variant;

    TrackIngredients(RailcraftItems itemContainer, IVariantEnum variant) {
        this(() -> itemContainer.getIngredient(variant), itemContainer, variant);
    }

    TrackIngredients(Supplier<Ingredient> ingredientSupplierVanilla, RailcraftItems itemContainer, IVariantEnum variant) {
        this.itemContainer = itemContainer;
        this.variant = variant;
        this.ingredientSupplierVanilla = ingredientSupplierVanilla;
    }

    @Override
    public Ingredient getIngredient() {
        return RailcraftConfig.vanillaTrackRecipes() ? ingredientSupplierVanilla.get() : itemContainer.getIngredient(variant);
    }

    @Override
    public ItemStack getStack(int qty) {
        return itemContainer.getStack(qty, variant);
    }
}
