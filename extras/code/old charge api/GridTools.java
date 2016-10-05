/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.electricity;

import com.google.common.base.Optional;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.charge.IElectricGrid.ChargeHandler.ConnectType;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.ITrackTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class GridTools {

    public static Set<IElectricGrid> getMutuallyConnectedObjects(IElectricGrid gridObject) {
        Set<IElectricGrid> connectedObjects = new HashSet<IElectricGrid>();

        WorldCoordinate myPos = new WorldCoordinate(gridObject.getTile());
        for (Map.Entry<BlockPos, EnumSet<ConnectType>> position : gridObject.getChargeHandler().getPossibleConnectionLocations().entrySet()) {
            Optional<IElectricGrid> otherObj = getGridObjectAt(gridObject.getTile().getWorld(), position.getKey());
            if (otherObj.isPresent() && position.getValue().contains(otherObj.get().getChargeHandler().getType())) {
                EnumSet<ConnectType> otherType = otherObj.get().getChargeHandler().getPossibleConnectionLocations().get(myPos);
                if (otherType != null && otherType.contains(gridObject.getChargeHandler().getType()))
                    connectedObjects.add(otherObj.get());
            }
        }
        return connectedObjects;
    }

    public static Optional<IElectricGrid> getGridObjectAt(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        IElectricGrid gridObject = null;
        if (tile instanceof IElectricGrid)
            gridObject = (IElectricGrid) tile;
        if (tile instanceof ITrackTile) {
            ITrackInstance track = ((ITrackTile) tile).getTrackInstance();
            if (track instanceof IElectricGrid)
                gridObject = (IElectricGrid) track;
        }
        return Optional.fromNullable(gridObject);
    }

}
