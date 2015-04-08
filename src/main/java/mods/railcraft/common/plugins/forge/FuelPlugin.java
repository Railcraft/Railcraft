/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import cpw.mods.fml.common.IFuelHandler;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.items.RailcraftToolItems;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FuelPlugin {

    private static IFuelHandler fuelHandler;
    private static ItemStack lastFuel;
    private static int lastFuelValue;

    public static IFuelHandler getFuelHandler() {
        if (fuelHandler == null)
            fuelHandler = new FuelHandler();
        return fuelHandler;
    }

    private static class FuelHandler implements IFuelHandler {

        private static final int COKE_HEAT = 3200;
        private final ItemStack coke = RailcraftToolItems.getCoalCoke();
        private final ItemStack cokeBlock = EnumCube.COKE_BLOCK.getItem();

        private FuelHandler() {
        }

        @Override
        public int getBurnTime(ItemStack fuel) {
            if (fuel == null)
                return 0;
            if (InvTools.isItemEqual(fuel, coke))
                return COKE_HEAT;
            if (InvTools.isItemEqual(fuel, cokeBlock))
                return COKE_HEAT * 10;
            if (fuel.getItem() instanceof ItemRailcraft)
                return ((ItemRailcraft) fuel.getItem()).getHeatValue(fuel);
            return 0;
        }

    }

    /**
     * Internal function that provides custom fuel values before requesting them
     * from Minecraft. It also caches the last fuel hit to reduce cpu cycles.
     *
     * @param stack The item to test
     * @return The fuel value
     */
    public static int getBurnTime(ItemStack stack) {
        if (stack == null)
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

            if (item instanceof ItemBlock) {
                Block block = InvTools.getBlockFromStack(stack);

                String name = block.getUnlocalizedName();
                if (name != null && name.contains("blockScaffold"))
                    return 0;
            }

//            if (itemID == Item.coal.itemID && stack.getItemDamage() == 0)
//                return 1600;

            if (item == Items.blaze_rod)
                return 1000;

            FluidStack liquid = FluidItemHelper.getFluidStackInContainer(stack);
            if (liquid != null && Fluids.LAVA.get() == liquid.getFluid())
                return liquid.amount;

            String name = stack.getItem().getUnlocalizedName();
            if (name != null && name.contains("itemScrap"))
                return 0;

            return TileEntityFurnace.getItemBurnTime(stack);
        } catch (Exception ex) {
            Game.logThrowable("Error in Fuel Handler! Is some mod creating items that are not compliant with standards?", ex);
        }
        return 0;
    }

}
