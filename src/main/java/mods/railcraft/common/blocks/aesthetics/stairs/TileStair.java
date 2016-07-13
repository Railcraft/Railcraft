/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.stairs;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileStair extends RailcraftTileEntity {

    @Nonnull
    private BlockMaterial material = BlockMaterial.getPlaceholder();

    public BlockMaterial getMaterial() {
        return material;
    }

    public void setStair(BlockMaterial stair) {
        this.material = stair;
    }

    @Override
    public String getLocalizationTag() {
        return BlockRailcraftStairs.getTag(material);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setString("stair", material.getName());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.getTag("stair") instanceof NBTTagString) {
            material = BlockMaterial.fromName(data.getString("stair"));
        } else if (data.getTag("stair") instanceof NBTTagByte) {
            material = BlockMaterial.fromOrdinal(data.getByte("stair"));
        }
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeUTF(material.getName());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        material = BlockMaterial.fromName(data.readUTF());
    }

    @Override
    public short getId() {
        return 42;
    }
}
