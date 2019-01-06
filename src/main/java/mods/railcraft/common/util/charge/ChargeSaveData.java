/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.charge;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.core.CollectionToolsAPI;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CovertJaguar on 8/1/2016 for Railcraft.
 */
public final class ChargeSaveData extends WorldSavedData {
    private static final String NAME = "railcraft.charge.";
    private final Map<BlockPos, Double> chargeLevels = CollectionToolsAPI.blockPosMap(new HashMap<>());

    public static ChargeSaveData getFor(Charge network, World world) {
        MapStorage storage = world.getPerWorldStorage();
        String dataIdentifier = NAME + network.name().toLowerCase();
        ChargeSaveData result = (ChargeSaveData) storage.getOrLoadData(ChargeSaveData.class, dataIdentifier);
        if (result == null) {
            result = new ChargeSaveData(dataIdentifier);
            storage.setData(dataIdentifier, result);
        }
        return result;
    }

    public ChargeSaveData(String name) {
        super(name);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (RailcraftConfig.printChargeDebug())
            Game.log().msg(Level.INFO, "Saving Charge Battery data...");
        NBTTagList list = new NBTTagList();
        for (Map.Entry<BlockPos, Double> entry : chargeLevels.entrySet()) {
            NBTTagCompound dataEntry = new NBTTagCompound();
            NBTPlugin.writeBlockPos(dataEntry, "pos", entry.getKey());
            dataEntry.setDouble("value", entry.getValue());
            list.appendTag(dataEntry);
        }
        nbt.setTag("batteries", list);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (RailcraftConfig.printChargeDebug())
            Game.log().msg(Level.INFO, "Loading Charge Battery data...");
        List<NBTTagCompound> list = NBTPlugin.getNBTList(nbt, "batteries", NBTTagCompound.class);
        for (NBTTagCompound entry : list) {
            BlockPos pos = NBTPlugin.readBlockPos(entry, "pos");
            if (pos != null)
                chargeLevels.put(pos, entry.getDouble("value"));
        }
    }

    public void initBattery(BatteryBlock battery) {
        battery.setCharge(chargeLevels.computeIfAbsent(battery.getPos(), blockPos -> battery.getInitialCharge()));
        markDirty();
    }

    public void updateBatteryRecord(BatteryBlock battery) {
        chargeLevels.put(battery.getPos(), battery.getCharge());
        markDirty();
    }

    public void removeBattery(BlockPos pos) {
        if (chargeLevels.remove(pos) != null)
            markDirty();
    }
}
