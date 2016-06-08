/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import com.google.common.collect.ForwardingList;
import net.minecraft.nbt.*;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class NBTPlugin {

    public static <T extends Enum<T>> void writeEnumOrdinal(NBTTagCompound data, String tag, Enum<T> e) {
        assert e.ordinal() < Byte.MAX_VALUE;
        data.setByte(tag, (byte) e.ordinal());
    }

    public static <T extends Enum<T>> T readEnumOrdinal(NBTTagCompound data, String tag, T[] enumConstants, T defaultValue) {
        if (data.hasKey(tag)) {
            byte ordinal = data.getByte(tag);
            return enumConstants[ordinal];
        }
        return defaultValue;
    }

    public static <T extends Enum<T>> void writeEnumName(NBTTagCompound data, String tag, Enum<T> e) {
        data.setString(tag, e.name());
    }

    public static <T extends Enum<T>> T readEnumName(NBTTagCompound data, String tag, T defaultValue) {
        if (data.hasKey(tag)) {
            String name = data.getString(tag);
            return Enum.valueOf(defaultValue.getClass().asSubclass(Enum.class), name);
        }
        return defaultValue;
    }

    public static void writeUUID(NBTTagCompound data, String tag, @Nullable UUID uuid) {
        if (uuid == null)
            return;
        NBTTagCompound nbtTag = new NBTTagCompound();
        nbtTag.setLong("most", uuid.getMostSignificantBits());
        nbtTag.setLong("least", uuid.getLeastSignificantBits());
        data.setTag(tag, nbtTag);
    }

    @Nullable
    public static UUID readUUID(NBTTagCompound data, String tag) {
        if (data.hasKey(tag)) {
            NBTTagCompound nbtTag = data.getCompoundTag(tag);
            return new UUID(nbtTag.getLong("most"), nbtTag.getLong("least"));
        }
        return null;
    }

    public enum EnumNBTType {

        END(NBTTagEnd.class),
        BYTE(NBTTagByte.class),
        SHORT(NBTTagShort.class),
        INT(NBTTagInt.class),
        LONG(NBTTagLong.class),
        FLOAT(NBTTagFloat.class),
        DOUBLE(NBTTagDouble.class),
        BYTE_ARRAY(NBTTagByteArray.class),
        STRING(NBTTagString.class),
        LIST(NBTTagList.class),
        COMPOUND(NBTTagCompound.class),
        INT_ARRAY(NBTTagIntArray.class);
        public static final EnumNBTType[] VALUES = values();
        public final Class<? extends NBTBase> classObject;

        EnumNBTType(Class<? extends NBTBase> c) {
            this.classObject = c;
        }

        public static EnumNBTType fromClass(Class<? extends NBTBase> c) {
            for (EnumNBTType type : VALUES) {
                if (type.classObject == c)
                    return type;
            }
            return null;
        }

    }

    public static <T extends NBTBase> NBTList<T> getNBTList(NBTTagCompound nbt, String tag, EnumNBTType type) {
        NBTTagList nbtList = nbt.getTagList(tag, type.ordinal());
        return new NBTList<T>(nbtList);
    }

    public static class NBTList<T extends NBTBase> extends ForwardingList<T> {

        private final ArrayList<T> backingList;

        public NBTList(NBTTagList nbtList) {
            backingList = ObfuscationReflectionHelper.getPrivateValue(NBTTagList.class, nbtList, 0);
        }

        @Override
        protected List<T> delegate() {
            return backingList;
        }

    }
}
