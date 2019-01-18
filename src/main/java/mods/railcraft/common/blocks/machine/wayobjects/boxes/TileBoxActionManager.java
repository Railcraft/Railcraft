/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.util.BitSet;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class TileBoxActionManager extends TileBoxSecured {

    private BitSet powerOnAspects = new BitSet(SignalAspect.VALUES.length);

    protected TileBoxActionManager() {
        setActionState(SignalAspect.GREEN, true);
    }

    @Override
    public boolean doesActionOnAspect(SignalAspect aspect) {
        return powerOnAspects.get(aspect.ordinal());
    }

    protected final void setActionState(SignalAspect aspect, boolean trigger) {
        powerOnAspects.set(aspect.ordinal(), trigger);
    }

    @Override
    public void doActionOnAspect(SignalAspect aspect, boolean trigger) {
        setActionState(aspect, trigger);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByteArray("powerOnAspects", powerOnAspects.toByteArray());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("PowerOnAspect")) {
            powerOnAspects = BitSet.valueOf(data.getByteArray("PowerOnAspect"));
        } else if (data.hasKey("powerOnAspects")) {
            powerOnAspects = BitSet.valueOf(data.getByteArray("powerOnAspects"));
        }
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        writeActionInfo(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        powerOnAspects = data.readBitSet();
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        super.writeGuiData(data);
        writeActionInfo(data);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        BitSet bits = data.readBitSet();
        if (sender == null || canAccess(sender.getGameProfile())) {
            powerOnAspects = bits;
        }
    }

    private void writeActionInfo(RailcraftOutputStream data) throws IOException {
        data.writeBitSet(powerOnAspects);
    }

}
