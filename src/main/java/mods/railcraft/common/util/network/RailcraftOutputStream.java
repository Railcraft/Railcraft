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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

/**
 * Created by CovertJaguar on 5/29/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RailcraftOutputStream extends DataOutputStream {

    public RailcraftOutputStream(OutputStream os) {
        super(os);
    }

    public void writeBlockPos(BlockPos pos) throws IOException {
        writeLong(pos.toLong());
    }

    public void writeVec3d(Vec3d vec) throws IOException {
        writeDouble(vec.x);
        writeDouble(vec.y);
        writeDouble(vec.z);
    }

    public void writeUUID(@Nullable UUID uuid) throws IOException {
        if (uuid == null) {
            writeLong(0);
            writeLong(0);
        } else {
            writeLong(uuid.getMostSignificantBits());
            writeLong(uuid.getLeastSignificantBits());
        }
    }

    public void writeBitSet(BitSet bitSet) throws IOException {
        byte[] bytes = bitSet.toByteArray();
        writeByte(bytes.length);
        write(bytes);
    }

    public void writeEnum(Enum<?> value) throws IOException {
        assert value.ordinal() < Byte.MAX_VALUE;
        writeByte(value.ordinal());
    }

    public void writeNBT(@Nullable NBTTagCompound nbt) throws IOException {
        if (nbt == null) {
            writeByte(0);
        } else {
            try (DataOutputStream nbtStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(this)))) {
                CompressedStreamTools.write(nbt, nbtStream);
            }
        }
    }

    public void writeItemStack(@Nullable ItemStack stack) throws IOException {
        if (InvTools.isEmpty(stack))
            writeShort(-1);
        else {
            writeShort(Item.getIdFromItem(stack.getItem()));
            writeByte(sizeOf(stack));
            writeShort(stack.getItemDamage());
            NBTTagCompound nbt = null;

            if (stack.getItem().isDamageable() || stack.getItem().getShareTag())
                nbt = stack.getTagCompound();

            writeNBT(nbt);
        }
    }

    public void writeFluidStack(@Nullable FluidStack fluidStack) throws IOException {
        if (fluidStack == null || fluidStack.amount == 0) {
            writeInt(0);
        } else {
            writeInt(fluidStack.amount);
            writeUTF(FluidRegistry.getFluidName(fluidStack.getFluid()));
            writeNBT(fluidStack.tag);
        }
    }
}
