/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.util.misc.EnumColor;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TilePostEmblem extends RailcraftTileEntity {
    private ForgeDirection facing = ForgeDirection.NORTH;
    private String emblem = "";
    private EnumColor color = null;

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        setFacing(MiscTools.getHorizontalSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving));
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            if (nbt.hasKey("color"))
                setColor(EnumColor.fromId(nbt.getByte("color")));
            if (nbt.hasKey("emblem"))
                setEmblem(nbt.getString("emblem"));
        }
    }

    public ForgeDirection getFacing() {
        return facing;
    }

    public void setFacing(ForgeDirection f) {
        switch (f) {
            case UP:
            case DOWN:
            case UNKNOWN:
                return;
        }
        if (f != facing) {
            facing = f;
            markBlockForUpdate();
        }
    }

    public String getEmblem() {
        return emblem;
    }

    public void setEmblem(String identifier) {
        if (!emblem.equals(identifier)) {
            emblem = identifier;
            sendUpdateToClient();
        }
    }

    public EnumColor getColor() {
        return color;
    }

    public void setColor(EnumColor color) {
        if (this.color != color) {
            this.color = color;
            sendUpdateToClient();
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setString("emblem", emblem);
        data.setByte("facing", (byte) facing.ordinal());

        if (color != null)
            data.setByte("color", (byte) color.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        emblem = data.getString("emblem");
        facing = ForgeDirection.getOrientation(data.getByte("facing"));

        if (data.hasKey("color"))
            color = EnumColor.fromId(data.getByte("color"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte((byte) facing.ordinal());
        data.writeByte((byte) (color != null ? color.ordinal() : -1));
        data.writeUTF(emblem);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        boolean needsUpdate = false;
        ForgeDirection f = ForgeDirection.getOrientation(data.readByte());
        if (facing != f) {
            facing = f;
            needsUpdate = true;
        }

        byte cByte = data.readByte();
        if (cByte < 0) {
            if (color != null) {
                color = null;
                needsUpdate = true;
            }
        } else {
            EnumColor c = EnumColor.fromId(cByte);
            if (color != c) {
                color = c;
                needsUpdate = true;
            }
        }

        emblem = data.readUTF();

        if (needsUpdate)
            markBlockForUpdate();
    }

    @Override
    public String getLocalizationTag() {
        return "tile.railcraft.post.emblem.name";
    }

    @Override
    public short getId() {
        return (short) EnumPost.EMBLEM.ordinal();
    }
}
