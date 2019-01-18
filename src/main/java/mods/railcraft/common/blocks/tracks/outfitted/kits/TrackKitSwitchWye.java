/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.tracks.ISwitchActuator.ArrowDirection;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.CartTools;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static net.minecraft.block.BlockRailBase.EnumRailDirection.*;

public class TrackKitSwitchWye extends TrackKitSwitch {

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.WYE;
    }

    // This is wonky as shit, but it works
    @Override
    public int getRenderState() {
        int state = 0;
        if (isMirrored() != isVisuallySwitched())
            state += 1;
        if (isMirrored() != (getRailDirectionRaw() == EAST_WEST))
            state += 2;
        return state;
    }

    @Override
    public BlockRailBase.EnumRailDirection getRailDirection(IBlockState state, @Nullable EntityMinecart cart) {
        BlockRailBase.EnumRailDirection dir = super.getRailDirection(state, cart);
        if (cart != null) {
            if (dir == NORTH_SOUTH) {
                if (isMirrored()) {
                    if (shouldSwitchForCart(cart)) {
                        dir = NORTH_WEST;
                    } else {
                        dir = SOUTH_WEST;
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
                        dir = NORTH_EAST;
                    } else {
                        dir = NORTH_WEST;
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
        EnumRailDirection dir = getRailDirectionRaw();
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
        return CartTools.getMinecartUUIDsAt(theWorldAsserted(), offset, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtDecisionEntrance() {
        EnumRailDirection dir = getRailDirectionRaw();
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
        EnumRailDirection dir = getRailDirectionRaw();
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
        return CartTools.getMinecartUUIDsAt(theWorldAsserted(), offset, 0.1f);
    }

    @Override
    public ArrowDirection getRedSignDirection() {
        if (getRailDirectionRaw() == EnumRailDirection.EAST_WEST) {
            if (isVisuallySwitched()) {
                return isMirrored() ? ArrowDirection.EAST : ArrowDirection.WEST;
            }
            return isMirrored() ? ArrowDirection.WEST : ArrowDirection.EAST;
        }
        if (isVisuallySwitched()) {
            return isMirrored() ? ArrowDirection.NORTH : ArrowDirection.SOUTH;
        }
        return isMirrored() ? ArrowDirection.SOUTH : ArrowDirection.NORTH;
    }

    @Override
    public ArrowDirection getWhiteSignDirection() {
        if (getRailDirectionRaw() == EnumRailDirection.EAST_WEST) {
            if (isMirrored()) {
                return ArrowDirection.NORTH;
            }
            return ArrowDirection.SOUTH;
        }
        if (isMirrored()) {
            return ArrowDirection.WEST;
        }
        return ArrowDirection.EAST;
    }

    @Override
    public EnumFacing getActuatorLocation() {
        EnumFacing face;
        if (getRailDirectionRaw() == EAST_WEST) {
            if (isMirrored()) {
                face = EnumFacing.SOUTH;
            } else {
                face = EnumFacing.NORTH;
            }
        } else {
            if (isMirrored()) {
                face = EnumFacing.EAST;
            } else {
                face = EnumFacing.WEST;
            }
        }
        return face;
    }
}
