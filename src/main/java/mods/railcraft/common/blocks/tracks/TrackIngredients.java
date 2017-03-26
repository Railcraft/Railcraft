/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRail;
import mods.railcraft.common.items.ItemRailbed;
import mods.railcraft.common.items.ItemTie;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.init.Blocks;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 9/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum TrackIngredients implements IRailcraftRecipeIngredient {
    RAIL_STRAP_IRON(() -> "slabWood", () -> RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.WOOD)),
    RAIL_STANDARD(() -> "ingotIron", () -> RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.STANDARD)),
    RAIL_ADVANCED(() -> "ingotGold", () -> RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.ADVANCED)),
    RAIL_SPEED(() -> "ingotSteel", () -> RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.SPEED)),
    RAIL_REINFORCED(() -> Blocks.OBSIDIAN, () -> RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.REINFORCED)),
    RAIL_ELECTRIC(() -> "ingotCopper", () -> RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.ELECTRIC)),
    TIE_WOOD(() -> RailcraftItems.TIE.getRecipeObject(ItemTie.EnumTie.WOOD)),
    TIE_STONE(() -> RailcraftItems.TIE.getRecipeObject(ItemTie.EnumTie.STONE)),
    RAILBED_WOOD(() -> "stickWood", () -> RailcraftItems.RAILBED.getRecipeObject(ItemRailbed.EnumRailbed.WOOD)),
    RAILBED_STONE(() -> Blocks.STONE_SLAB, () -> RailcraftItems.RAILBED.getRecipeObject(ItemRailbed.EnumRailbed.STONE));
    private final Supplier<Object> ingredientSupplier;
    private final Supplier<Object> ingredientSupplierVanilla;

    TrackIngredients(Supplier<Object> ingredientSupplier) {
        this.ingredientSupplier = ingredientSupplier;
        this.ingredientSupplierVanilla = ingredientSupplier;
    }

    TrackIngredients(Supplier<Object> ingredientSupplierVanilla, Supplier<Object> ingredientSupplier) {
        this.ingredientSupplier = ingredientSupplier;
        this.ingredientSupplierVanilla = ingredientSupplierVanilla;
    }

    @Nullable
    @Override
    public Object getRecipeObject() {
        return RailcraftConfig.vanillaTrackRecipes() ? ingredientSupplierVanilla.get() : ingredientSupplier.get();
    }
}
