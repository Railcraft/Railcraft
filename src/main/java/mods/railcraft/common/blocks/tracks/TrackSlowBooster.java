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
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.tracks.ITrackPowered;

public class TrackSlowBooster extends TrackSlow implements ITrackPowered {

    private static final int POWER_PROPAGATION = 8;
    private static final double BOOST_FACTOR = 0.04;
    private static final double SLOW_FACTOR = 0.5;
    private static final double START_BOOST = 0.02;
    private static final double STALL_THRESHOLD = 0.03;
    private static final double BOOST_THRESHOLD = 0.01;
    private boolean powered = false;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SLOW_BOOSTER;
    }

    @Override
    public boolean isFlexibleRail() {
        return false;
    }

    @Override
    public IIcon getIcon() {
        if (!isPowered()) {
            return TrackTextureLoader.INSTANCE.getTrackIcons(getTrackSpec())[1];
        }
        return TrackTextureLoader.INSTANCE.getTrackIcons(getTrackSpec())[0];
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        int meta = tileEntity.getBlockMetadata();

        int i = tileEntity.xCoord;
        int j = tileEntity.yCoord;
        int k = tileEntity.zCoord;

        int dirMeta = meta & 7;
        double speed = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
        if (powered) {
            if (speed > BOOST_THRESHOLD) {
                cart.motionX += (cart.motionX / speed) * BOOST_FACTOR;
                cart.motionZ += (cart.motionZ / speed) * BOOST_FACTOR;
            } else if (dirMeta == 1) {
                if (getWorld().isSideSolid(i - 1, j, k, ForgeDirection.EAST)) {
                    cart.motionX = START_BOOST;
                } else if (getWorld().isSideSolid(i + 1, j, k, ForgeDirection.WEST)) {
                    cart.motionX = -START_BOOST;
                }
            } else if (dirMeta == 0) {
                if (getWorld().isSideSolid(i, j, k - 1, ForgeDirection.SOUTH)) {
                    cart.motionZ = START_BOOST;
                } else if (getWorld().isSideSolid(i, j, k + 1, ForgeDirection.NORTH)) {
                    cart.motionZ = -START_BOOST;
                }
            }
        } else {
            if (speed < STALL_THRESHOLD) {
                cart.motionX = 0.0D;
                cart.motionY = 0.0D;
                cart.motionZ = 0.0D;
            } else {
                cart.motionX *= SLOW_FACTOR;
                cart.motionY = 0.0D;
                cart.motionZ *= SLOW_FACTOR;
            }
        }
    }

    @Override
    public int getPowerPropagation() {
        return POWER_PROPAGATION;
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

        powered = data.readBoolean();

        markBlockNeedsUpdate();
    }
}
