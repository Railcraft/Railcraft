/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.ic2;

import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileIC2SinkDelegate extends TileIC2Delegate implements IEnergySink {

    private final ISinkDelegate delegate;

    public TileIC2SinkDelegate(ISinkDelegate delegate) {
        super(delegate.getTile());
        this.delegate = delegate;
    }

    @Override
    public double getDemandedEnergy() {
        return delegate.getDemandedEnergy();
    }

    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        return delegate.injectEnergy(directionFrom, amount);
    }

    @Override
    public int getSinkTier() {
        return delegate.getSinkTier();
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return delegate.acceptsEnergyFrom(emitter, direction);
    }

}
