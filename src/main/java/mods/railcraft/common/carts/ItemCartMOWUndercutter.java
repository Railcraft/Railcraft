/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCartMOWUndercutter extends ItemCart {
    public ItemCartMOWUndercutter(IRailcraftCartContainer cart) {
        super(cart);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(),
                "YLY",
                "RSR",
                "DMD",
                'L', new ItemStack(Blocks.REDSTONE_LAMP),
                'Y', "dyeYellow",
                'R', new ItemStack(Blocks.PISTON),
                'D', new ItemStack(Items.DIAMOND_SHOVEL),
                'S', "blockSteel",
                'M', new ItemStack(Items.MINECART));
    }
}
