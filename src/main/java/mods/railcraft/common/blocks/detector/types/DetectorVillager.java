/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.detector.types;

import mods.railcraft.common.blocks.detector.Detector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.gui.EnumGui;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

public class DetectorVillager extends Detector {

    private int profession;
    private Mode mode = Mode.ANY;

    @Override
    public EnumDetector getType() {
        return EnumDetector.VILLAGER;
    }

    private boolean cartHasVillager(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            if (cart.getPassengers().stream().anyMatch(entity -> entity instanceof EntityVillager))
                return true;
        }
        return false;
    }

    private boolean cartHasProfession(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            if (cart.getPassengers().stream().filter(e -> e instanceof EntityVillager).map(entity -> (EntityVillager) entity)
                    .anyMatch(villager -> villager.getProfession() == profession))
                return true;
        }
        return false;
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        switch (mode) {
            case ANY:
                return cartHasVillager(carts) ? FULL_POWER : NO_POWER;
            case NONE:
                return !cartHasVillager(carts) ? FULL_POWER : NO_POWER;
            case EQUALS:
                return cartHasProfession(carts) ? FULL_POWER : NO_POWER;
            case NOT:
                return !cartHasProfession(carts) ? FULL_POWER : NO_POWER;
            default:
                return NO_POWER;
        }
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_VILLAGER, player);
        return true;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getProfession() {
        return profession;
    }

    public void setProfession(int profession) {
        this.profession = profession;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setInteger("profession", profession);
        data.setByte("mode", (byte) mode.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        profession = data.getInteger("profession");
        mode = Mode.values()[data.getByte("mode")];
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeInt(profession);
        data.writeByte((byte) mode.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        profession = data.readInt();
        mode = Mode.values()[data.readByte()];
    }

    @Override
    public void writeGuiData(@Nonnull DataOutputStream data) throws IOException {
        data.writeInt(profession);
        data.writeByte(mode.ordinal());
    }

    @Override
    public void readGuiData(@Nonnull DataInputStream data, EntityPlayer sender) throws IOException {
        profession = data.readInt();
        mode = Mode.values()[data.readByte()];
    }

    public enum Mode {

        ANY, NONE, NOT, EQUALS
    }

}
