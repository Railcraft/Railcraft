/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DataTools {

    public static void writeItemStack(ItemStack stack, DataOutputStream dataStream) throws IOException {
        if (stack == null)
            dataStream.writeShort(-1);
        else {
            dataStream.writeShort(Item.getIdFromItem(stack.getItem()));
            dataStream.writeByte(stack.stackSize);
            dataStream.writeShort(stack.getItemDamage());
            NBTTagCompound nbt = null;

            if (stack.getItem().isDamageable() || stack.getItem().getShareTag())
                nbt = stack.getTagCompound();

            writeNBT(nbt, dataStream);
        }
    }

    public static ItemStack readItemStack(DataInputStream dataStream) throws IOException {
        ItemStack stack = null;
        short id = dataStream.readShort();

        if (id >= 0) {
            byte stackSize = dataStream.readByte();
            short damage = dataStream.readShort();
            stack = new ItemStack(Item.getItemById(id), stackSize, damage);
            stack.setTagCompound(readNBT(dataStream));
        }

        return stack;
    }

    public static void writeNBT(NBTTagCompound nbt, DataOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(nbt != null);
        if (nbt != null)
            CompressedStreamTools.writeCompressed(nbt, dataStream);
    }

    public static NBTTagCompound readNBT(DataInputStream dataStream) throws IOException {
        if (dataStream.readBoolean())
            return CompressedStreamTools.read(dataStream);
        return null;
    }

    public static void byteArray2BitSet(BitSet bits, byte[] bytes) {
        bits.clear();
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0)
                bits.set(i);
        }
    }

    public static void bitSet2ByteArray(BitSet bits, byte[] bytes) {
        Arrays.fill(bytes, (byte) 0);
        for (int i = 0; i < bits.length(); i++) {
            if (bits.get(i))
                bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
        }
    }

}
