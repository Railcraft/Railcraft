/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.forge;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.fluids.OptionalFluidStack;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.util.misc.Code;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.Game.Logger;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.DataSerializerEntry;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by CovertJaguar on 6/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DataManagerPlugin {

    public abstract static class DataSerializerIO<T> implements DataSerializer<T> {

        private final String name;

        protected DataSerializerIO(String name) {
            this.name = name;
        }

        public ResourceLocation getResourceName() {
            return new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, name);
        }

        @Override
        public T copyValue(T value) {
            return value;
        }

        @Override
        public DataParameter<T> createKey(int id) {
            return new DataParameter<>(id, this);
        }
    }

    public static final DataSerializerIO<OptionalFluidStack> OPTIONAL_FLUID_STACK = new DataSerializerIO<OptionalFluidStack>("optional.fluid.stack") {
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

    public static final DataSerializerIO<EnumColor> ENUM_COLOR = new DataSerializerIO<EnumColor>("enum.color") {
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

    public static final DataSerializerIO<byte[]> BYTE_ARRAY = new DataSerializerIO<byte[]>("byte.array") {
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
        register(OPTIONAL_FLUID_STACK);
        register(ENUM_COLOR);
        register(BYTE_ARRAY);
    }

    private static void register(DataSerializerIO<?> dataSerializer) {
        ForgeRegistries.DATA_SERIALIZERS.register(new DataSerializerEntry(dataSerializer).setRegistryName(dataSerializer.getResourceName()));
    }

    public static <T> DataParameter<T> create(DataSerializer<T> serializer) {
        DataParameter<T> dataParameter = EntityDataManager.createKey(Code.getCallerClass(1), serializer);
        Logger.INSTANCE.msg(Level.WARN, "This is NOT an error. It's just Forge being nosy.");
        return dataParameter;
    }

    public static <T extends Enum<T>> void writeEnum(EntityDataManager dataManager, DataParameter<Byte> parameter, Enum<T> value) {
        dataManager.set(parameter, (byte) value.ordinal());
    }

    public static <T extends Enum<T>> T readEnum(EntityDataManager dataManager, DataParameter<Byte> parameter, T[] values) {
        return values[dataManager.get(parameter)];
    }
}
