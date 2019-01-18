/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.fluids;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class FluidContainerHandler {

    public static final FluidContainerHandler INSTANCE = new FluidContainerHandler();

    private boolean initialized;
    private List<ItemStack> candidates = new ArrayList<>();

    public void init() {
        initialized = true;
        candidates.clear();
        for (Item item : ForgeRegistries.ITEMS) {
            NonNullList<ItemStack> list = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, list);
            for (ItemStack stack : list) {
                if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                    candidates.add(stack);
                }
            }
        }
    }

    public List<ItemStack> findCanDrain(FluidStack fluidStack) {
        if (!initialized) {
            init();
        }

        List<ItemStack> ret = new ArrayList<>();
        for (ItemStack itemStack : candidates) {
            IFluidHandlerItem handler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (handler != null && fluidStack.isFluidStackIdentical(handler.drain(fluidStack, false))) {
                ret.add(itemStack);
            }
        }

        return ret;
    }
}
