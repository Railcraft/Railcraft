/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import net.minecraft.nbt.*;

public class SafeNBTWrapper {

    private final NBTTagCompound data;

    public SafeNBTWrapper(NBTTagCompound data) {
        this.data = data;
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was
     * stored.
     */
    public byte getByte(String key) {
        if (data.hasKey(key)) {
            NBTBase tag = data.getTag(key);
            if (tag instanceof NBTTagByte)
                return ((NBTTagByte) tag).getByte();
            else if (tag instanceof NBTTagShort)
                return ((NBTTagShort) tag).getByte();
            else if (tag instanceof NBTTagInt)
                return ((NBTTagInt) tag).getByte();
        }
        return 0;
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was
     * stored.
     */
    public short getShort(String key) {
        if (data.hasKey(key)) {
            NBTBase tag = data.getTag(key);
            if (tag instanceof NBTTagShort)
                return ((NBTTagShort) tag).getShort();
            else if (tag instanceof NBTTagInt)
                return ((NBTTagInt) tag).getShort();
            else if (tag instanceof NBTTagByte)
                return ((NBTTagByte) tag).getShort();
        }
        return 0;
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key
     * was stored.
     */
    public int getInteger(String key) {
        if (data.hasKey(key)) {
            NBTBase tag = data.getTag(key);
            if (tag instanceof NBTTagInt)
                return ((NBTTagInt) tag).getInt();
            else if (tag instanceof NBTTagShort)
                return ((NBTTagShort) tag).getInt();
            else if (tag instanceof NBTTagByte)
                return ((NBTTagByte) tag).getInt();
        }
        return 0;
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was
     * stored.
     */
    public float getFloat(String key) {
        if (data.hasKey(key)) {
            NBTBase tag = data.getTag(key);
            if (tag instanceof NBTTagFloat)
                return ((NBTTagFloat) tag).getFloat();
            else if (tag instanceof NBTTagDouble)
                return ((NBTTagDouble) tag).getFloat();
        }
        return 0;
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was
     * stored.
     */
    public double getDouble(String key) {
        if (data.hasKey(key)) {
            NBTBase tag = data.getTag(key);
            if (tag instanceof NBTTagFloat)
                return ((NBTTagFloat) tag).getDouble();
            else if (tag instanceof NBTTagDouble)
                return ((NBTTagDouble) tag).getDouble();
            else if (tag instanceof NBTTagInt)
                return ((NBTTagInt) tag).getDouble();
            else if (tag instanceof NBTTagShort)
                return ((NBTTagShort) tag).getDouble();
            else if (tag instanceof NBTTagByte)
                return ((NBTTagByte) tag).getDouble();
        }
        return 0;
    }

}
