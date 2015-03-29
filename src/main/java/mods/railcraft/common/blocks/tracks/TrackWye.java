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
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.UUID;

public class TrackWye extends TrackSwitchBase {
    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.WYE;
    }

    @Override
    public IIcon getIcon() {
        if (isVisuallySwitched()) {
            return getIcon(1);
        }
        return getIcon(0);
    }

    @Override
    public int getBasicRailMetadata(EntityMinecart cart) {
        int meta = tileEntity.getBlockMetadata();
        if (cart != null) {
            if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
                if (isMirrored()) {
                    if (shouldSwitchForCart(cart)) {
                        meta = EnumTrackMeta.WEST_NORTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.WEST_SOUTH_CORNER.ordinal();
                    }
                } else {
                    if (shouldSwitchForCart(cart)) {
                        meta = EnumTrackMeta.EAST_SOUTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.EAST_NORTH_CORNER.ordinal();
                    }
                }
            } else if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
                if (isMirrored()) {
                    if (shouldSwitchForCart(cart)) {
                        meta = EnumTrackMeta.EAST_NORTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.WEST_NORTH_CORNER.ordinal();
                    }
                } else {
                    if (shouldSwitchForCart(cart)) {
                        meta = EnumTrackMeta.WEST_SOUTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.EAST_SOUTH_CORNER.ordinal();
                    }
                }
            }
        }
        return meta;
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
    public ForgeDirection getActuatorLocation() {
        ForgeDirection dir = ForgeDirection.NORTH;
        int meta = tileEntity.getBlockMetadata();
        if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
            if (isMirrored()) {
                dir = ForgeDirection.SOUTH;
            } else {
                dir = ForgeDirection.NORTH;
            }
        } else if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            if (isMirrored()) {
                dir = ForgeDirection.EAST;
            } else {
                dir = ForgeDirection.WEST;
            }
        }
        return dir;
    }
}
