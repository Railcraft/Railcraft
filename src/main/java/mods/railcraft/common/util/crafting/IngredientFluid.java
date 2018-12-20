/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import com.google.gson.JsonObject;
import mods.railcraft.common.fluids.FluidContainerHandler;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

/**
 * An ingredient for fluids.
 */
public final class IngredientFluid extends IngredientRailcraft {

    private final FluidStack fluidStack;

    public IngredientFluid(FluidStack fluidStack) {
        super(FluidContainerHandler.INSTANCE.findCanDrain(fluidStack).toArray(new ItemStack[0]));
        this.fluidStack = fluidStack.copy();
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if (InvTools.isEmpty(stack)) {
            return false;
        }
        ItemStack checking = InvTools.copyOne(stack);
        IFluidHandlerItem handler = checking.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler == null) {
            return false;
        }
        return fluidStack.isFluidStackIdentical(handler.drain(fluidStack, false));
    }

    @Override
    public ItemStack getRemaining(ItemStack original) {
        ItemStack ret = InvTools.copyOne(original);
        IFluidHandlerItem handler = ret.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler == null) {
            return super.getRemaining(original);
        }
        handler.drain(fluidStack, true);
        return InvTools.makeSafe(handler.getContainer());
    }

    public static final class Factory implements IIngredientFactory {

        /**
         * Invoked by forge via reflection.
         */
        public Factory() {
            Game.log(Level.INFO, "Fluid ingredient factory loaded");
        }

        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            return new IngredientFluid(CraftingPlugin.getFluidStackFromRecipeFile(json));
        }
    }
}
