/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.ic2;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.IMetaDelegate;
import mods.railcraft.common.util.misc.Code;
import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileIC2MultiEmitterDelegate extends TileIC2EmitterDelegate implements IMetaDelegate {
    private final List<IEnergyTile> subTiles;

    public TileIC2MultiEmitterDelegate(IMultiEmitterDelegate delegate) {
        super(delegate);
        subTiles = Code.cast(delegate.getSubTiles());
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
        return true;
    }

    @Override
    public List<IEnergyTile> getSubTiles() {
        return subTiles;
    }

}
