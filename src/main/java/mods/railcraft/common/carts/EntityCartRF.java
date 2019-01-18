/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;

public final class EntityCartRF extends CartBase implements IWeightedCart {
    private static final DataParameter<Integer> FE = DataManagerPlugin.create(DataSerializers.VARINT);
    private static final int CAPACITY = 2000000;
    private final CartStorage storage = new CartStorage();

    public EntityCartRF(World world) {
        super(world);
    }

    public EntityCartRF(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.REDSTONE_FLUX;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FE, 0);
    }

    public IEnergyStorage getEnergyStorage() {
        return storage;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isClient(world))
            return;

        if (storage.getEnergyStored() > CAPACITY)
            storage.setEnergyStored(CAPACITY);
    }

    @Override
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        if (Game.isHost(world))
            GuiHandler.openGui(EnumGui.CART_FE, player, world, this);
        return true;
    }

    @Override
    public final float softMaxSpeed() {
        return 0.08F;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        storage.setEnergyStored(nbt.getInteger("fe"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("fe", storage.getEnergyStored());
    }

    private class CartStorage implements IEnergyStorage {

        @Override
        public int getEnergyStored() {
            return dataManager.get(FE);
        }

        public void setEnergyStored(int amount) {
            dataManager.set(FE, amount);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!canReceive())
                return 0;

            int energyStored = getEnergyStored();
            int energyReceived = Math.min(CAPACITY - energyStored, maxReceive);
            if (!simulate)
                setEnergyStored(energyStored + energyReceived);
            return energyReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!canExtract())
                return 0;

            int energyStored = getEnergyStored();
            int energyExtracted = Math.min(energyStored, maxExtract);
            if (!simulate)
                setEnergyStored(energyStored - energyExtracted);
            return energyExtracted;
        }

        @Override
        public int getMaxEnergyStored() {
            return CAPACITY;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }
}
