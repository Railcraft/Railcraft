/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import com.google.common.collect.MapMaker;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
final class TrainManager {

    static final Map<World, TrainManager> instances = new MapMaker().weakKeys().makeMap();

    final Map<UUID, Train> trains;
    final World world;
    final SaveData data;

    static TrainManager getInstance(World world) {
        return instances.computeIfAbsent(world, TrainManager::new);
    }

    TrainManager(World world) {
        this.world = world;
        this.data = SaveData.forWorld(world);
        this.trains = new HashMap<>(data.trains);
    }

    public static final class SaveData extends WorldSavedData {
        Map<UUID, Train> trains = new HashMap<>();
        World world;

        static SaveData forWorld(World world) {
            MapStorage storage = world.getPerWorldStorage();
            SaveData saveData = (SaveData) storage.getOrLoadData(SaveData.class, "railcraft.trains");
            if (saveData == null) {
                saveData = new SaveData("railcraft.trains");
                storage.setData("railcraft.trains", saveData);
            }
            saveData.world = world;
            return saveData;
        }

        public SaveData(String name) {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            trains.clear();
            for (NBTTagCompound each : NBTPlugin.getNBTList(nbt, "trains", NBTTagCompound.class)) {
                Train train = Train.readFromNBT(each, world);
                if (train != null)
                    trains.put(train.getUUID(), train);
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            NBTTagList listTag = new NBTTagList();
            for (Train train : trains.values()) {
                NBTTagCompound tag = new NBTTagCompound();
                train.writeToNBT(tag);
                listTag.appendTag(tag);
                train.setDirty(false);
            }
            compound.setTag("trains", listTag);
            return compound;
        }

        @Override
        public boolean isDirty() {
            return super.isDirty() || trains.values().stream().anyMatch(Train::isDirty);
        }
    }
}
