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
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCartTank extends ItemCart implements IPrototypedItem {
    public ItemCartTank(IRailcraftCartContainer cart) {
        super(cart);
    }

    @Override
    public boolean isValidPrototype(ItemStack stack) {
        return FluidItemHelper.isFluidInContainer(stack);
    }

    @Override
    public void defineRecipes() {
        if (EnumMachineBeta.TANK_IRON_GAUGE.isAvailable()) {
            CraftingPlugin.addRecipe(getStack(),
                    "T",
                    "M",
                    'T', EnumMachineBeta.TANK_IRON_GAUGE.getStack(),
                    'M', Items.MINECART);
        } else {
            CraftingPlugin.addRecipe(getStack(),
                    "GGG",
                    "GMG",
                    "GGG",
                    'G', "blockGlassColorless",
                    'M', Items.MINECART);
        }
    }
}
