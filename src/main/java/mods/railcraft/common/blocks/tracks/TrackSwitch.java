/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.tracks.ITrackReversable;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

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
        if (isSwitched()) {
            index += 1;
        }
        return getIcon(index);
    }

    @Override
    public int getBasicRailMetadata(EntityMinecart cart) {
        int meta = tileEntity.getBlockMetadata();
        if (cart != null && isSwitched()) {
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
    protected boolean shouldLockSwitch() {
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
        return CartTools.isMinecartOnRailAt(getWorld(), x, y, z, 0.3f);
    }

    @Override
    protected boolean shouldSpringSwitch() {
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
        return CartTools.isMinecartOnRailAt(getWorld(), x, y, z, 0.3f);
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
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public boolean isReversed() {
        return reversed;
    }

    @Override
    public AxisAlignedBB getRoutingSearchBox() {
        ForgeDirection side = ForgeDirection.SOUTH;
        if (tileEntity.getBlockMetadata() == 1) {
            if (isReversed() != isMirrored()) {
                side = ForgeDirection.WEST;
            } else {
                side = ForgeDirection.EAST;
            }
        } else if (isReversed() != isMirrored()) {
            side = ForgeDirection.NORTH;
        }
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
        box = MiscTools.addCoordToAABB(box, side.offsetX, 0, side.offsetZ);
        box = MiscTools.addCoordToAABB(box, side.offsetX + 1, 1, side.offsetZ + 1);
        box = box.offset(getX(), getY(), getZ());
        return box;
    }

    @Override
    public ArrowDirection getRedSignDirection() {
        if (tileEntity.getBlockMetadata() == 1) {
            if (isSwitched()) {
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
        if (isSwitched()) {
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
            if (isSwitched()) {
                return ArrowDirection.EAST_WEST;
            }
            return ArrowDirection.NORTH_SOUTH;
        }
        if (isSwitched()) {
            return ArrowDirection.NORTH_SOUTH;
        }
        return ArrowDirection.EAST_WEST;
    }

}
