/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import it.unimi.dsi.fastutil.longs.Long2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
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

import java.util.List;

/**
 * Created by CovertJaguar on 8/1/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class BatterySaveData extends WorldSavedData {
    private static final String NAME = "railcraft.batteries";
    private Long2DoubleMap chargeLevels = new Long2DoubleLinkedOpenHashMap();

    public static BatterySaveData forWorld(World world) {
        MapStorage storage = world.getPerWorldStorage();
        BatterySaveData result = (BatterySaveData) storage.getOrLoadData(BatterySaveData.class, NAME);
        if (result == null) {
            result = new BatterySaveData();
            storage.setData(NAME, result);
        }
        return result;
    }

    BatterySaveData() {
        super(NAME);
        chargeLevels.defaultReturnValue(Double.NaN);
    }

    @Deprecated // called by reflection
    public BatterySaveData(String name) {
        super(name);
        chargeLevels.defaultReturnValue(Double.NaN);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (RailcraftConfig.printChargeDebug())
            Game.log(Level.INFO, "Saving Charge Battery data...");
        NBTTagList list = new NBTTagList();
        for (Long2DoubleMap.Entry entry : chargeLevels.long2DoubleEntrySet()) {
            NBTTagCompound dataEntry = new NBTTagCompound();
            NBTPlugin.writeBlockPos(dataEntry, "pos", BlockPos.fromLong(entry.getLongKey()));
            dataEntry.setDouble("value", entry.getDoubleValue());
            list.appendTag(dataEntry);
        }
        nbt.setTag("batteries", list);
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
                chargeLevels.put(pos.toLong(), entry.getDouble("value"));
        }
    }

    public void initBattery(BlockPos pos, IChargeBlock.ChargeBattery chargeBattery) {
        double charge = chargeLevels.get(pos.toLong());
        if (Double.isNaN(charge))
            charge = 0.0;
        chargeBattery.initCharge(charge);
    }

    public void updateBatteryRecord(BlockPos pos, IChargeBlock.ChargeBattery chargeBattery) {
        chargeLevels.put(pos.toLong(), chargeBattery.getCharge());
        markDirty();
    }

    public void removeBattery(BlockPos pos) {
        if (!Double.isNaN(chargeLevels.remove(pos.toLong())))
            markDirty();
    }
}
