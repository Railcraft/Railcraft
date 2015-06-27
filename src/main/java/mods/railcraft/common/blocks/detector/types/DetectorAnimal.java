/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector.types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import mods.railcraft.common.blocks.detector.Detector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import static mods.railcraft.common.plugins.forge.PowerPlugin.*;

public class DetectorAnimal extends Detector implements IGuiReturnHandler {

    public boolean chicken = true;
    public boolean cow = true;
    public boolean pig = true;
    public boolean sheep = true;
    public boolean mooshroom = true;
    public boolean wolf = true;
    public boolean other = true;

    @Override
    public EnumDetector getType() {
        return EnumDetector.ANIMAL;
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            if (cart.riddenByEntity instanceof EntityChicken) {
                if (chicken) {
                    return FULL_POWER;
                }
            } else if (cart.riddenByEntity instanceof EntityMooshroom) {
                if (mooshroom) {
                    return FULL_POWER;
                }
            } else if (cart.riddenByEntity instanceof EntityPig) {
                if (pig) {
                    return FULL_POWER;
                }
            } else if (cart.riddenByEntity instanceof EntitySheep) {
                if (sheep) {
                    return FULL_POWER;
                }
            } else if (cart.riddenByEntity instanceof EntityCow) {
                if (cow) {
                    return FULL_POWER;
                }
            } else if (cart.riddenByEntity instanceof EntityWolf) {
                if (wolf) {
                    return FULL_POWER;
                }
            } else if (cart.riddenByEntity instanceof EntityAnimal) {
                if (other) {
                    return FULL_POWER;
                }
            }
        }
        return NO_POWER;
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_ANIMAL, player);
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("chicken", chicken);
        data.setBoolean("cow", cow);
        data.setBoolean("pig", pig);
        data.setBoolean("sheep", sheep);
        data.setBoolean("mooshroom", mooshroom);
        data.setBoolean("wolf", wolf);
        data.setBoolean("other", other);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        chicken = data.getBoolean("chicken");
        cow = data.getBoolean("cow");
        pig = data.getBoolean("pig");
        sheep = data.getBoolean("sheep");
        mooshroom = data.getBoolean("mooshroom");
        wolf = data.getBoolean("wolf");
        other = data.getBoolean("other");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        writeGuiData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        readGuiData(data, null);
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        byte bits = 0;
        bits |= chicken ? 1 : 0;
        bits |= cow ? 2 : 0;
        bits |= pig ? 4 : 0;
        bits |= sheep ? 8 : 0;
        bits |= mooshroom ? 16 : 0;
        bits |= wolf ? 32 : 0;
        bits |= other ? 64 : 0;

        data.writeByte(bits);
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        byte bits = data.readByte();
        chicken = (bits & 1) != 0;
        cow = (bits & 2) != 0;
        pig = (bits & 4) != 0;
        sheep = (bits & 8) != 0;
        mooshroom = (bits & 16) != 0;
        wolf = (bits & 32) != 0;
        other = (bits & 64) != 0;
    }

}
