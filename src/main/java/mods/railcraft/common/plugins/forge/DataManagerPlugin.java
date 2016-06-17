/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.plugins.forge;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

/**
 * Created by CovertJaguar on 6/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DataManagerPlugin {

    public static <T> DataParameter<T> create(Class<?> clazz, DataSerializer<T> serializer) {
        return EntityDataManager.createKey(clazz.asSubclass(Entity.class), serializer);
    }

    public interface DataWrapper<T> {
        void set(T value);

        T get();
    }

    public static class EnumDataWrapper<T extends Enum<T>> implements DataWrapper<T> {
        public static <T extends Enum<T>> DataWrapper<T> create(Entity entity, T[] enumValues) {
            return new EnumDataWrapper<T>(entity, enumValues);
        }

        private final EntityDataManager dataManager;
        private final DataParameter<Byte> parameter;
        private final T[] enumValues;

        private EnumDataWrapper(Entity entity, T[] enumValues) {
            parameter = DataManagerPlugin.create(entity.getClass(), DataSerializers.BYTE);
            dataManager = entity.getDataManager();
            this.enumValues = enumValues;
        }

        @Override
        public void set(T value) {
            dataManager.set(parameter, (byte) value.ordinal());
        }

        @Override
        public T get() {
            return enumValues[dataManager.get(parameter)];
        }
    }
}
