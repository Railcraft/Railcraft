/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.ic2;

import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileIC2Delegate extends TileEntity implements IEnergyTile {

    private final TileEntity delegate;

    public TileIC2Delegate(TileEntity delegate) {
        this.delegate = delegate;
        setPos(delegate.getPos());
        this.world = delegate.getWorld();
    }

    @Override
    public boolean isInvalid() {
        return delegate.isInvalid();
    }

}
