/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

import java.util.Calendar;

@RailcraftModule("railcraft:seasonal")
public class ModuleSeasonal extends RailcraftModulePayload {

    public ModuleSeasonal() {
        setEnabledEventHandler(new ModuleEventHandler() {

            @Override
            public void preInit() {
                // Define Pumpkin Cart
                EnumCart cart = EnumCart.PUMPKIN;
                if (cart.setup()) {
                    Calendar cal = Calendar.getInstance();
                    int month = cal.get(Calendar.MONTH);
                    if (month == Calendar.OCTOBER || month == Calendar.NOVEMBER) {
                        Game.log(Level.INFO, "Activating Halloween Seasonal Pack");
                        CraftingPlugin.addRecipe(cart.getCartItem(), "GGG",
                                "WPW",
                                "WWW",
                                'G', new ItemStack(Items.GUNPOWDER),
                                'P', new ItemStack(Blocks.PUMPKIN),
                                'W', "slabWood");
                    }
                }

                // Define Gift Cart
                cart = EnumCart.GIFT;
                if (cart.setup()) {
                    Calendar cal = Calendar.getInstance();
                    int month = cal.get(Calendar.MONTH);
                    if (month == Calendar.DECEMBER || month == Calendar.JANUARY) {
                        Game.log(Level.INFO, "Activating Christmas Seasonal Pack");
                        CraftingPlugin.addRecipe(cart.getCartItem(), "GGG",
                                "WEW",
                                "WWW",
                                'G', new ItemStack(Items.GUNPOWDER),
                                'E', "gemEmerald",
                                'W', "slabWood");
                    }
                }
            }
        });
    }
}
