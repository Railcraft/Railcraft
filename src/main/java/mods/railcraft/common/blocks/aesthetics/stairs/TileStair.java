/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.stairs;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.IIcon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileStair extends RailcraftTileEntity {
    private EnumBlockMaterial stair = EnumBlockMaterial.SANDY_BRICK;

    @Override
    public boolean canUpdate() {
        return false;
    }

    public IIcon getTexture(int side) {
        return stair.getIcon(side);
    }

    public EnumBlockMaterial getStair() {
        return stair;
    }

    public void setStair(EnumBlockMaterial stair) {
        this.stair = stair;
    }

    @Override
    public String getLocalizationTag() {
        return "tile." + BlockRailcraftStairs.getTag(stair);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setString("stair", stair.name());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.getTag("stair") instanceof NBTTagString) {
            stair = EnumBlockMaterial.fromName(data.getString("stair"));
        } else if (data.getTag("stair") instanceof NBTTagByte) {
            stair = EnumBlockMaterial.fromOrdinal(data.getByte("stair"));
        }
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte((byte) stair.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        stair = EnumBlockMaterial.fromOrdinal(data.readByte());
    }

    @Override
    public short getId() {
        return 42;
    }
}
