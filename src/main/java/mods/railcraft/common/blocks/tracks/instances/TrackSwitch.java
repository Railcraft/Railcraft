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

import mods.railcraft.api.tracks.ISwitchDevice.ArrowDirection;
import mods.railcraft.api.tracks.ITrackReversible;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.CartUtils;

import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class TrackSwitch extends TrackSwitchBase implements ITrackReversible {
    private boolean reversed;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SWITCH;
    }

    @Override
    public IIcon getIcon() {
        int index = 0;
        if (reversed) {
            index += 2;
        }
        if (isVisuallySwitched()) {
            index += 1;
        }
        return getIcon(index);
    }
    
    @Override
    public EnumRailDirection getRailDirection(IBlockState state, EntityMinecart cart) {
        EnumRailDirection current = super.getRailDirection(state, cart);
        if (cart != null && shouldSwitchForCart(cart)) {
            if (current == EnumRailDirection.NORTH_SOUTH) {
                if (isMirrored()) {
                    if (reversed) {
                       return EnumRailDirection.SOUTH_WEST;
                    } else {
                        return EnumRailDirection.NORTH_WEST;
                    }
                } else {
                    if (reversed) {
                        return EnumRailDirection.NORTH_EAST;
                    } else {
                        return EnumRailDirection.SOUTH_EAST;
                    }
                }
            } else if (current == EnumRailDirection.EAST_WEST) {
                if (isMirrored()) {
                    if (reversed) {
                        return EnumRailDirection.NORTH_WEST;
                    } else {
                        return EnumRailDirection.NORTH_EAST;
                    }
                } else {
                    if (reversed) {
                        return EnumRailDirection.SOUTH_EAST;
                    } else {
                        return EnumRailDirection.SOUTH_WEST;
                    }
                }
            }
        }
        return current;
    }

    @Override
    protected List<UUID> getCartsAtLockEntrance() {
        EnumRailDirection dir = getRailDirection();
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.NORTH_SOUTH) {
            if (isReversed() != isMirrored()) {
                offset = offset.south();
            } else {
                offset = offset.north();
            }
        } else if (dir == EnumRailDirection.EAST_WEST) {
            if (!isReversed() != isMirrored()) {
                offset = offset.east();
            } else {
                offset = offset.west();
            }
        }
        return CartUtils.getMinecartUUIDsAt(getWorld(), offset, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtDecisionEntrance() {
        EnumRailDirection dir = getRailDirection();
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.NORTH_SOUTH) {
            if (isReversed() != isMirrored()) {
                offset = offset.north();
            } else {
                offset = offset.south();
            }
        } else if (dir == EnumRailDirection.EAST_WEST) {
            if (!isReversed() != isMirrored()) {
                offset = offset.west();
            } else {
                offset = offset.east();
            }
        }
        return CartUtils.getMinecartUUIDsAt(getWorld(), offset, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtSpringEntrance() {
        EnumRailDirection dir = getRailDirection();
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.NORTH_SOUTH) {
            if (isMirrored()) {
                offset = offset.west();
            } else {
                offset = offset.east();
            }
        } else if (dir == EnumRailDirection.EAST_WEST) {
            if (isMirrored()) {
                offset = offset.north();
            } else {
                offset = offset.south();
            }
        }
        return CartUtils.getMinecartUUIDsAt(getWorld(), offset, 0.1f);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("Reversed", reversed);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        reversed = data.getBoolean("Reversed");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(reversed);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        reversed = data.readBoolean();
    }

    @Override
    public boolean isReversed() {
        return reversed;
    }

    @Override
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public ArrowDirection getRedSignDirection() {
        if (tileEntity.getBlockMetadata() == 1) {
            if (isVisuallySwitched()) {
                if (isMirrored()) {
                    return ArrowDirection.NORTH;
                }
                return ArrowDirection.SOUTH;
            }
            if (isReversed() != isMirrored()) {
                return ArrowDirection.WEST;
            }
            return ArrowDirection.EAST;
        }
        if (isVisuallySwitched()) {
            if (isMirrored()) {
                return ArrowDirection.EAST;
            }
            return ArrowDirection.WEST;
        }
        if (isReversed() != isMirrored()) {
            return ArrowDirection.NORTH;
        }
        return ArrowDirection.SOUTH;
    }

    @Override
    public ArrowDirection getWhiteSignDirection() {
        if (tileEntity.getBlockMetadata() == 1) {
            if (isVisuallySwitched()) {
                return ArrowDirection.EAST_WEST;
            }
            return ArrowDirection.NORTH_SOUTH;
        }
        if (isVisuallySwitched()) {
            return ArrowDirection.NORTH_SOUTH;
        }
        return ArrowDirection.EAST_WEST;
    }

    @Override
    public EnumFacing getActuatorLocation() {
        EnumFacing face = EnumFacing.NORTH;
        EnumRailDirection dir = getRailDirection();

        if (dir == EnumRailDirection.NORTH_SOUTH) {
            if (isMirrored()) {
                face = EnumFacing.EAST;
            } else {
                face = EnumFacing.WEST;
            }
        } else if (dir == EnumRailDirection.EAST_WEST) {
            if (isMirrored()) {
                face = EnumFacing.SOUTH;
            } else {
                face = EnumFacing.NORTH;
            }
        }
        return face;
    }
}