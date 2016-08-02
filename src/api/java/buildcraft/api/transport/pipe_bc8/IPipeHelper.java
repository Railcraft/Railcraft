package buildcraft.api.transport.pipe_bc8;

import buildcraft.api.transport.pipe_bc8.IPipeContentsEditable.IPipeContentsEditableFluid;
import buildcraft.api.transport.pipe_bc8.IPipeContentsEditable.IPipeContentsEditableItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IPipeHelper {
    IPipeContentsEditableItem getContentsForItem(ItemStack stack);

    IPipeContentsEditableFluid getContentsForFluid(FluidStack fluid);
}
