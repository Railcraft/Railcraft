/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.api.charge.IBatteryBlock;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.ic2.ISinkDelegate;
import mods.railcraft.common.plugins.ic2.TileIC2MultiEmitterDelegate;
import mods.railcraft.common.plugins.ic2.TileIC2SinkDelegate;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChargeFeederIC2 extends TileCharge implements ISinkDelegate {
    private TileEntity sinkDelegate;
    private boolean addedToIC2EnergyNet;

    @Override
    public IEnumMachine<?> getMachineType() {
        return FeederVariant.IC2;
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
        IBatteryBlock battery = getBattery();
        if (state.getBlock() instanceof BlockChargeFeeder && state.getValue(BlockChargeFeeder.REDSTONE)) {
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
        getBattery().addCharge(amount);
        return 0.0;
    }

    @Override
    public boolean acceptsEnergyFrom(Object emitter, EnumFacing direction) {
        return !(emitter instanceof TileIC2MultiEmitterDelegate);
    }

    public TileEntity getIC2Delegate() {
        if (sinkDelegate == null)
            try {
                sinkDelegate = new TileIC2SinkDelegate(this);
            } catch (Throwable error) {
                Game.log().api("IndustrialCraft", error);
            }
        return sinkDelegate;
    }
}
