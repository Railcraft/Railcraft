package mods.railcraft.common.plugins.rf;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 5/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RedstoneFluxPlugin {

    public static int pushToTile(TileEntity tile, EnumFacing side, int powerToTransfer) {
        if (canTileReceivePower(tile, side)) {
            IEnergyReceiver handler = (IEnergyReceiver) tile;
            if (powerToTransfer > 0)
                return handler.receiveEnergy(side, powerToTransfer, false);
        }
        return 0;
    }

    public static int pushToTiles(IEnergyProvider provider, AdjacentTileCache tileCache, int pushPerSide) {
        int pushed = 0;
        for (EnumFacing side : EnumFacing.VALUES) {
            TileEntity tile = tileCache.getTileOnSide(side);
            if (canTileReceivePower(tile, side.getOpposite())) {
                IEnergyReceiver handler = (IEnergyReceiver) tile;
                int amountToPush = provider.extractEnergy(side, pushPerSide, true);
                if (amountToPush > 0) {
                    int amountPushed = handler.receiveEnergy(side.getOpposite(), amountToPush, false);
                    pushed += amountPushed;
                    provider.extractEnergy(side, amountPushed, false);
                }
            }
        }
        return pushed;
    }

    public static boolean canTileReceivePower(@Nullable TileEntity tile, EnumFacing side) {
        if (tile instanceof IEnergyReceiver) {
            IEnergyReceiver handler = (IEnergyReceiver) tile;
            return handler.canConnectEnergy(side);
        }
        return false;
    }
}
