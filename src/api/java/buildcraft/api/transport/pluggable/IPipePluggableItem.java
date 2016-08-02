package buildcraft.api.transport.pluggable;

import buildcraft.api.transport.IPipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IPipePluggableItem {
    PipePluggable createPipePluggable(IPipe pipe, EnumFacing side, ItemStack stack);
}
