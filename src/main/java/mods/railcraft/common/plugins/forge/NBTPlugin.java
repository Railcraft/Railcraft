/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import com.google.common.collect.ForwardingList;
import com.mojang.authlib.GameProfile;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class NBTPlugin {
    @Nullable
    public static NBTTagCompound makeGameProfileTag(@Nullable GameProfile profile) {
        if (profile == null || (profile.getName() == null && profile.getId() == null))
            return null;
        NBTTagCompound nbt = new NBTTagCompound();
        if (profile.getName() != null)
            nbt.setString("name", profile.getName());
        if (profile.getId() != null)
            nbt.setString("id", profile.getId().toString());
        return nbt;
    }

    public static GameProfile readGameProfileTag(NBTTagCompound data) {
        String ownerName = PlayerPlugin.UNKNOWN_PLAYER_NAME;
        if (data.hasKey("name"))
            ownerName = data.getString("name");
        UUID ownerUUID = null;
        if (data.hasKey("id"))
            ownerUUID = UUID.fromString(data.getString("id"));
        return new GameProfile(ownerUUID, ownerName);
    }

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

    public static void writeBlockPos(NBTTagCompound data, String tag, BlockPos pos) {
        data.setIntArray(tag, new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }

    @Nullable
    public static BlockPos readBlockPos(NBTTagCompound data, String tag) {
        if (data.hasKey(tag)) {
            int[] c = data.getIntArray(tag);
            return new BlockPos(c[0], c[1], c[2]);
        }
        return null;
    }

    public static void writeItemStack(NBTTagCompound data, String tag, @Nullable ItemStack stack) {
        NBTTagCompound nbt = new NBTTagCompound();
        if (!InvTools.isEmpty(stack))
            stack.writeToNBT(nbt);
        data.setTag(tag, nbt);
    }

    public static ItemStack readItemStack(NBTTagCompound data, String tag) {
        if (data.hasKey(tag)) {
            NBTTagCompound nbt = data.getCompoundTag(tag);
            return new ItemStack(nbt);
        }
        return InvTools.emptyStack();
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
        private static final Map<Class<? extends NBTBase>, EnumNBTType> classToType = new HashMap<>();
        public final Class<? extends NBTBase> classObject;

        EnumNBTType(Class<? extends NBTBase> c) {
            this.classObject = c;
        }

        static {
            for (EnumNBTType each : VALUES) {
                classToType.put(each.classObject, each);
            }
        }

        public static EnumNBTType fromClass(Class<? extends NBTBase> c) {
            return classToType.get(c);
        }

    }

    public static <T extends NBTBase> List<T> getNBTList(NBTTagCompound nbt, String tag, EnumNBTType type) {
        NBTTagList nbtList = nbt.getTagList(tag, type.ordinal());
        return new NBTList<>(nbtList);
    }

    public static <T extends NBTBase> List<T> asList(NBTTagList list) {
        return new NBTList<>(list);
    }

    private static final class NBTList<T extends NBTBase> extends ForwardingList<T> {

        private final List<T> backingList;

        @SuppressWarnings("unchecked")
        NBTList(NBTTagList nbtList) {
            backingList = (List<T>) nbtList.tagList;
        }

        @Override
        protected List<T> delegate() {
            return backingList;
        }

    }
}
