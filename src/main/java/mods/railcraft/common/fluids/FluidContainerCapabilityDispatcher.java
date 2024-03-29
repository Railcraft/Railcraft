/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.fluids;

import mods.railcraft.common.modules.ModuleResources;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * A simple capability dispatcher for Railcraft fluid containers. Drain only,
 * no fills.
 */
final class FluidContainerCapabilityDispatcher extends FluidBucketWrapper {

    private final ItemFluidContainer item;

    FluidContainerCapabilityDispatcher(ItemFluidContainer item, ItemStack container) {
        super(container);
        this.item = item;
    }

    @Override
    public FluidStack getFluid() {
        if (isEmpty())
            return null;
        return item.fluid.get(Fluid.BUCKET_VOLUME);
    }

    private boolean isEmpty() {
        return InvTools.isEmpty(container) || InvTools.isItem(container, item.empty);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (isEmpty())
            return null;
        return super.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (isEmpty())
            return null;
        return super.drain(maxDrain, doDrain);
    }

    @Override
    protected void setFluid(@Nullable FluidStack fluid) {
        if (fluid == null) {
            if (item.empty == Items.GLASS_BOTTLE && ModuleResources.getInstance().isBottleFree()) {
                container = ItemStack.EMPTY;
            } else {
                container = new ItemStack(item.empty);
            }
        }
    }
}
