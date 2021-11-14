/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.api.signals;

import mods.railcraft.api.core.WorldCoordinate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SignalBlockSimple extends SignalBlock {

    private SignalAspect aspect = SignalAspect.BLINK_RED;

    public SignalBlockSimple(String locTag, TileEntity tile) {
        super(locTag, tile, 1);
    }

    @Override
    public void updateSignalAspect() {
        aspect = determineAspect(pairings.peek());
    }

    @Override
    public SignalAspect getSignalAspect() {
        return aspect;
    }

    @Override
    protected SignalAspect getSignalAspectForPair(WorldCoordinate otherCoord) {
        return SignalAspect.GREEN;
    }

    @Override
    protected void saveNBT(NBTTagCompound data) {
        super.saveNBT(data);
        aspect.writeToNBT(data, "aspect");
    }

    @Override
    protected void loadNBT(NBTTagCompound data) {
        super.loadNBT(data);
        aspect = SignalAspect.readFromNBT(data, "aspect");
    }
}
