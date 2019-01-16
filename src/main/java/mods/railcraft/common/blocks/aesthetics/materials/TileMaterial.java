/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.materials;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileMaterial extends TileRailcraft {

    private Materials material = Materials.getPlaceholder();

    public Materials getMaterial() {
        return material;
    }

    public void setMaterial(Materials material) {
        this.material = material;
    }

    @Override
    public String getLocalizationTag() {
        return ((IMaterialBlock) getBlockType()).getTranslationKey(material) + ".name";
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setString("mat", material.getName());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("mat")) {
            material = Materials.fromName(data.getString("mat"));
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
        material = Materials.fromName(data.readUTF());
    }
}
