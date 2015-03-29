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
import mods.railcraft.api.tracks.ITrackReversable;
import mods.railcraft.common.carts.CartUtils;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class TrackSwitch extends TrackSwitchBase implements ITrackReversable {
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
    public int getBasicRailMetadata(EntityMinecart cart) {
        int meta = tileEntity.getBlockMetadata();
        if (cart != null && shouldSwitchForCart(cart)) {
            if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
                if (isMirrored()) {
                    if (reversed) {
                        meta = EnumTrackMeta.WEST_SOUTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.WEST_NORTH_CORNER.ordinal();
                    }
                } else {
                    if (reversed) {
                        meta = EnumTrackMeta.EAST_NORTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.EAST_SOUTH_CORNER.ordinal();
                    }
                }
            } else if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
                if (isMirrored()) {
                    if (reversed) {
                        meta = EnumTrackMeta.WEST_NORTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.EAST_NORTH_CORNER.ordinal();
                    }
                } else {
                    if (reversed) {
                        meta = EnumTrackMeta.EAST_SOUTH_CORNER.ordinal();
                    } else {
                        meta = EnumTrackMeta.WEST_SOUTH_CORNER.ordinal();
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
        if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            if (isReversed() != isMirrored()) {
                z++;
            } else {
                z--;
            }
        } else if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
            if (!isReversed() != isMirrored()) {
                x++;
            } else {
                x--;
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
        if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            if (isReversed() != isMirrored()) {
                z--;
            } else {
                z++;
            }
        } else if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
            if (!isReversed() != isMirrored()) {
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
        if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            if (isMirrored()) {
                x--;
            } else {
                x++;
            }
        } else if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
            if (isMirrored()) {
                z--;
            } else {
                z++;
            }
        }
        return CartUtils.getMinecartUUIDsAt(getWorld(), x, y, z, 0.1f);
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
    public ForgeDirection getActuatorLocation() {
        ForgeDirection dir = ForgeDirection.NORTH;
        int meta = tileEntity.getBlockMetadata();
        if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            if (isMirrored()) {
                dir = ForgeDirection.EAST;
            } else {
                dir = ForgeDirection.WEST;
            }
        } else if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
            if (isMirrored()) {
                dir = ForgeDirection.SOUTH;
            } else {
                dir = ForgeDirection.NORTH;
            }
        }
        return dir;
    }
}