package mods.railcraft.common.util.network;

import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class PacketMovingSound extends RailcraftPacket {
    private String soundName;
    private SoundCategory category;
    private int id;
    private SoundHelper.MovingSoundType type;

    public PacketMovingSound() {
    }

    public PacketMovingSound(SoundEvent sound, SoundCategory category, EntityMinecart cart, SoundHelper.MovingSoundType type) {
        this.soundName = ReflectionHelper.getPrivateValue(SoundEvent.class, sound, 1).toString();
        this.category = category;
        this.id = cart.getEntityId();
        this.type = type;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeInt(id);
        data.writeEnum(category);
        data.writeUTF(soundName);
        data.writeEnum(type);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(RailcraftInputStream data) throws IOException {
        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(data.readInt());
        SoundCategory category = data.readEnum(SoundCategory.values());
        SoundEvent event = new SoundEvent(new ResourceLocation(data.readUTF()));
        SoundHelper.MovingSoundType type = data.readEnum(SoundHelper.MovingSoundType.values());
        if (entity == null || !(entity instanceof EntityMinecart))
            return;
        Minecraft.getMinecraft().addScheduledTask(() -> {
            type.handle(event, category, (EntityMinecart) entity);
        });
    }

    @Override
    public int getID() {
        return PacketType.MOVING_SOUND.ordinal();
    }
}
