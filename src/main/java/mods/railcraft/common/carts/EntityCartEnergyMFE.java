/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartEnergyMFE extends EntityCartEnergy {

    public EntityCartEnergyMFE(World world) {
        super(world);
    }

    public EntityCartEnergyMFE(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.ENERGY_MFE;
    }

    @Override
    public int getTier() {
        return 3;
    }

    @Override
    public int getCapacity() {
        return 4000000;
    }

    @Override
    public int getTransferLimit() {
        return 512;
    }

    @Override
    public String getInventoryName() {
        return LocalizationPlugin.translate(EnumCart.ENERGY_MFE.getTag());
    }

    @Override
    public ItemStack getIC2Item() {
        return IC2Plugin.getItem("mfeUnit");
    }
}
