/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import mods.railcraft.common.util.misc.Game;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChestMetals extends TileChestRailcraft {

    private static final int TICK_PER_CONDENSE = 16;

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineBeta.METALS_CHEST;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (clock % TICK_PER_CONDENSE == 0 && Game.isHost(worldObj))
            for (Metal metal : Metal.VALUES) {
                condense(metal);
            }
    }

    private void condense(Metal metal) {
        InventoryManipulator im = InventoryManipulator.get(this);
        if (metal.getIngot() != null && im.canRemoveItems(metal.nuggetFilter, 9) && im.canAddStack(metal.getIngot())) {
            im.removeItems(metal.nuggetFilter, 9);
            InvTools.moveItemStack(metal.getIngot(), this);
        }
        if (metal.getBlock() != null && im.canRemoveItems(metal.ingotFilter, 9) && im.canAddStack(metal.getBlock())) {
            im.removeItems(metal.ingotFilter, 9);
            InvTools.moveItemStack(metal.getBlock(), this);
        }
    }

}
