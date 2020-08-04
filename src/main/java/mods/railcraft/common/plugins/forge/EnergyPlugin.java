/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.util.misc.Capabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

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

    public static int chargeToForgeEnergy(double charge) {
        return MathHelper.floor(charge / RailcraftConstants.FE_EU_RATIO);
    }

    public static double forgeEnergyToCharge(int fe) {
        return fe * RailcraftConstants.FE_EU_RATIO;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static int pushToTiles(TileRailcraft tile, IEnergyStorage energyStorage, int pushPerSide, EnumFacing[] targetSides) {
        return Arrays.stream(targetSides)
                .mapToInt(side -> pushToSide(tile, energyStorage, pushPerSide, side)).sum();
    }

    private static int pushToSide(TileRailcraft tile, IEnergyStorage energyStorage, int pushPerSide, EnumFacing side) {
        return tile.getTileCache().onSide(side)
                .flatMap(target -> Capabilities.get(target, ENERGY, side.getOpposite()))
                .filter(IEnergyStorage::canReceive)
                .map(receiver -> {
                    int amountToPush = energyStorage.extractEnergy(pushPerSide, true);
                    if (amountToPush > 0) {
                        int amountPushed = receiver.receiveEnergy(amountToPush, false);
                        energyStorage.extractEnergy(amountPushed, false);
                        return amountPushed;
                    }
                    return 0;
                }).orElse(0);
    }

    public static boolean canTileReceivePower(@Nullable TileEntity tile, EnumFacing side) {
        return Capabilities.get(tile, ENERGY, side).map(IEnergyStorage::canReceive).orElse(false);
    }
}