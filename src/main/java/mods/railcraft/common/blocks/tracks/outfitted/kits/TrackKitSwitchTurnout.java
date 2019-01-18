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
import mods.railcraft.api.tracks.ITrackKitReversible;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.CartTools;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class TrackKitSwitchTurnout extends TrackKitSwitch implements ITrackKitReversible {
    private boolean reversed;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.TURNOUT;
    }

    // This is wonky as shit, but it works
    @Override
    public int getRenderState() {
        int state = 0;
        if (isVisuallySwitched())
            state += 1;
        if (isReversed())
            state += 2;
        if (isMirrored())
            state += 4;
        return state;
    }

    @Override
    public EnumRailDirection getRailDirection(IBlockState state, @Nullable EntityMinecart cart) {
        EnumRailDirection current = super.getRailDirection(state, cart);
        if (cart != null && shouldSwitchForCart(cart)) {
            if (current == EnumRailDirection.NORTH_SOUTH) {
                if (isMirrored()) {
                    return reversed ? EnumRailDirection.SOUTH_WEST : EnumRailDirection.NORTH_WEST;
                } else {
                    return reversed ? EnumRailDirection.NORTH_EAST : EnumRailDirection.SOUTH_EAST;
                }
            } else if (current == EnumRailDirection.EAST_WEST) {
                if (isMirrored()) {
                    return reversed ? EnumRailDirection.NORTH_WEST : EnumRailDirection.NORTH_EAST;
                } else {
                    return reversed ? EnumRailDirection.SOUTH_EAST : EnumRailDirection.SOUTH_WEST;
                }
            }
        }
        return current;
    }

    @Override
    protected List<UUID> getCartsAtLockEntrance() {
        EnumRailDirection dir = getRailDirectionRaw();
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.NORTH_SOUTH) {
            offset = isReversed() != isMirrored() ? offset.south() : offset.north();
        } else if (dir == EnumRailDirection.EAST_WEST) {
            offset = isReversed() == isMirrored() ? offset.east() : offset.west();
        }
        return CartTools.getMinecartUUIDsAt(theWorldAsserted(), offset, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtDecisionEntrance() {
        EnumRailDirection dir = getRailDirectionRaw();
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.NORTH_SOUTH) {
            offset = isReversed() != isMirrored() ? offset.north() : offset.south();
        } else if (dir == EnumRailDirection.EAST_WEST) {
            offset = isReversed() == isMirrored() ? offset.west() : offset.east();
        }
        return CartTools.getMinecartUUIDsAt(theWorldAsserted(), offset, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtSpringEntrance() {
        EnumRailDirection dir = getRailDirectionRaw();
        BlockPos offset = getPos();
        if (dir == EnumRailDirection.NORTH_SOUTH) {
            offset = isMirrored() ? offset.west() : offset.east();
        } else if (dir == EnumRailDirection.EAST_WEST) {
            offset = isMirrored() ? offset.north() : offset.south();
        }
        return CartTools.getMinecartUUIDsAt(theWorldAsserted(), offset, 0.1f);
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
        if (getRailDirectionRaw() == EnumRailDirection.EAST_WEST) {
            if (isVisuallySwitched()) {
                return isMirrored() ? ArrowDirection.NORTH : ArrowDirection.SOUTH;
            }
            return isReversed() != isMirrored() ? ArrowDirection.EAST : ArrowDirection.WEST;
        }
        if (isVisuallySwitched()) {
            return isMirrored() ? ArrowDirection.WEST : ArrowDirection.EAST;
        }
        return isReversed() != isMirrored() ? ArrowDirection.NORTH : ArrowDirection.SOUTH;
    }

    @Override
    public ArrowDirection getWhiteSignDirection() {
        if (getRailDirectionRaw() == EnumRailDirection.EAST_WEST) {
            return isVisuallySwitched() ? ArrowDirection.EAST_WEST : ArrowDirection.NORTH_SOUTH;
        }
        return isVisuallySwitched() ? ArrowDirection.NORTH_SOUTH : ArrowDirection.EAST_WEST;
    }

    @Override
    public EnumFacing getActuatorLocation() {
        EnumFacing face = EnumFacing.NORTH;
        EnumRailDirection dir = getRailDirectionRaw();

        if (dir == EnumRailDirection.NORTH_SOUTH) {
            face = isMirrored() ? EnumFacing.EAST : EnumFacing.WEST;
        } else if (dir == EnumRailDirection.EAST_WEST) {
            face = isMirrored() ? EnumFacing.SOUTH : EnumFacing.NORTH;
        }
        return face;
    }
}