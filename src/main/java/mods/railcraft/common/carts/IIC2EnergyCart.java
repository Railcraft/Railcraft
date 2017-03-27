/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.IWorldNameable;

/**
 * EnergyCart
 *
 * Created by CovertJaguar on 5/3/2016.
 */
public interface IIC2EnergyCart extends IInventory, IWorldNameable {
    int getCapacity();

    double getEnergy();

    void setEnergy(double energy);

    int getEnergyBarScaled(int scale);

    EntityMinecart getEntity();
}
