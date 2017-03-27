/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import net.minecraft.init.Blocks;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCartTNTWood extends ItemCart {
    public ItemCartTNTWood(IRailcraftCartContainer cart) {
        super(cart);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(getStack(),
                "WTW",
                "WWW",
                'T', Blocks.TNT,
                'W', "slabWood");
    }

    @Override
    public void initializeDefinintion() {
        LootPlugin.addLoot(getCartType(), 1, 3, LootPlugin.Type.RAILWAY);
    }
}
