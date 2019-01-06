/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.util.Strings;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FuelPlugin {

    private static ItemStack lastFuel;
    private static int lastFuelValue;

    /**
     * Internal function that provides custom fuel values before requesting them
     * from Minecraft. It also caches the last fuel hit to reduce cpu cycles.
     *
     * @param stack The item to test
     * @return The fuel value
     */
    public static int getBurnTime(ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return 0;

        if (InvTools.isItemEqualSemiStrict(stack, lastFuel))
            return lastFuelValue;

        lastFuel = stack;
        lastFuelValue = findFuelValue(stack);
        return lastFuelValue;
    }

    private static int findFuelValue(ItemStack stack) {
        try {
            Item item = stack.getItem();

            String name = InvTools.getBlockStateFromStack(stack).getBlock().getTranslationKey();
            if (!Strings.isEmpty(name) && name.contains("blockScaffold"))
                return 0;

//            if (itemID == Item.coal.itemID && stack.getItemDamage() == 0)
//                return 1600;

            if (item == Items.BLAZE_ROD)
                return 1000;

            FluidStack liquid = FluidItemHelper.getFluidStackInContainer(stack);
            if (liquid != null && Fluids.LAVA.is(liquid.getFluid()))
                return liquid.amount;

            name = stack.getItem().getTranslationKey();
            if (!Strings.isEmpty(name) && name.contains("itemScrap"))
                return 0;

            return TileEntityFurnace.getItemBurnTime(stack);
        } catch (Exception ex) {
            Game.log().throwable("Error in Fuel Handler! Is some mod creating items that are not compliant with standards?", ex);
        }
        return 0;
    }

}
