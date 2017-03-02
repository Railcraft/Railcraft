/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemSpikeMaulSteel extends ItemSpikeMaul {

    public ItemSpikeMaulSteel() {
        super(ItemMaterials.Material.STEEL, ItemMaterials.STEEL_TOOL);
    }

    @Override
    public void defineRecipes() {
        // TODO: Add recipe
//        CraftingPlugin.addRecipe(new ItemStack(this),
//                " RI",
//                "RIR",
//                "IR ",
//                'I', "ingotIron",
//                'R', "dyeRed");
    }

}
