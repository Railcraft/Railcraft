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
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.carts.CartUtils;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;

import java.util.List;
import java.util.UUID;

import static net.minecraft.block.BlockRailBase.EnumRailDirection.*;

public class TrackWye extends TrackSwitchBase {
    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.WYE;
    }

    //TODO: Replace with getActualState()?
    @Override
    public IIcon getIcon() {
        if (isVisuallySwitched()) {
            return getIcon(1);
        }
        return getIcon(0);
    }

    @Override
    public BlockRailBase.EnumRailDirection getRailDirection(IBlockState state, EntityMinecart cart) {
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
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        int meta = tileEntity.getBlockMetadata();
        if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
            if (isMirrored()) {
                x--;
            } else {
                x++;
            }
        } else if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            if (isMirrored()) {
                z++;
            } else {
                z--;
            }
        }
        return CartUtils.getMinecartUUIDsAt(getWorld(), x, y, z, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtDecisionEntrance() {
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        int meta = tileEntity.getBlockMetadata();
        if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
            if (isMirrored()) {
                z--;
            } else {
                z++;
            }
        } else if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            if (isMirrored()) {
                x--;
            } else {
                x++;
            }
        }
        return CartUtils.getMinecartUUIDsAt(getWorld(), x, y, z, 0.1f);
    }

    @Override
    protected List<UUID> getCartsAtSpringEntrance() {
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        int meta = tileEntity.getBlockMetadata();
        if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
            if (isMirrored()) {
                x++;
            } else {
                x--;
            }
        } else if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            if (isMirrored()) {
                z--;
            } else {
                z++;
            }
        }
        return CartUtils.getMinecartUUIDsAt(getWorld(), x, y, z, 0.1f);
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
