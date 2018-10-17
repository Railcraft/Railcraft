package mods.railcraft.common.util.entity;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

/**
 *
 */
public abstract class ChestLogic extends AbstractLogic {

    protected IInventory inventory;

    ChestLogic(World world, IInventory inventory) {
        super(world);
        this.inventory = inventory;
    }
}
