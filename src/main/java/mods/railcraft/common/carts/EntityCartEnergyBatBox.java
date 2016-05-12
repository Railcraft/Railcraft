/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class EntityCartEnergyBatBox extends EntityCartEnergy {

    public EntityCartEnergyBatBox(World world) {
        super(world);
    }

    public EntityCartEnergyBatBox(World world, double d, double d1, double d2) {
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
        return EnumCart.ENERGY_BATBOX;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public int getCapacity() {
        return 40000;
    }

    @Override
    public int getTransferLimit() {
        return 32;
    }

    @Override
    public String getInventoryName() {
        return LocalizationPlugin.translate(EnumCart.ENERGY_BATBOX.getTag());
    }

    @Override
    public ItemStack getIC2Item() {
        return IC2Plugin.getItem("batBox");
    }

}
