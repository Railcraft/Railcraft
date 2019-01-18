/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector;

import mods.railcraft.common.util.inventory.InventoryAdvanced;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class DetectorFilter extends Detector {

    private final InventoryAdvanced invFilters;

    protected DetectorFilter(int invSize) {
        invFilters = new InventoryAdvanced(invSize).callbackTile(this::getTile).phantom();
    }

    public InventoryAdvanced getFilters() {
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
