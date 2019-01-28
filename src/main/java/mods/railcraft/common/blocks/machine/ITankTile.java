/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.interfaces.ITileTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.inventory.IInventory;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITankTile extends ITileTank {
    @Nullable
    StandardTank getTank();

    IInventory getInventory();

    String getTitle();
}
