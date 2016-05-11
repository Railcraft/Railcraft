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
