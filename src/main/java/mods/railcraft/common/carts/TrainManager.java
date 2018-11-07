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
import mods.railcraft.common.util.misc.Game;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
public final class TrainManager {

    static final Map<World, TrainManager> instances = new MapMaker().weakKeys().makeMap();

    final World world;
    final TrainSaveData data;

    public static TrainManager forWorld(World world) {
        return instances.computeIfAbsent(world, TrainManager::new);
    }

    public static void clear() {
        instances.values().forEach(i -> i.data.clear());
    }

    TrainManager(World world) {
        this.world = world;
        this.data = TrainSaveData.forWorld(world);
    }

    public @Nullable Train get(@Nullable UUID trainID) {
        Train train = data.trains.get(trainID);
        if (train != null)
            train.setWorld(world);
        return train;
    }

    public @Nullable Train add(Train train) {
        Train old = data.trains.put(train.getUUID(), train);
        if (old != train)
            data.markDirty();
        return old;
    }

    public Optional<Train> remove(@Nullable UUID trainID) {
        Train old = data.trains.remove(trainID);
        if (old != null)
            data.markDirty();
        return Optional.ofNullable(old);
    }

    public static final class TrainSaveData extends WorldSavedData {
        Map<UUID, Train> trains = new HashMap<>();

        static TrainSaveData forWorld(World world) {
            MapStorage storage = world.getPerWorldStorage();
            TrainSaveData data = (TrainSaveData) storage.getOrLoadData(TrainSaveData.class, "railcraft.trains");
            if (data == null) {
                data = new TrainSaveData("railcraft.trains");
                storage.setData("railcraft.trains", data);
            }
            return data;
        }

        public TrainSaveData(String name) {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            trains.clear();
            for (NBTTagCompound each : NBTPlugin.getNBTList(nbt, "trains", NBTTagCompound.class)) {
                Train train = Train.readFromNBT(each);
                if (train != null)
                    trains.put(train.getUUID(), train);
            }
            Game.log(Level.INFO, "Loaded {0} Trains...", trains.size());
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            Game.log(Level.INFO, "Saving {0} Trains...", trains.size());
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

        public void clear() {
            trains.clear();
            markDirty();
        }
    }
}
