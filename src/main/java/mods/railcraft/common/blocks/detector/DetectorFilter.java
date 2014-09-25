/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector;

import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.common.util.inventory.PhantomInventory;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class DetectorFilter extends Detector {

    private final PhantomInventory invFilters;

    public DetectorFilter(int invSize) {
        invFilters = new PhantomInventory(invSize);
    }

    public PhantomInventory getFilters() {
        return invFilters;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        NBTTagCompound filters = new NBTTagCompound();
        getFilters().writeToNBT("Items", filters);
        data.setTag("filters", filters);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        NBTTagCompound filters = data.getCompoundTag("filters");
        getFilters().readFromNBT("Items", filters);
    }

}
