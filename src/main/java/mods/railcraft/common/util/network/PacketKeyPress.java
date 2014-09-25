/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityLocomotive.LocoMode;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.carts.Train;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import static mods.railcraft.common.util.network.PacketKeyPress.EnumKeyBinding.*;

public class PacketKeyPress extends RailcraftPacket {

    public enum EnumKeyBinding {

        LOCOMOTIVE_INCREASE_SPEED,
        LOCOMOTIVE_DECREASE_SPEED,
        LOCOMOTIVE_MODE_CHANGE,
        LOCOMOTIVE_WHISTLE;
        public static final EnumKeyBinding[] VALUES = values();
    }
    private EntityPlayerMP player;
    private EnumKeyBinding binding;

    public PacketKeyPress(EntityPlayerMP player) {
        super();
        this.player = player;
    }

    public PacketKeyPress(EnumKeyBinding binding) {
        this.binding = binding;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        data.writeByte(binding.ordinal());
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        int type = data.readByte();
//        if(type < 0 || type >= VALUES.length){
//            return;
//        }
        binding = VALUES[type];
//        if(!(player instanceof EntityPlayer)){
//            return;
//        }
        EntityPlayer entiyPlayer = (EntityPlayer) player;
        if (entiyPlayer == null)
            return;
        if (!(entiyPlayer.ridingEntity instanceof EntityMinecart))
            return;
        Train train = LinkageManager.instance().getTrain((EntityMinecart) entiyPlayer.ridingEntity);
        if (binding == LOCOMOTIVE_INCREASE_SPEED) {
            for (EntityMinecart cart : train) {
                if (cart instanceof EntityLocomotive) {
                    EntityLocomotive loco = (EntityLocomotive) cart;
                    if (loco.canControl(entiyPlayer.getGameProfile()))
                        loco.increaseSpeed();
                }
            }
        } else if (binding == LOCOMOTIVE_DECREASE_SPEED) {
            for (EntityMinecart cart : train) {
                if (cart instanceof EntityLocomotive) {
                    EntityLocomotive loco = (EntityLocomotive) cart;
                    if (loco.canControl(entiyPlayer.getGameProfile()))
                        loco.decreaseSpeed();
                }
            }
        } else if (binding == LOCOMOTIVE_MODE_CHANGE) {
            for (EntityMinecart cart : train) {
                if (cart instanceof EntityLocomotive) {
                    EntityLocomotive loco = (EntityLocomotive) cart;
                    if (loco.canControl(entiyPlayer.getGameProfile())) {
                        LocoMode mode = loco.getMode();
                        if (mode == LocoMode.RUNNING)
                            loco.setMode(LocoMode.IDLE);
                        else
                            loco.setMode(LocoMode.RUNNING);
                    }
                }
            }
        } else if (binding == LOCOMOTIVE_WHISTLE)
            for (EntityMinecart cart : train) {
                if (cart instanceof EntityLocomotive) {
                    EntityLocomotive loco = (EntityLocomotive) cart;
                    loco.whistle();
                    break;
                }
            }
    }

    @Override
    public int getID() {
        return PacketType.KEY_PRESS.ordinal();
    }

}
