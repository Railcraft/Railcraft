/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import net.minecraft.init.Items;

public class ItemBottle extends ItemFluidContainer {

    public ItemBottle(Fluids fluid) {
        super(fluid, Items.GLASS_BOTTLE);
        setMaxStackSize(16);
    }

}
