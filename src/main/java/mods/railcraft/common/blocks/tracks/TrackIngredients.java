/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks;

import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.core.IRailcraftRecipeIngredient;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRail;
import mods.railcraft.common.items.ItemRailbed;
import mods.railcraft.common.items.ItemTie;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 9/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum TrackIngredients implements IRailcraftRecipeIngredient {
    RAIL_WOOD(() -> RailcraftConfig.useOldRecipes() ? "slabWood" : RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.WOOD)),
    RAIL_STANDARD(() -> RailcraftConfig.useOldRecipes() ? new ItemStack(Items.IRON_INGOT) : RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.STANDARD)),
    RAIL_ADVANCED(() -> RailcraftConfig.useOldRecipes() ? new ItemStack(Items.GOLD_INGOT) : RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.ADVANCED)),
    RAIL_SPEED(() -> RailcraftConfig.useOldRecipes() ? "ingotSteel" : RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.SPEED)),
    RAIL_REINFORCED(() -> RailcraftConfig.useOldRecipes() || !EnumMachineAlpha.ROCK_CRUSHER.isEnabled() ? "ingotSteel" : RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.REINFORCED)),
    RAIL_ELECTRIC(() -> RailcraftConfig.useOldRecipes() ? "ingotCopper" : RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.ELECTRIC)),
    TIE_WOOD(() -> RailcraftItems.TIE.getRecipeObject(ItemTie.EnumTie.WOOD)),
    RAILBED_WOOD(() -> RailcraftConfig.useOldRecipes() ? "stickWood" : RailcraftItems.RAILBED.getRecipeObject(ItemRailbed.EnumRailbed.WOOD)),
    RAILBED_STONE(() -> RailcraftConfig.useOldRecipes() ? Blocks.STONE_SLAB : RailcraftItems.RAILBED.getRecipeObject(ItemRailbed.EnumRailbed.STONE)),
    RAILBED_REINFORCED(() -> RailcraftConfig.useOldRecipes() || !RailcraftItems.RAIL.isEnabled() || !EnumMachineAlpha.ROCK_CRUSHER.isEnabled() ? new ItemStack(Blocks.OBSIDIAN) : RAILBED_STONE);
    private final Supplier<Object> ingredientSupplier;

    TrackIngredients(Supplier<Object> ingredientSupplier) {
        this.ingredientSupplier = ingredientSupplier;
    }

    @Nullable
    @Override
    public Object getRecipeObject() {
        return ingredientSupplier.get();
    }
}
