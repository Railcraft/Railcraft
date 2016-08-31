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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

import java.util.Calendar;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCartPumpkin extends ItemCart {
    public ItemCartPumpkin(IRailcraftCartContainer cart) {
        super(cart);
    }

    @Override
    public void defineRecipes() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        if (month == Calendar.OCTOBER || month == Calendar.NOVEMBER) {
            Game.log(Level.INFO, "Activating Halloween Seasonal Pack");
            CraftingPlugin.addRecipe(getStack(), "GGG",
                    "WPW",
                    "WWW",
                    'G', new ItemStack(Items.GUNPOWDER),
                    'P', new ItemStack(Blocks.PUMPKIN),
                    'W', "slabWood");
        }
    }
}
