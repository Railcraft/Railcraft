/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.util.misc.AdjacentTileCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static net.minecraftforge.energy.CapabilityEnergy.ENERGY;

/**
 * Created by CovertJaguar on 5/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EnergyPlugin {

    public static final IEnergyStorage DUMMY_STORAGE = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return 0;
        }

        @Override
        public int getMaxEnergyStored() {
            return 0;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    @SuppressWarnings("UnusedReturnValue")
    public static int pushToTile(TileEntity tile, EnumFacing side, int powerToTransfer) {
        if (canTileReceivePower(tile, side)) {
            IEnergyStorage storage = tile.getCapability(ENERGY, side);
            if (storage != null && powerToTransfer > 0)
                return storage.receiveEnergy(powerToTransfer, false);
        }
        return 0;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static int pushToTiles(TileEntity te, AdjacentTileCache tileCache, int pushPerSide) {
        int pushed = 0;
        for (EnumFacing side : EnumFacing.VALUES) {
            if (!te.hasCapability(ENERGY, side))
                continue;
            IEnergyStorage source = te.getCapability(ENERGY, side);
            TileEntity tile = tileCache.getTileOnSide(side);
            if (source != null && canTileReceivePower(tile, side.getOpposite())) {
                IEnergyStorage receiver = tile.getCapability(ENERGY, side.getOpposite());
                if (receiver != null) {
                    int amountToPush = source.extractEnergy(pushPerSide, false);
                    if (amountToPush > 0) {
                        int amountPushed = receiver.receiveEnergy(amountToPush, false);
                        pushed += amountPushed;
                        source.extractEnergy(amountPushed, false);
                    }
                }

            }
        }
        return pushed;
    }

    public static boolean canTileReceivePower(@Nullable TileEntity tile, EnumFacing side) {
        if (tile != null && tile.hasCapability(ENERGY, side)) {
            //noinspection ConstantConditions
            return tile.getCapability(ENERGY, side).canReceive();
        }
        return false;
    }
}