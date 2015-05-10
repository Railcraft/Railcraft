/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector.types;

import mods.railcraft.common.blocks.detector.Detector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.gui.EnumGui;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorTrain extends Detector {
    private short trainSize = 5;

    @Override
    public EnumDetector getType() {
        return EnumDetector.TRAIN;
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            int count = Train.getTrain(cart).size();
            if (count >= getTrainSize()) {
                return FULL_POWER;
            }
        }
        return NO_POWER;
    }

    @Override
    protected short updateInterval() {
        return 4;
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_TRAIN, player);
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setShort("size", getTrainSize());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        setTrainSize(data.getShort("size"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeShort(getTrainSize());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        setTrainSize(data.readShort());
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeShort(getTrainSize());
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        setTrainSize(data.readShort());
    }

    public short getTrainSize() {
        return trainSize;
    }

    public void setTrainSize(short trainSize) {
        this.trainSize = trainSize;
    }
}
