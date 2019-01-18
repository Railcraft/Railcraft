/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.gui.widgets.FEEnergyIndicator;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static net.minecraftforge.energy.CapabilityEnergy.ENERGY;

public abstract class TileRFManipulator extends TileManipulatorCart {
    protected static final int TRANSFER_RATE = 8000;
    protected static final int TRANSFER_FADE = 20;
    private static final int RF_CAP = 4000000;
    protected final EnergyStorage energyStorage = new EnergyStorage(RF_CAP);
    public final FEEnergyIndicator rfIndicator = new FEEnergyIndicator(energyStorage);

    protected TileRFManipulator() {
        setInventorySize(0);
    }

//    @Override
//    public IIcon getIcon(int side) {
//        if (side == direction.ordinal())
//            return getMachineType().getTexture(isProcessing() ? 7 : 8);
//        return getMachineType().getTexture(isProcessing() ? 0 : 6);
//    }

    @Override
    public boolean isManualMode() {
        return false;
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        return !isSendCartGateAction() && cart instanceof EntityCartRF;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTBase nbt = ENERGY.writeNBT(energyStorage, null);
        data.setTag("energy", nbt);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        NBTBase nbt = data.getTag("energy");
        if (nbt != null)
            ENERGY.readNBT(energyStorage, null, nbt);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(isProcessing());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        boolean transfer = data.readBoolean();
        if (isProcessing() != transfer) {
            setProcessing(transfer);
            markBlockForUpdate();
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == ENERGY ? ENERGY.cast(energyStorage) : super.getCapability(capability, facing);
    }
}
