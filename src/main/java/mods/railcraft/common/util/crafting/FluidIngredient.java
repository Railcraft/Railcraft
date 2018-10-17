package mods.railcraft.common.util.crafting;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import mods.railcraft.common.fluids.FluidContainerHandler;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An ingredient for fluids.
 */
@SuppressWarnings("unused")
public final class FluidIngredient extends Ingredient implements IRemainderIngredient {

    private final FluidStack fluidStack;
    @Nullable
    private IntList compressed;
    private final ItemStack[] matching;

    public FluidIngredient(FluidStack fluidStack) {
        this.fluidStack = fluidStack.copy();
        this.matching = FluidContainerHandler.INSTANCE.findCanDrain(fluidStack).toArray(new ItemStack[0]);
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return matching;
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
    public IntList getValidItemStacksPacked() {
        if (this.compressed == null) {
            this.compressed = new IntArrayList(this.matching.length);

            for (ItemStack itemstack : this.matching) {
                this.compressed.add(RecipeItemHelper.pack(itemstack));
            }

            this.compressed.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.compressed;
    }

    @Override
    protected void invalidate() {
        this.compressed = null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public ItemStack getRemaining(ItemStack original) {
        ItemStack ret = InvTools.copyOne(original);
        IFluidHandlerItem handler = ret.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler == null) {
            return ret;
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

        @NotNull
        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            return new FluidIngredient(CraftingPlugin.getFluidStackFromRecipeFile(json));
        }
    }
}
