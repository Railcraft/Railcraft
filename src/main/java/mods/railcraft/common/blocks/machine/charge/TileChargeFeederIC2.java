/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.common.blocks.charge.ChargeBattery;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.ic2.ISinkDelegate;
import mods.railcraft.common.plugins.ic2.TileIC2MultiEmitterDelegate;
import mods.railcraft.common.plugins.ic2.TileIC2SinkDelegate;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChargeFeederIC2 extends TileCharge implements ISinkDelegate {
    private TileEntity sinkDelegate;
    private boolean addedToIC2EnergyNet;
    private final ChargeBattery battery = new ChargeBattery(1024.0, 512.0, 0.65);

    @Override
    public IEnumMachine<?> getMachineType() {
        return FeederVariant.IC2;
    }

    @Override
    public ChargeBattery getBattery() {
        return battery;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isHost(getWorld()) && !addedToIC2EnergyNet) {
            addedToIC2EnergyNet = IC2Plugin.addTileToNet(getIC2Delegate());
        }
    }

    private void dropFromNet() {
        if (addedToIC2EnergyNet)
            IC2Plugin.removeTileFromNet(getIC2Delegate());
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        dropFromNet();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        dropFromNet();
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public double getDemandedEnergy() {
        IBlockState state = getBlockState();
        if (state.getBlock() instanceof BlockChargeFeeder && state.getValue(BlockChargeFeeder.REDSTONE) && battery.isInitialized()) {
            double chargeDifference = battery.getCapacity() - battery.getCharge();
            return chargeDifference > 0.0 ? chargeDifference : 0.0;
        }
        return 0.0;
    }

    @Override
    public int getSinkTier() {
        return 3;
    }

    @Override
    public double injectEnergy(EnumFacing directionFrom, double amount) {
        return battery.addCharge(amount);
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, EnumFacing direction) {
        return !(emitter instanceof TileIC2MultiEmitterDelegate);
    }

    public TileEntity getIC2Delegate() {
        if (sinkDelegate == null)
            try {
                sinkDelegate = new TileIC2SinkDelegate(this);
            } catch (Throwable error) {
                Game.logErrorAPI("IndustrialCraft", error);
            }
        return sinkDelegate;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        battery.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        battery.readFromNBT(data);
    }
}
