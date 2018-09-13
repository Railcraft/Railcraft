package mods.railcraft.common.carts;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.MapMaker;
import mods.railcraft.common.carts.Train.TrainInfo;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin.EnumNBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nullable;
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
        this.trains = new TrainMap(world, data.trains);
    }

    public static final class SaveData extends WorldSavedData {
        Map<UUID, TrainInfo> trains = new HashMap<>();

        static SaveData forWorld(World world) {
            MapStorage storage = world.getPerWorldStorage();
            SaveData result = (SaveData) storage.getOrLoadData(SaveData.class, "railcraft.trains");
            if (result == null) {
                result = new SaveData("railcraft.trains");
                storage.setData("railcraft.trains", result);
            }
            return result;
        }

        public SaveData(String name) {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            trains.clear();
            for (NBTTagCompound each : NBTPlugin.<NBTTagCompound>getNBTList(nbt, "trains", EnumNBTType.COMPOUND)) {
                TrainInfo train = new TrainInfo();
                train.readFromNBT(each);
                trains.put(train.id, train);
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            NBTTagList listTag = new NBTTagList();
            for (TrainInfo each : trains.values()) {
                NBTTagCompound tag = new NBTTagCompound();
                each.writeToNBT(tag);
                listTag.appendTag(tag);
            }
            compound.setTag("trains", listTag);
            return compound;
        }
    }

    static final class TrainMap extends ForwardingMap<UUID, Train> {
        private final Map<UUID, TrainInfo> ref;
        private final Map<UUID, Train> delegate = new HashMap<>();
        private final World world;

        TrainMap(World world, Map<UUID, TrainInfo> ref) {
            this.world = world;
            this.ref = ref;
        }

        @Override
        protected Map<UUID, Train> delegate() {
            return delegate;
        }

        @Override
        public boolean containsKey(@Nullable Object key) {
            return delegate.containsKey(key) || ref.containsKey(key);
        }

        @Override
        public Train get(@Nullable Object key) {
            Train got = delegate.get(key);
            if (got == null) {
                TrainInfo info = ref.get(key);
                if (info != null) {
                    Train train = info.build(world);
                    delegate.put((UUID) key, train);
                    return train;
                }
            }
            return got;
        }

        @Override
        public Train put(UUID key, Train value) {
            Train ret = delegate.put(key, value);
            ref.put(key, value.getInfo());
            return ret;
        }
    }
}
