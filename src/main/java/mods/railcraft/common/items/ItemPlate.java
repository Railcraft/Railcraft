/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.util.collections.CollectionTools;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nullable;

import static mods.railcraft.common.items.Metal.*;

public class ItemPlate extends ItemMetal {

    public ItemPlate() {
        super(Form.PLATE, false, false, CollectionTools.createIndexedLookupTable(IRON, STEEL, TIN, COPPER, LEAD));
    }

    @Override
    public void initializeDefinintion() {
        for (Metal m : getMetalBiMap().values()) {
            LootPlugin.addLoot(RailcraftItems.PLATE, m, 6, 18, LootPlugin.Type.WORKSHOP);
        }
    }

    @Override
    public String getOreTag(@Nullable IVariantEnum variant) {
        return null;
    }

    @Override
    public void defineRecipes() {
        RailcraftItems plate = RailcraftItems.PLATE;

        // Iron Plate
        IRecipe recipe = new ShapedOreRecipe(plate.getStack(4, Metal.IRON),
                "II",
                "II",
                'I', "ingotIron");
        RailcraftCraftingManager.rollingMachine.getRecipeList().add(recipe);

        // Steel Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, Metal.STEEL),
                "II",
                "II",
                'I', "ingotSteel");
        RailcraftCraftingManager.rollingMachine.addRecipe(recipe);

        // Tin Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, Metal.TIN),
                "IT",
                "TI",
                'I', "ingotIron",
                'T', "ingotTin");
        RailcraftCraftingManager.rollingMachine.addRecipe(recipe);

        // Copper Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, Metal.COPPER),
                "II",
                "II",
                'I', "ingotCopper");
        RailcraftCraftingManager.rollingMachine.addRecipe(recipe);

        // Lead Plate
        recipe = new ShapedOreRecipe(plate.getStack(4, Metal.LEAD),
                "II",
                "II",
                'I', "ingotLead");
        RailcraftCraftingManager.rollingMachine.addRecipe(recipe);

        RailcraftCraftingManager.blastFurnace.addRecipe(plate.getStack(Metal.IRON), true, false, 1280, Metal.STEEL.getStack(Form.INGOT));
    }
}
