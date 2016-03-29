/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.ISwitchDevice.ArrowDirection;
import mods.railcraft.common.carts.CartUtils;

import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.List;
import java.util.UUID;

public class TrackWye extends TrackSwitchBase {
    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.WYE;
    }

    // @Override
    // public IIcon getIcon() {
    // if (isVisuallySwitched()) {
    // return getIcon(1);
    // }
    // return getIcon(0);
    // }

    @Override
    public EnumRailDirection getRailDirection(IBlockState state, EnumRailDirection current, EntityMinecart cart) {
        if (cart != null) {
            if (current == EnumRailDirection.NORTH_SOUTH) {
                if (isMirrored()) {
                    if (shouldSwitchForCart(cart)) {
                        return EnumRailDirection.NORTH_WEST;
                    } else {
                        return EnumRailDirection.SOUTH_WEST;
                    }
                } else {
                    if (shouldSwitchForCart(cart)) {
                        return EnumRailDirection.SOUTH_EAST;
                    } else {
                        return EnumRailDirection.NORTH_EAST;
                    }
                }
            } else if (current == EnumRailDirection.EAST_WEST) {
                if (isMirrored()) {
                    if (shouldSwitchForCart(cart)) {
                        return EnumRailDirection.NORTH_EAST;
                    } else {
                        return EnumRailDirection.NORTH_WEST;
                    }
                } else {
                    if (shouldSwitchForCart(cart)) {
                        return EnumRailDirection.SOUTH_WEST;
                    } else {
                        return EnumRailDirection.SOUTH_EAST;
                    }
                }
            }
        }
        return current;
    }

    @Override
    protected List<UUID> getCartsAtLockEntrance() {
        EnumRailDirection dir = TrackTools.getTrackDirection(getWorld(), null, getPos());
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.EAST_WEST) {
            if (isMirrored()) {
                offset = offset.west();
            } else {
                offset = offset.east();
            }
        } else if (dir == EnumRailDirection.NORTH_SOUTH) {
            if (isMirrored()) {
                offset = offset.south();
            } else {
                offset = offset.north();
            }
        }
        return CartUtils.getMinecartUUIDsAt(getWorld(), offset, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtDecisionEntrance() {
        EnumRailDirection dir = TrackTools.getTrackDirection(getWorld(), null, getPos());
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.EAST_WEST) {
            if (isMirrored()) {
                offset = offset.north();
            } else {
                offset = offset.south();
            }
        } else if (dir == EnumRailDirection.NORTH_SOUTH) {
            if (isMirrored()) {
                offset = offset.west();
            } else {
                offset = offset.east();
            }
        }
        return CartUtils.getMinecartUUIDsAt(getWorld(), offset, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtSpringEntrance() {
        EnumRailDirection dir = TrackTools.getTrackDirection(getWorld(), null, getPos());
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.EAST_WEST) {
            if (isMirrored()) {
                offset = offset.east();
            } else {
                offset = offset.west();
            }
        } else if (dir == EnumRailDirection.NORTH_SOUTH) {
            if (isMirrored()) {
                offset = offset.north();
            } else {
                offset = offset.south();
            }
        }
        return CartUtils.getMinecartUUIDsAt(getWorld(), offset, 0.1f);
    }

    @Override
    public ArrowDirection getRedSignDirection() {
        if (EnumTrackMeta.EAST_WEST.isEqual(tileEntity.getBlockMetadata())) {
            if (isVisuallySwitched()) {
                if (isMirrored()) {
                    return ArrowDirection.WEST;
                }
                return ArrowDirection.EAST;
            }
            if (isMirrored()) {
                return ArrowDirection.EAST;
            }
            return ArrowDirection.WEST;
        }
        if (isVisuallySwitched()) {
            if (isMirrored()) {
                return ArrowDirection.NORTH;
            }
            return ArrowDirection.SOUTH;
        }
        if (isMirrored()) {
            return ArrowDirection.SOUTH;
        }
        return ArrowDirection.NORTH;
    }

    @Override
    public ArrowDirection getWhiteSignDirection() {
        if (EnumTrackMeta.EAST_WEST.isEqual(tileEntity.getBlockMetadata())) {
            if (isMirrored()) {
                return ArrowDirection.NORTH;
            }
            return ArrowDirection.SOUTH;
        }
        if (isMirrored()) {
            return ArrowDirection.EAST;
        }
        return ArrowDirection.WEST;
    }

    @Override
    public EnumFacing getActuatorLocation() {
        EnumFacing dir = EnumFacing.NORTH;
        int meta = tileEntity.getBlockMetadata();
        if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
            if (isMirrored()) {
                dir = EnumFacing.SOUTH;
            } else {
                dir = EnumFacing.NORTH;
            }
        } else if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            if (isMirrored()) {
                dir = EnumFacing.EAST;
            } else {
                dir = EnumFacing.WEST;
            }
        }
        return dir;
    }
}
