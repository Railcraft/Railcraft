package mods.railcraft.common.plugins.ic2;

import cpw.mods.fml.common.Optional;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergySourceInfo;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by CovertJaguar on 10/6/2015.
 */
@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySourceInfo", modid = "IC2-Classic-Spmod")
public abstract class TileIC2SourceDelegate extends TileIC2Delegate implements IEnergySource, IEnergySourceInfo {

    public TileIC2SourceDelegate(TileEntity delegate) {
        super(delegate);
    }

    @Override
    public int getMaxEnergyAmount() {
        return IC2Plugin.POWER_TIERS[getSourceTier()];
    }
}
