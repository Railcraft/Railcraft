/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.IIcon;
import mods.railcraft.api.carts.CartTools;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TrackWye extends TrackSwitchBase {

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.WYE;
    }

    @Override
    public IIcon getIcon() {
        if (isSwitched()) {
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
                    if (isSwitched()) {
                        meta = EnumTrackMeta.WEST_NORTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.WEST_SOUTH_CORNER.ordinal();
                    }
                } else {
                    if (isSwitched()) {
                        meta = EnumTrackMeta.EAST_SOUTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.EAST_NORTH_CORNER.ordinal();
                    }
                }
            } else if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
                if (isMirrored()) {
                    if (isSwitched()) {
                        meta = EnumTrackMeta.EAST_NORTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.WEST_NORTH_CORNER.ordinal();
                    }
                } else {
                    if (isSwitched()) {
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
    protected boolean shouldLockSwitch() {
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
        return CartTools.isMinecartOnRailAt(getWorld(), x, y, z, 0.3f);
    }

    @Override
    protected boolean shouldSpringSwitch() {
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
        return CartTools.isMinecartOnRailAt(getWorld(), x, y, z, 0.3f);
    }

    @Override
    public AxisAlignedBB getRoutingSearchBox() {
        ForgeDirection side = ForgeDirection.WEST;
        if (EnumTrackMeta.EAST_WEST.isEqual(tileEntity.getBlockMetadata())) {
            if (isMirrored()) {
                side = ForgeDirection.NORTH;
            } else {
                side = ForgeDirection.SOUTH;
            }
        }
        if (isMirrored()) {
            side = ForgeDirection.EAST;
        }
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
        box = box.addCoord(side.offsetX, side.offsetY, side.offsetZ);
        box = box.addCoord(side.offsetX + 1, side.offsetY + 1, side.offsetZ + 1);
        box = box.offset(getX(), getY(), getZ());
        return box;
    }

    @Override
    public ArrowDirection getRedSignDirection() {
        if (EnumTrackMeta.EAST_WEST.isEqual(tileEntity.getBlockMetadata())) {
            if (isSwitched()) {
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
        if (isSwitched()) {
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

}
