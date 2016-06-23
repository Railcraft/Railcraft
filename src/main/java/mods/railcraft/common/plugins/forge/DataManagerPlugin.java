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

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by CovertJaguar on 6/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DataManagerPlugin {

    public static final DataSerializer<Optional<FluidStack>> OPTIONAL_FLUID_STACK = new DataSerializer<java.util.Optional<FluidStack>>() {
        @Override
        public void write(PacketBuffer buf, Optional<FluidStack> value) {
            try (ByteBufOutputStream out = new ByteBufOutputStream(buf);
                 RailcraftOutputStream data = new RailcraftOutputStream(out)) {
                data.writeFluidStack(value.orElse(null));
            } catch (IOException e) {
                Game.logThrowable("Error syncing FluidStack", e);
                if (Game.IS_DEBUG)
                    throw new RuntimeException(e);
            }
        }

        @Override
        public Optional<FluidStack> read(PacketBuffer buf) throws IOException {
            try (ByteBufInputStream out = new ByteBufInputStream(buf);
                 RailcraftInputStream data = new RailcraftInputStream(out)) {
                return Optional.ofNullable(data.readFluidStack());
            } catch (IOException e) {
                Game.logThrowable("Error syncing FluidStack", e);
                if (Game.IS_DEBUG)
                    throw new RuntimeException(e);
            }
            return Optional.empty();
        }

        @Override
        public DataParameter<Optional<FluidStack>> createKey(int id) {
            return new DataParameter<Optional<FluidStack>>(id, this);
        }
    };

    static {
        DataSerializers.registerSerializer(OPTIONAL_FLUID_STACK);
    }

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
