/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.charge;

import mods.railcraft.api.charge.IBatteryCart;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * Created by CovertJaguar on 10/4/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CapabilityCartBatterySetup {
    public static void register() {
        CapabilityManager.INSTANCE.register(IBatteryCart.class, new Capability.IStorage<IBatteryCart>() {
            @Override
            public NBTBase writeNBT(Capability<IBatteryCart> capability, IBatteryCart instance, EnumFacing side) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setDouble("charge", instance.getCharge());
                return nbt;
            }

            @Override
            public void readNBT(Capability<IBatteryCart> capability, IBatteryCart instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound tags = (NBTTagCompound) nbt;
                instance.setCharge(tags.getDouble("charge"));
            }
        }, CartBattery::new);
    }
}
