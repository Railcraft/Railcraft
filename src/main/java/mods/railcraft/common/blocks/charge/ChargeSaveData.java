/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.ChargeNodeDefinition;
import mods.railcraft.api.charge.IBlockBattery;
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
import java.util.Map.Entry;

/**
 * Created by CovertJaguar on 8/1/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class ChargeSaveData extends WorldSavedData {
    private static final String NAME = "railcraft.batteries";
    private Map<BlockPos, Double> chargeLevels = new HashMap<>();
    private Map<BlockPos, ChargeNodeDefinition> positions = new HashMap<>();

    static ChargeSaveData forWorld(World world) {
        MapStorage storage = world.getPerWorldStorage();
        ChargeSaveData result = (ChargeSaveData) storage.getOrLoadData(ChargeSaveData.class, NAME);
        if (result == null) {
            result = new ChargeSaveData();
            storage.setData(NAME, result);
        }
        return result;
    }

    ChargeSaveData() {
        super(NAME);
    }

    @Deprecated // called by reflection
    public ChargeSaveData(String name) {
        super(name);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (RailcraftConfig.printChargeDebug())
            Game.log(Level.INFO, "Saving Charge Battery data...");
        NBTTagList list = new NBTTagList();
        for (Entry<BlockPos, Double> entry : chargeLevels.entrySet()) {
            NBTTagCompound dataEntry = new NBTTagCompound();
            NBTPlugin.writeBlockPos(dataEntry, "pos", entry.getKey());
            dataEntry.setDouble("value", entry.getValue());
            list.appendTag(dataEntry);
        }
        nbt.setTag("batteries", list);
        NBTTagList nodes = new NBTTagList();
        for (Entry<BlockPos, ChargeNodeDefinition> pair : positions.entrySet()) {
            NBTTagCompound dataEntry = new NBTTagCompound();
            NBTPlugin.writeBlockPos(dataEntry, "pos", pair.getKey());
            pair.getValue().writeToNBT(dataEntry);
            nodes.appendTag(dataEntry);
        }
        nbt.setTag("nodes", nodes);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (RailcraftConfig.printChargeDebug())
            Game.log(Level.INFO, "Loading Charge Battery data...");
        List<NBTTagCompound> list = NBTPlugin.getNBTList(nbt, "batteries", NBTPlugin.EnumNBTType.COMPOUND);
        for (NBTTagCompound entry : list) {
            BlockPos pos = NBTPlugin.readBlockPos(entry, "pos");
            if (pos != null)
                chargeLevels.put(pos, entry.getDouble("value"));
        }

        List<NBTTagCompound> nodes = NBTPlugin.getNBTList(nbt, "nodes", NBTPlugin.EnumNBTType.COMPOUND);
        for (NBTTagCompound entry : nodes) {
            BlockPos pos = NBTPlugin.readBlockPos(entry, "pos");
            ChargeNodeDefinition definition = ChargeNodeDefinition.readFromNBT(entry);
            positions.put(pos, definition);
        }
    }

    void initBattery(BlockPos pos, IBlockBattery chargeBattery) {
        Double charge = chargeLevels.get(pos);
        chargeBattery.initCharge(charge == null ? 0.0 : charge);
    }

    void updateBatteryRecord(BlockPos pos, IBlockBattery chargeBattery) {
        chargeLevels.put(pos, chargeBattery.getCharge());
        markDirty();
    }

    void removeBattery(BlockPos pos) {
        if (chargeLevels.remove(pos) != null)
            markDirty();
    }

    Map<BlockPos, ChargeNodeDefinition> getPositions() {
        return positions;
    }
}
