/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

import java.util.Calendar;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCartGift extends ItemCart {
    public ItemCartGift(IRailcraftCartContainer cart) {
        super(cart);
    }

    @Override
    public void defineRecipes() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        if (month == Calendar.DECEMBER || month == Calendar.JANUARY) {
            Game.log(Level.INFO, "Activating Christmas Seasonal Pack");
            CraftingPlugin.addRecipe(getStack(), "GGG",
                    "WEW",
                    "WWW",
                    'G', new ItemStack(Items.GUNPOWDER),
                    'E', "gemEmerald",
                    'W', "slabWood");
        }
    }
}
