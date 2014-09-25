/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.electricity.IElectricGrid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public class TrackElectric extends TrackBaseRailcraft implements IElectricGrid {

    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.TRACK);

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.ELECTRIC;
    }

    @Override
    public IIcon getIcon() {
        int meta = tileEntity.getBlockMetadata();
        if (meta >= 6)
            return getIcon(1);
        return getIcon(0);
    }

    @Override
    public boolean isFlexibleRail() {
        return true;
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        chargeHandler.tick();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
    }

}
