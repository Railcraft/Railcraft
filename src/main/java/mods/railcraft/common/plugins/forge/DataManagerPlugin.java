/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.forge;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import mods.railcraft.common.fluids.OptionalFluidStack;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import java.io.IOException;

/**
 * Created by CovertJaguar on 6/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DataManagerPlugin {

    public abstract static class DataSerializerIO<T> implements DataSerializer<T> {
        @Override
        public final void write(PacketBuffer buf, T value) {
            try (ByteBufOutputStream out = new ByteBufOutputStream(buf);
                 RailcraftOutputStream data = new RailcraftOutputStream(out)) {
                write(data, value);
            } catch (IOException e) {
                Game.logThrowable("Error syncing FluidStack", e);
                if (Game.DEVELOPMENT_ENVIRONMENT)
                    throw new RuntimeException(e);
            }
        }

        protected abstract void write(RailcraftOutputStream outputStream, T value) throws IOException;

        @Override
        public final T read(PacketBuffer buf) throws IOException {
            try (ByteBufInputStream out = new ByteBufInputStream(buf);
                 RailcraftInputStream data = new RailcraftInputStream(out)) {
                return read(data);
            }
        }

        protected abstract T read(RailcraftInputStream inputStream) throws IOException;

        @Override
        public DataParameter<T> createKey(int id) {
            return new DataParameter<T>(id, this);
        }
    }

    public static final DataSerializer<OptionalFluidStack> OPTIONAL_FLUID_STACK = new DataSerializerIO<OptionalFluidStack>() {
        @Override
        public void write(RailcraftOutputStream outputStream, OptionalFluidStack value) throws IOException {
            outputStream.writeFluidStack(value.orElse(null));
//            Game.log(Level.INFO, "fluid write");
        }

        @Override
        public OptionalFluidStack read(RailcraftInputStream inputStream) throws IOException {
//            Game.log(Level.INFO, "fluid read");
            return OptionalFluidStack.of(inputStream.readFluidStack());
        }
    };

    public static final DataSerializer<EnumColor> ENUM_COLOR = new DataSerializerIO<EnumColor>() {
        @Override
        public void write(RailcraftOutputStream outputStream, EnumColor value) throws IOException {
            outputStream.writeEnum(value);
        }

        @Override
        public EnumColor read(RailcraftInputStream inputStream) throws IOException {
            return inputStream.readEnum(EnumColor.VALUES);
        }
    };

    public static void register() {
        DataSerializers.registerSerializer(OPTIONAL_FLUID_STACK);
        DataSerializers.registerSerializer(ENUM_COLOR);
    }

    public static <T> DataParameter<T> create(DataSerializer<T> serializer) {
        Class<?> clazz = sun.reflect.Reflection.getCallerClass(2);
        return EntityDataManager.createKey(clazz.asSubclass(Entity.class), serializer);
    }

    @Deprecated
    public static <T> DataParameter<T> create(Class<?> clazz, DataSerializer<T> serializer) {
        return EntityDataManager.createKey(clazz.asSubclass(Entity.class), serializer);
    }

    public static <T extends Enum<T>> void writeEnum(EntityDataManager dataManager, DataParameter<Byte> parameter, Enum<T> value) {
        dataManager.set(parameter, (byte) value.ordinal());
    }

    public static <T extends Enum<T>> T readEnum(EntityDataManager dataManager, DataParameter<Byte> parameter, T[] values) {
        return values[dataManager.get(parameter)];
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
