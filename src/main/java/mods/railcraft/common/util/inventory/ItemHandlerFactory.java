package mods.railcraft.common.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;

public final class ItemHandlerFactory {

    public static IItemHandlerModifiable wrap(IInventory inventory, @Nullable EnumFacing side) {
        if (inventory instanceof ISidedInventory && side != null) {
            return new SidedInvWrapper((ISidedInventory) inventory, side);
        }
        return new InvWrapper(inventory);
    }

    private ItemHandlerFactory() {
    }
}
