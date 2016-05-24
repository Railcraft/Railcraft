/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.CartUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackDisposal extends TrackSuspended implements ITrackPowered {

    private static final int TIME_TILL_NEXT_MOUNT = 40;
    private boolean powered;

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
    public boolean canPlaceRailAt(World world, BlockPos pos) {
        return world.isAirBlock(pos.down()) && super.canPlaceRailAt(world, pos);
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block neighborBlock) {
        super.onNeighborBlockChange(state, neighborBlock);
        testPower(state);
    }

    @Override
    public IBlockState getActualState(IBlockState state) {
        state = super.getActualState(state);
        state = state.withProperty(POWERED, isPowered());
        return state;
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
