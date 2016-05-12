package mods.railcraft.common.carts;

import ic2.api.item.IC2Items;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public final class EntityCartEnergyMFSU extends EntityCartEnergy {

    public EntityCartEnergyMFSU(World world) {
        super(world);
    }

    public EntityCartEnergyMFSU(World world, double d, double d1, double d2) {
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
    public int getTier() {
        return 3;
    }

    @Override
    public String getInventoryName() {
        return LocalizationPlugin.translate(EnumCart.ENERGY_MFSU.getTag());
    }

    @Override
    public ItemStack getIC2Item() {
        return IC2Items.getItem("mfsUnit");
    }

    @Override
    public int getCapacity() {
        return 10000000;
    }

    @Override
    public int getTransferLimit() {
        return 512;
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.ENERGY_MFSU;
    }

}
