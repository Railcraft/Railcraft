/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import com.google.gson.*;
import mods.railcraft.common.fluids.FluidContainerHandler;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

/**
 * An ingredient for fluids.
 */
public final class FluidIngredient extends RailcraftIngredient {

    private final FluidStack fluidStack;

    public FluidIngredient(FluidStack fluidStack) {
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

        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        /**
         * Invoked by forge via reflection.
         */
        public Factory() {
            Game.log().msg(Level.INFO, "Fluid ingredient factory loaded");
        }

        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            return new FluidIngredient(getFluidStackFromRecipeFile(json));
        }

        private FluidStack getFluidStackFromRecipeFile(JsonObject json) {
            String name = JsonUtils.getString(json, "fluid");

            Fluid fluid = FluidRegistry.getFluid(name);

            if (fluid == null)
                throw new JsonSyntaxException("Unknown fluid '" + name + "'");

            int amount = JsonUtils.getInt(json, "amount");

            if (json.has("nbt")) {
                // Lets hope this works? Needs test
                try {
                    JsonElement element = json.get("nbt");
                    NBTTagCompound nbt;
                    if (element.isJsonObject())
                        nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
                    else
                        nbt = JsonToNBT.getTagFromJson(element.getAsString());

                    return new FluidStack(fluid, amount, nbt);
                } catch (NBTException e) {
                    throw new JsonSyntaxException("Invalid NBT Entry: " + e);
                }
            }

            return new FluidStack(fluid, amount);
        }
    }
}
