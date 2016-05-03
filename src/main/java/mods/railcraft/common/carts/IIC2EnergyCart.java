package mods.railcraft.common.carts;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;

/**
 * Created by CovertJaguar on 5/3/2016.
 */
public interface IIC2EnergyCart extends IInventory {
    int getCapacity();

    double getEnergy();

    void setEnergy(double energy);

    int getEnergyBarScaled(int scale);

    String getName();

    EntityMinecart getEntity();
}
