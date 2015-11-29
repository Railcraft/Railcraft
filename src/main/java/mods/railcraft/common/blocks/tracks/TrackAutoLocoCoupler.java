/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.carts.EntityLocomotive;

public class TrackAutoLocoCoupler extends TrackBaseRailcraft implements ITrackPowered {

    private boolean powered = false;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.AUTOLOCOCOUPLER;
    }

    @Override
    public IIcon getIcon() {
        int iconIndex = 0;
        if (!isPowered())
            iconIndex++;
        return getIcon(iconIndex);
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered()) {
            if(cart instanceof EntityLocomotive) {
            	((EntityLocomotive) cart).readyToLink = true;
            }
        }
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public int getPowerPropagation() {
        return 8;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        boolean p = data.readBoolean();

        boolean needsUpdate = false;
        if (p != powered) {
            powered = p;
            needsUpdate = true;
        }
        if (needsUpdate)
            markBlockNeedsUpdate();
    }

}
