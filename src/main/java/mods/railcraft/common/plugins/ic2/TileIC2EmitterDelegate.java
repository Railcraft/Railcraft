/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.ic2;

import cpw.mods.fml.common.Optional;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergySourceInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySourceInfo", modid = "IC2-Classic-Spmod")
public class TileIC2EmitterDelegate extends TileIC2Delegate implements IEnergySource, IEnergySourceInfo {

    private final IEmitterDelegate delegate;

    public TileIC2EmitterDelegate(IEmitterDelegate delegate) {
        super(delegate.getTile());
        this.delegate = delegate;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return delegate.emitsEnergyTo(receiver, direction);
    }

    @Override
    public double getOfferedEnergy() {
        return delegate.getOfferedEnergy();
    }

    @Override
    public void drawEnergy(double amount) {
        delegate.drawEnergy(amount);
    }

    @Override
    public int getSourceTier() {
        return delegate.getSourceTier();
    }

    @Override
    public int getMaxEnergyAmount() {
        int ret;
        switch (this.getSourceTier()) {
            case 0:
                ret = 1;
                break;
            case 1:
                ret = 6;
                break;
            case 2:
                ret = 32;
                break;
            case 3:
                ret = 512;
                break;
            case 4:
                ret = 2048;
                break;
            case 5:
                ret = 8192;
                break;
            default:
                ret = 0;
        }
        return ret;
    }
}
