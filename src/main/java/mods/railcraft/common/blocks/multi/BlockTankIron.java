/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;

/**
 * Created by CovertJaguar on 6/11/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockTankIron extends BlockTankMetal {
    protected BlockTankIron(Material material) {
        super(material);
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();

        // Smelting Recipe to turn Iron Tanks into Steel Nuggets
        BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Item.getItemFromBlock(this)), 640, RailcraftItems.NUGGET.getStack(4, Metal.STEEL));
    }

}
