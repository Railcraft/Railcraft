package mods.railcraft.common.util.entity;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * The logic behind the void chest.
 */
public class VoidChestLogic extends ChestLogic {

    public VoidChestLogic(World world, IInventory inventory) {
        super(world, inventory);
    }

    @Override
    public void update() {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!InvTools.isEmpty(stack)) {
                inventory.decrStackSize(slot, 1);
                break;
            }
        }
    }
}
