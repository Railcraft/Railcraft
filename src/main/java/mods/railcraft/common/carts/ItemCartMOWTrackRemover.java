/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.api.items.IToolCrowbar;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCartMOWTrackRemover extends ItemCart {
    public ItemCartMOWTrackRemover(IRailcraftCartContainer cart) {
        super(cart);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(),
                "YLY",
                "PSP",
                "CMC",
                'Y', "dyeYellow",
                'L', new ItemStack(Blocks.REDSTONE_LAMP),
                'P', new ItemStack(Blocks.STICKY_PISTON),
                'S', "blockSteel",
                'C', IToolCrowbar.ORE_TAG,
                'M', new ItemStack(Items.MINECART));
    }
}
