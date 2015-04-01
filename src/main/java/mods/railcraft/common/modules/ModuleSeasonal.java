/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import java.util.Calendar;
import org.apache.logging.log4j.Level;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class ModuleSeasonal extends RailcraftModule {

    @Override
    public void initFirst() {

        // Define Pumpkin Cart
        EnumCart cart = EnumCart.PUMPKIN;
        if (cart.setup()) {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH);
            if (month == Calendar.OCTOBER || month == Calendar.NOVEMBER) {
                Game.log(Level.INFO, "Activating Halloween Seasonal Pack");
                CraftingPlugin.addShapedRecipe(cart.getCartItem(), new Object[]{
                            "GGG",
                            "WPW",
                            "WWW",
                            'G', new ItemStack(Items.gunpowder),
                            'P', new ItemStack(Blocks.pumpkin),
                            'W', "slabWood"
                        });
            }
        }

        // Define Gift Cart
        cart = EnumCart.GIFT;
        if (cart.setup()) {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH);
            if (month == Calendar.DECEMBER || month == Calendar.JANUARY) {
                Game.log(Level.INFO, "Activating Christmas Seasonal Pack");
                CraftingPlugin.addShapedRecipe(cart.getCartItem(), new Object[]{
                            "GGG",
                            "WEW",
                            "WWW",
                            'G', new ItemStack(Items.gunpowder),
                            'E', "gemEmerald",
                            'W', "slabWood"
                        });
            }
        }
    }
}
