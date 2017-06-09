/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.api.core.items.IPrototypedItem;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCartCargo extends ItemCart implements IPrototypedItem {
    public ItemCartCargo(IRailcraftCartContainer cart) {
        super(cart);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(getStack(),
                "B",
                "M",
                'B', Blocks.TRAPPED_CHEST,
                'M', Items.MINECART);
    }
}
