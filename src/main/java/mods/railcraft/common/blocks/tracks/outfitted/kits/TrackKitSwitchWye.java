/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.tracks.ISwitchDevice.ArrowDirection;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.CartTools;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

import static net.minecraft.block.BlockRailBase.EnumRailDirection.*;

public class TrackKitSwitchWye extends TrackKitSwitch {

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.WYE;
    }

    @Override
    public int getRenderState() {
        int state = 0;
        if (isVisuallySwitched())
            state += 1;
        if (isMirrored() != (getTrackShape() == EAST_WEST))
            state += 2;
        return state;
    }

    @Override
    public BlockRailBase.EnumRailDirection getRailDirection(IBlockState state, EntityMinecart cart) {
        BlockRailBase.EnumRailDirection dir = super.getRailDirection(state, cart);
        if (cart != null) {
            if (dir == NORTH_SOUTH) {
                if (isMirrored()) {
                    if (shouldSwitchForCart(cart)) {
                        dir = SOUTH_WEST;
                    } else {
                        dir = NORTH_WEST;
                    }
                } else {
                    if (shouldSwitchForCart(cart)) {
                        dir = SOUTH_EAST;
                    } else {
                        dir = NORTH_EAST;
                    }
                }
            } else if (dir == EAST_WEST) {
                if (isMirrored()) {
                    if (shouldSwitchForCart(cart)) {
                        dir = NORTH_WEST;
                    } else {
                        dir = NORTH_EAST;
                    }
                } else {
                    if (shouldSwitchForCart(cart)) {
                        dir = SOUTH_WEST;
                    } else {
                        dir = SOUTH_EAST;
                    }
                }
            }
        }
        return dir;
    }

    @Override
    protected List<UUID> getCartsAtLockEntrance() {
        EnumRailDirection dir = getRailDirection();
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.EAST_WEST) {
            offset = offset.east();
        } else if (dir == EnumRailDirection.NORTH_SOUTH) {
            offset = offset.north();
        }
        return CartTools.getMinecartUUIDsAt(theWorldAsserted(), offset, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtDecisionEntrance() {
        EnumRailDirection dir = getRailDirection();
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
        return CartTools.getMinecartUUIDsAt(theWorldAsserted(), offset, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtSpringEntrance() {
        EnumRailDirection dir = getRailDirection();
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.EAST_WEST) {
            offset = offset.west();
        } else if (dir == EnumRailDirection.NORTH_SOUTH) {
            offset = offset.south();
        }
        return CartTools.getMinecartUUIDsAt(theWorldAsserted(), offset, 0.1f);
    }

    //TODO: these are wrong
    @Override
    public ArrowDirection getRedSignDirection() {
        EnumRailDirection dir = getRailDirection();
        if (dir == EnumRailDirection.EAST_WEST) {
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

    //TODO: these are wrong
    @Override
    public ArrowDirection getWhiteSignDirection() {
        EnumRailDirection dir = getRailDirection();
        if (dir == EnumRailDirection.EAST_WEST) {
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
        EnumFacing face = EnumFacing.NORTH;
        EnumRailDirection dir = getRailDirection();
        if (dir == EAST_WEST) {
            if (isMirrored()) {
                face = EnumFacing.SOUTH;
            } else {
                face = EnumFacing.NORTH;
            }
        } else if (dir == NORTH_SOUTH) {
            if (isMirrored()) {
                face = EnumFacing.EAST;
            } else {
                face = EnumFacing.WEST;
            }
        }
        return face;
    }
}
