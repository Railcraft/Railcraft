package mods.railcraft.common.fluids;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nullable;

/**
 * A simple capability dispatcher for Railcraft fluid containers. Drain only,
 * no fills.
 */
public class FluidContainerCapabilityDispatcher extends FluidBucketWrapper {

    private final ItemFluidContainer item;

    public FluidContainerCapabilityDispatcher(ItemFluidContainer item, ItemStack container) {
        super(container);
        this.item = item;
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        return item.fluid.get(Fluid.BUCKET_VOLUME);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return super.drain(resource, doDrain);
    }

    @Override
    protected void setFluid(@Nullable Fluid fluid) {
        if (fluid == null)
            container.deserializeNBT(new ItemStack(item.empty).serializeNBT());
    }
}
