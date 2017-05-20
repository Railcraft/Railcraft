/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.ICartBattery;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * Created by CovertJaguar on 10/4/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CapabilityCartBattery {
    @CapabilityInject(ICartBattery.class)
    public static Capability<ICartBattery> CHARGE_CART_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(ICartBattery.class, new Capability.IStorage<ICartBattery>() {
            @Override
            public NBTBase writeNBT(Capability<ICartBattery> capability, ICartBattery instance, EnumFacing side) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setDouble("charge", instance.getCharge());
                return nbt;
            }

            @Override
            public void readNBT(Capability<ICartBattery> capability, ICartBattery instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound tags = (NBTTagCompound) nbt;
                instance.setCharge(tags.getDouble("charge"));
            }
        }, CartBattery::new);
    }
}
