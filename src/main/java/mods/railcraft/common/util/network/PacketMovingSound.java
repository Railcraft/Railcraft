/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.network;

import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

class PacketMovingSound extends RailcraftPacket {
    private String soundName;
    private SoundCategory category;
    private int id;
    private SoundHelper.MovingSoundType type;
    private NBTTagCompound extraData;

    PacketMovingSound() {
    }

    PacketMovingSound(SoundEvent sound, SoundCategory category, EntityMinecart cart, SoundHelper.MovingSoundType type, NBTTagCompound extraData) {
        this.soundName = sound.soundName.toString();
        this.category = category;
        this.id = cart.getEntityId();
        this.type = type;
        this.extraData = extraData;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeInt(id);
        data.writeEnum(category);
        data.writeUTF(soundName);
        data.writeEnum(type);
        data.writeNBT(extraData);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(RailcraftInputStream data) throws IOException {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(data.readInt());
        SoundCategory category = data.readEnum(SoundCategory.values());
        SoundEvent event = new SoundEvent(new ResourceLocation(data.readUTF()));
        SoundHelper.MovingSoundType type = data.readEnum(SoundHelper.MovingSoundType.values());
        NBTTagCompound nbt = data.readNBT();
        if (!(entity instanceof EntityMinecart))
            return;
        type.handle(event, category, (EntityMinecart) entity, nbt);
    }

    @Override
    public int getID() {
        return PacketType.MOVING_SOUND.ordinal();
    }
}
