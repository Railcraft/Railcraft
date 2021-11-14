/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.api.signals;

import mods.railcraft.api.core.WorldCoordinate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SignalBlockRelay extends SignalBlock {

    private final Map<WorldCoordinate, SignalAspect> aspects = new HashMap<WorldCoordinate, SignalAspect>();

    public SignalBlockRelay(String locTag, TileEntity tile) {
        super(locTag, tile, 2);
    }

    @Override
    protected void updateSignalAspect() {
        aspects.keySet().retainAll(getPairs());
        for (WorldCoordinate otherCoord : getPairs()) {
            aspects.put(otherCoord, determineAspect(otherCoord));
        }
    }

    @Override
    public SignalAspect getSignalAspect() {
        if (isWaitingForRetest() || isBeingPaired()) {
            return SignalAspect.BLINK_YELLOW;
        }
        if (!isPaired()) {
            return SignalAspect.BLINK_RED;
        }
        SignalAspect aspect = SignalAspect.GREEN;
        for (WorldCoordinate otherCoord : getPairs()) {
            aspect = SignalAspect.mostRestrictive(aspect, aspects.get(otherCoord));
        }
        return aspect;
    }

    @Override
    protected SignalAspect getSignalAspectForPair(WorldCoordinate otherCoord) {
        SignalAspect aspect = SignalAspect.GREEN;
        for (Map.Entry<WorldCoordinate, SignalAspect> entry : aspects.entrySet()) {
            if (entry.getKey().equals(otherCoord)) {
                continue;
            }
            aspect = SignalAspect.mostRestrictive(aspect, entry.getValue());
        }
        return aspect;
    }

    @Override
    protected void saveNBT(NBTTagCompound data) {
        super.saveNBT(data);
        NBTTagList tagList = data.getTagList("aspects", 10);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbt = tagList.getCompoundTagAt(i);
            WorldCoordinate coord = WorldCoordinate.readFromNBT(nbt, "coord");
            SignalAspect aspect = SignalAspect.readFromNBT(nbt, "aspect");
            aspects.put(coord, aspect);
        }
    }

    @Override
    protected void loadNBT(NBTTagCompound data) {
        super.loadNBT(data);
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<WorldCoordinate, SignalAspect> entry : aspects.entrySet()) {
            NBTTagCompound nbt = new NBTTagCompound();
            if (entry.getKey() != null && entry.getValue() != null) {
                entry.getKey().writeToNBT(nbt, "coord");
                entry.getValue().writeToNBT(nbt, "aspect");
                tagList.appendTag(nbt);
            }
        }
        data.setTag("aspects", tagList);
    }
}
