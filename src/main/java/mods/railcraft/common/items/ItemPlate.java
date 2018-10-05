/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.util.collections.CollectionTools;
import net.minecraft.item.crafting.Ingredient;

import static mods.railcraft.common.items.Metal.*;

public class ItemPlate extends ItemMetal {

    public ItemPlate() {
        super(Form.PLATE, true, false, CollectionTools.createIndexedLookupTable(IRON, STEEL, TIN, COPPER, LEAD, SILVER, BRONZE, GOLD, NICKEL, INVAR, ZINC, BRASS));
    }

    @Override
    public void defineRecipes() {
        RailcraftItems plate = RailcraftItems.PLATE;

        for (Metal m : getMetalBiMap().values()) {
            RailcraftCraftingManager.getRollingMachineCraftings().addRecipe(plate.getStack(4, m),
                    "II",
                    "II",
                    'I', m.getOreTag(Form.INGOT));
        }

        RailcraftCraftingManager.getBlastFurnaceCraftings().createRecipe(Ingredient.fromStacks(plate.getStack(Metal.IRON)), 1280, Metal.STEEL.getStack(Form.INGOT), RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SLAG));
    }
}
