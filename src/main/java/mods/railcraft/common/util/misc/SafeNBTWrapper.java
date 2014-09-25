/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagShort;

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
                return ((NBTTagByte) tag).func_150290_f();
            else if (tag instanceof NBTTagShort)
                return ((NBTTagShort) tag).func_150290_f();
            else if (tag instanceof NBTTagInt)
                return ((NBTTagInt) tag).func_150290_f();
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
                return ((NBTTagShort) tag).func_150289_e();
            else if (tag instanceof NBTTagInt)
                return ((NBTTagInt) tag).func_150289_e();
            else if (tag instanceof NBTTagByte)
                return ((NBTTagByte) tag).func_150289_e();
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
                return ((NBTTagInt) tag).func_150287_d();
            else if (tag instanceof NBTTagShort)
                return ((NBTTagShort) tag).func_150287_d();
            else if (tag instanceof NBTTagByte)
                return ((NBTTagByte) tag).func_150287_d();
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
                return ((NBTTagFloat) tag).func_150288_h();
            else if (tag instanceof NBTTagDouble)
                return ((NBTTagDouble) tag).func_150288_h();
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
                return ((NBTTagFloat) tag).func_150286_g();
            else if (tag instanceof NBTTagDouble)
                return ((NBTTagDouble) tag).func_150286_g();
            else if (tag instanceof NBTTagInt)
                return ((NBTTagInt) tag).func_150287_d();
            else if (tag instanceof NBTTagShort)
                return ((NBTTagShort) tag).func_150287_d();
            else if (tag instanceof NBTTagByte)
                return ((NBTTagByte) tag).func_150287_d();
        }
        return 0;
    }

}
