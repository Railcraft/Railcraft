/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import java.util.Arrays;

/**
 * Created by CovertJaguar on 6/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DataManagerPlugin {

    public abstract static class DataSerializerIO<T> implements DataSerializer<T> {

        @Override
        public T copyValue(T value) {
            return value;
        }

        @Override
        public DataParameter<T> createKey(int id) {
            return new DataParameter<>(id, this);
        }
    }

    public static final DataSerializer<OptionalFluidStack> OPTIONAL_FLUID_STACK = new DataSerializerIO<OptionalFluidStack>() {
        @Override
        public final void write(PacketBuffer buf, OptionalFluidStack value) {
            try (ByteBufOutputStream out = new ByteBufOutputStream(buf);
                 RailcraftOutputStream data = new RailcraftOutputStream(out)) {
                data.writeFluidStack(value.orElse(null));
            } catch (IOException e) {
                Game.log().throwable("Error syncing Object", e);
                if (Game.DEVELOPMENT_VERSION)
                    throw new RuntimeException(e);
            }
        }

        @Override
        public final OptionalFluidStack read(PacketBuffer buf) {
            try (ByteBufInputStream out = new ByteBufInputStream(buf);
                 RailcraftInputStream data = new RailcraftInputStream(out)) {

                return OptionalFluidStack.of(data.readFluidStack());
            } catch (IOException e) {
                Game.log().throwable("Error syncing Object", e);
                if (Game.DEVELOPMENT_VERSION)
                    throw new RuntimeException(e);
            }
            return OptionalFluidStack.empty();
        }

        @Override
        public OptionalFluidStack copyValue(OptionalFluidStack value) {
            return value.copy();
        }
    };

    public static final DataSerializer<EnumColor> ENUM_COLOR = new DataSerializerIO<EnumColor>() {
        @Override
        public final void write(PacketBuffer buf, EnumColor value) {
            try (ByteBufOutputStream out = new ByteBufOutputStream(buf);
                 RailcraftOutputStream data = new RailcraftOutputStream(out)) {
                data.writeEnum(value);
            } catch (IOException e) {
                Game.log().throwable("Error syncing Object", e);
                if (Game.DEVELOPMENT_VERSION)
                    throw new RuntimeException(e);
            }
        }

        @Override
        public final EnumColor read(PacketBuffer buf) {
            try (ByteBufInputStream out = new ByteBufInputStream(buf);
                 RailcraftInputStream data = new RailcraftInputStream(out)) {

                return data.readEnum(EnumColor.VALUES);
            } catch (IOException e) {
                Game.log().throwable("Error syncing Object", e);
                if (Game.DEVELOPMENT_VERSION)
                    throw new RuntimeException(e);
            }
            return EnumColor.WHITE;
        }
    };

    public static final DataSerializer<byte[]> BYTE_ARRAY = new DataSerializerIO<byte[]>() {
        @Override
        public void write(PacketBuffer packetBuffer, byte[] bytes) {
            packetBuffer.writeByteArray(bytes);
        }

        @Override
        public byte[] read(PacketBuffer packetBuffer) throws IOException {
            return packetBuffer.readByteArray();
        }

        @Override
        public byte[] copyValue(byte[] value) {
            return Arrays.copyOf(value, value.length);
        }
    };

    public static void register() {
        DataSerializers.registerSerializer(OPTIONAL_FLUID_STACK);
        DataSerializers.registerSerializer(ENUM_COLOR);
        DataSerializers.registerSerializer(BYTE_ARRAY);
    }

    @SuppressWarnings("deprecation")
    public static <T> DataParameter<T> create(DataSerializer<T> serializer) {
        Class<?> clazz = sun.reflect.Reflection.getCallerClass(2);
        return EntityDataManager.createKey(clazz.asSubclass(Entity.class), serializer);
    }

    public static <T extends Enum<T>> void writeEnum(EntityDataManager dataManager, DataParameter<Byte> parameter, Enum<T> value) {
        dataManager.set(parameter, (byte) value.ordinal());
    }

    public static <T extends Enum<T>> T readEnum(EntityDataManager dataManager, DataParameter<Byte> parameter, T[] values) {
        return values[dataManager.get(parameter)];
    }
}
