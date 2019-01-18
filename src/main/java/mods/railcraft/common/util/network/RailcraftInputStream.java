/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.network;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

/**
 * Created by CovertJaguar on 5/29/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RailcraftInputStream extends DataInputStream {
    public RailcraftInputStream(InputStream is) {
        super(is);
    }

    public BlockPos readBlockPos() throws IOException {
        return BlockPos.fromLong(readLong());
    }

    public Vec3d readVec3d() throws IOException {
        double x = readDouble();
        double y = readDouble();
        double z = readDouble();
        return new Vec3d(x, y, z);
    }

    public UUID readUUID() throws IOException {
        return new UUID(readLong(), readLong());
    }

    public BitSet readBitSet() throws IOException {
        int length = readByte();
        byte[] bytes = new byte[length];
        readFully(bytes);
        return BitSet.valueOf(bytes);
    }

    public <T extends Enum<T>> T readEnum(T[] enumConstants) throws IOException {
        return enumConstants[readByte()];
    }

    public @Nullable NBTTagCompound readNBT() throws IOException {
        mark(1);
        byte b = readByte();

        NBTTagCompound nbt = null;
        if (b != 0) {
            reset();
            try (DataInputStream nbtStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(this)))) {
                nbt = CompressedStreamTools.read(nbtStream, new NBTSizeTracker(2097152L));
            }
        }
        return nbt;
    }

    public ItemStack readItemStack() throws IOException {
        ItemStack stack = InvTools.emptyStack();
        short id = readShort();

        if (id >= 0) {
            byte stackSize = readByte();
            short damage = readShort();
            stack = new ItemStack(Item.getItemById(id), stackSize, damage);
            stack.setTagCompound(readNBT());
        }

        return stack;
    }

    @SuppressWarnings("ConstantConditions")
    public @Nullable FluidStack readFluidStack() throws IOException {
        int amount = readInt();
        if (amount > 0) {
            try {
                String fluidName = readUTF();
                NBTTagCompound nbt = readNBT();
                Fluid fluid = FluidRegistry.getFluid(fluidName);
                if (fluid == null)
                    throw new IOException("Non-existent fluid was read from the stream, this shouldn't happen normally.");
                FluidStack stack = new FluidStack(fluid, amount);
                stack.tag = nbt;
                return stack;
            } catch (IllegalArgumentException ex) {
                throw new IOException(ex);
            }
        }
        return null;
    }
}
