/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.common.carts.EntityCartBed;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.util.collections.Streams;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
        switch (binding) {
            case LOCOMOTIVE_REVERSE:
                applyAction(player, false, loco -> loco.setReverse(!loco.isReverse()));
                break;
            case LOCOMOTIVE_INCREASE_SPEED:
                applyAction(player, false, EntityLocomotive::increaseSpeed);
                break;
            case LOCOMOTIVE_DECREASE_SPEED:
                applyAction(player, false, EntityLocomotive::decreaseSpeed);
                break;
            case LOCOMOTIVE_MODE_CHANGE:
                applyAction(player, false, loco -> {
                    EntityLocomotive.LocoMode mode = loco.getMode();
                    if (mode == EntityLocomotive.LocoMode.RUNNING)
                        loco.setMode(EntityLocomotive.LocoMode.IDLE);
                    else
                        loco.setMode(EntityLocomotive.LocoMode.RUNNING);
                });
                break;
            case LOCOMOTIVE_WHISTLE:
                applyAction(player, true, EntityLocomotive::whistle);
                break;
            case BED_CART_SLEEP:
                Entity ridden = player.getRidingEntity();
                if (ridden instanceof EntityCartBed) {
                    ((EntityCartBed) ridden).attemptSleep();
                }
                break;
        }
    }

    private void applyAction(@Nullable EntityPlayer player, boolean single, Consumer<EntityLocomotive> action) {
        if (player == null)
            return;
        if (!(player.getRidingEntity() instanceof EntityMinecart))
            return;
        Stream<EntityLocomotive> locos = Train.streamCarts((EntityMinecart) player.getRidingEntity())
                .flatMap(Streams.toType(EntityLocomotive.class))
                .filter(loco -> loco.canControl(player.getGameProfile()));
        if (single) {
            locos.findAny().ifPresent(action);
        } else {
            locos.forEach(action);
        }
    }

    @Override
    public int getID() {
        return PacketType.KEY_PRESS.ordinal();
    }

}
