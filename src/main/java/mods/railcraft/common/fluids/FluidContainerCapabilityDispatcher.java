package mods.railcraft.common.fluids;

import mods.railcraft.common.modules.ModuleResources;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

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

    @Override
    protected void setFluid(@Nullable Fluid fluid) {
        if (fluid == null) {
            if (item.empty == Items.GLASS_BOTTLE && ModuleResources.getInstance().isBottleFree()) {
                setSize(container, 0);
            } else {
                container.deserializeNBT(new ItemStack(item.empty).serializeNBT());
            }
        }
    }
}
