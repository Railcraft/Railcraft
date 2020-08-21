/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import com.mojang.authlib.GameProfile;
import mods.railcraft.common.carts.EntityCartBed;
import mods.railcraft.common.carts.EntityLocomotive;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;

import static mods.railcraft.common.util.network.PacketKeyPress.EnumKeyBinding.VALUES;

public class PacketKeyPress extends RailcraftPacket {

    public enum EnumKeyBinding {

        LOCOMOTIVE_REVERSE,
        LOCOMOTIVE_INCREASE_SPEED,
        LOCOMOTIVE_DECREASE_SPEED,
        LOCOMOTIVE_MODE_CHANGE,
        LOCOMOTIVE_WHISTLE,
        BED_CART_SLEEP;
        public static final EnumKeyBinding[] VALUES = values();
    }

    private EntityPlayerMP player;
    private EnumKeyBinding binding;

    public PacketKeyPress(EntityPlayerMP player) {
        this.player = player;
    }

    public PacketKeyPress(EnumKeyBinding binding) {
        this.binding = binding;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeByte(binding.ordinal());
    }

    @Override
    public void readData(RailcraftInputStream data) throws IOException {
        int type = data.readByte();
        binding = VALUES[type];
        if (!(player.getRidingEntity() instanceof EntityMinecart))
            return;
        EntityMinecart cart = (EntityMinecart) player.getRidingEntity();
        GameProfile gameProfile = player.getGameProfile();
        switch (binding) {
            case LOCOMOTIVE_REVERSE:
                EntityLocomotive.applyAction(gameProfile, cart, false, loco -> loco.setReverse(!loco.isReverse()));
                break;
            case LOCOMOTIVE_INCREASE_SPEED:
                EntityLocomotive.applyAction(gameProfile, cart, false, EntityLocomotive::increaseSpeed);
                break;
            case LOCOMOTIVE_DECREASE_SPEED:
                EntityLocomotive.applyAction(gameProfile, cart, false, EntityLocomotive::decreaseSpeed);
                break;
            case LOCOMOTIVE_MODE_CHANGE:
                EntityLocomotive.applyAction(gameProfile, cart, false, loco -> {
                    EntityLocomotive.LocoMode mode = loco.getMode();
                    if (mode == EntityLocomotive.LocoMode.RUNNING)
                        loco.setMode(EntityLocomotive.LocoMode.IDLE);
                    else
                        loco.setMode(EntityLocomotive.LocoMode.RUNNING);
                });
                break;
            case LOCOMOTIVE_WHISTLE:
                EntityLocomotive.applyAction(gameProfile, cart, true, EntityLocomotive::whistle);
                break;
            case BED_CART_SLEEP:
                Entity ridden = player.getRidingEntity();
                if (ridden instanceof EntityCartBed) {
                    ((EntityCartBed) ridden).attemptSleep();
                }
                break;
        }
    }

    @Override
    public int getID() {
        return PacketType.KEY_PRESS.ordinal();
    }

}
