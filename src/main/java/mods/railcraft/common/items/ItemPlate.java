/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.util.collections.CollectionTools;

import static mods.railcraft.common.items.Metal.*;

public class ItemPlate extends ItemMetal {

    public ItemPlate() {
        super(Form.PLATE, true, false, CollectionTools.createIndexedLookupTable(IRON, STEEL, TIN, COPPER, LEAD, SILVER, BRONZE, GOLD, NICKEL, INVAR, ZINC, BRASS));
    }

    @Override
    public void defineRecipes() {
        RailcraftItems plate = RailcraftItems.PLATE;

        for (Metal m : getMetalBiMap().values()) {
            Crafters.rollingMachine().newRecipe(plate.getStack(4, m)).shaped(
                    "II",
                    "II",
                    'I', m.getOreTag(Form.INGOT));
        }

        Crafters.blastFurnace().newRecipe(RailcraftItems.PLATE.getIngredient(IRON))
                .name("railcraft:smelt_plate")
                .output(Metal.STEEL.getStack(Form.INGOT))
                .slagOutput(1)
                .register();
    }
}
