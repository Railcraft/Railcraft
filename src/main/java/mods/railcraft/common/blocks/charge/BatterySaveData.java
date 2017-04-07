/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import org.apache.logging.log4j.Level;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CovertJaguar on 8/1/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BatterySaveData extends WorldSavedData {
    private static final String NAME = "railcraft.batteries";
    private Map<BlockPos, Double> chargeLevels = new LinkedHashMap<>();

    public static BatterySaveData forWorld(World world) {
        MapStorage storage = world.getPerWorldStorage();
        BatterySaveData result = (BatterySaveData) storage.getOrLoadData(BatterySaveData.class, NAME);
        if (result == null) {
            result = new BatterySaveData(NAME);
            storage.setData(NAME, result);
        }
        return result;
    }

    @Deprecated
    public BatterySaveData(String name) {
        super(name);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (Game.DEVELOPMENT_ENVIRONMENT)
            Game.log(Level.INFO, "Saving Charge Battery data...");
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
        if (Game.DEVELOPMENT_ENVIRONMENT)
            Game.log(Level.INFO, "Loading Charge Battery data...");
        List<NBTTagCompound> list = NBTPlugin.getNBTList(nbt, "batteries", NBTPlugin.EnumNBTType.COMPOUND);
        for (NBTTagCompound entry : list) {
            BlockPos pos = NBTPlugin.readBlockPos(entry, "pos");
            if (pos != null)
                chargeLevels.put(pos, nbt.getDouble("value"));
        }
    }

    public void initBattery(BlockPos pos, IChargeBlock.ChargeBattery chargeBattery) {
        if (!chargeBattery.isInfinite())
            chargeBattery.setCharge(chargeLevels.getOrDefault(pos, 0.0));
    }

    public void updateBatteryRecord(BlockPos pos, IChargeBlock.ChargeBattery chargeBattery) {
        if (!chargeBattery.isInfinite()) {
            chargeLevels.put(pos, chargeBattery.getCharge());
            markDirty();
        }
    }

    public void removeBattery(BlockPos pos) {
        if (chargeLevels.remove(pos) != null)
            markDirty();
    }
}
