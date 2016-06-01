/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.util.misc.EnumColor;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TilePostEmblem extends RailcraftTileEntity {
    private EnumFacing facing = EnumFacing.NORTH;
    private String emblem = "";
    private EnumColor color;

    @Override
    public void onBlockPlacedBy(@Nonnull IBlockState state, @Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        setFacing(MiscTools.getHorizontalSideFacingPlayer(placer));
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            if (nbt.hasKey("color"))
                setColor(EnumColor.fromOrdinal(nbt.getByte("color")));
            if (nbt.hasKey("emblem"))
                setEmblem(nbt.getString("emblem"));
        }
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing f) {
        if (f == null)
            return;
        switch (f) {
            case UP:
            case DOWN:
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

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);
        data.setString("emblem", emblem);
        data.setByte("facing", (byte) facing.ordinal());

        if (color != null)
            data.setByte("color", (byte) color.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);
        emblem = data.getString("emblem");
        facing = EnumFacing.getFront(data.getByte("facing"));

        if (data.hasKey("color"))
            color = EnumColor.fromOrdinal(data.getByte("color"));
    }

    @Override
    public void writePacketData(@Nonnull RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte((byte) facing.ordinal());
        data.writeByte((byte) (color != null ? color.ordinal() : -1));
        data.writeUTF(emblem);
    }

    @Override
    public void readPacketData(@Nonnull RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        boolean needsUpdate = false;
        EnumFacing f = EnumFacing.getFront(data.readByte());
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
            EnumColor c = EnumColor.fromOrdinal(cByte);
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
