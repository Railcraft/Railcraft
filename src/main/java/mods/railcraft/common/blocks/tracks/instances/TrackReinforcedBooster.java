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
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackReinforcedBooster extends TrackReinforced implements ITrackPowered {

    private static final int POWER_PROPAGATION = 8;
    private static final double BOOST_FACTOR = 0.065;
    private static final double SLOW_FACTOR = 0.5;
    private static final double START_BOOST = 0.02;
    private static final double STALL_THRESHOLD = 0.03;
    private static final double BOOST_THRESHOLD = 0.01;
    private boolean powered;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.REINFORCED_BOOSTER;
    }

    @Override
    public boolean isFlexibleRail() {
        return false;
    }

    @Override
    public IBlockState getActualState(IBlockState state) {
        state = super.getActualState(state);
        state = state.withProperty(POWERED, isPowered());
        return state;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        EnumRailDirection dir = getRailDirection();
        double speed = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
        World world = theWorldAsserted();
        if (powered) {
            if (speed > BOOST_THRESHOLD) {
                cart.motionX += (cart.motionX / speed) * BOOST_FACTOR;
                cart.motionZ += (cart.motionZ / speed) * BOOST_FACTOR;
            } else if (dir == EnumRailDirection.EAST_WEST) {
                if (world.isSideSolid(getPos().west(), EnumFacing.EAST)) {
                    cart.motionX = START_BOOST;
                } else if (world.isSideSolid(getPos().east(), EnumFacing.WEST)) {
                    cart.motionX = -START_BOOST;
                }
            } else if (dir == EnumRailDirection.NORTH_SOUTH) {
                if (world.isSideSolid(getPos().north(), EnumFacing.SOUTH)) {
                    cart.motionZ = START_BOOST;
                } else if (world.isSideSolid(getPos().south(), EnumFacing.NORTH)) {
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
