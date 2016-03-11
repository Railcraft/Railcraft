/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.carts.CartUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackDisposal extends TrackSuspended implements ITrackPowered {

    private static final int TIME_TILL_NEXT_MOUNT = 40;
    private boolean powered = false;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.DISPOSAL;
    }

    @Override
    public boolean canMakeSlopes() {
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (!isPowered() && cart.canBeRidden()) {
            if (cart.riddenByEntity != null) {
                CartUtils.dismount(cart, cart.posX, cart.posY - 2, cart.posZ);
            }
            cart.getEntityData().setInteger("MountPrevention", TIME_TILL_NEXT_MOUNT);
        }
    }

    @Override
    public boolean canPlaceRailAt(World world, int x, int y, int z) {
        if (!world.isAirBlock(x, y - 1, z))
            return false;
        return super.canPlaceRailAt(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        testPower();
    }

    @Override
    public IIcon getIcon() {
        if (isPowered()) {
            return getIcon(1);
        }
        return getIcon(0);
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
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("powered", powered);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        powered = nbttagcompound.getBoolean("powered");
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
        if (p != powered) {
            powered = p;
            markBlockNeedsUpdate();
        }
    }

}
