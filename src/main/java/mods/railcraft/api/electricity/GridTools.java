/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.electricity;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.electricity.IElectricGrid.ChargeHandler.ConnectType;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.ITrackTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class GridTools {

    public static Set<IElectricGrid> getMutuallyConnectedObjects(IElectricGrid gridObject) {
        Set<IElectricGrid> connectedObjects = new HashSet<IElectricGrid>();

        WorldCoordinate myPos = new WorldCoordinate(gridObject.getTile());
        for (Map.Entry<WorldCoordinate, EnumSet<ConnectType>> position : gridObject.getChargeHandler().getPossibleConnectionLocations().entrySet()) {
            IElectricGrid otherObj = getGridObjectAt(gridObject.getTile().getWorldObj(), position.getKey());
            if (otherObj != null && position.getValue().contains(otherObj.getChargeHandler().getType())) {
                EnumSet<ConnectType> otherType = otherObj.getChargeHandler().getPossibleConnectionLocations().get(myPos);
                if (otherType != null && otherType.contains(gridObject.getChargeHandler().getType()))
                    connectedObjects.add(otherObj);
            }
        }
        return connectedObjects;
    }

    public static IElectricGrid getGridObjectAt(IBlockAccess world, WorldCoordinate pos) {
        return getGridObjectAt(world, pos.x, pos.y, pos.z);
    }

    public static IElectricGrid getGridObjectAt(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null)
            return null;
        if (tile instanceof IElectricGrid)
            return (IElectricGrid) tile;
        if (tile instanceof ITrackTile) {
            ITrackInstance track = ((ITrackTile) tile).getTrackInstance();
            if (track instanceof IElectricGrid)
                return (IElectricGrid) track;
        }
        return null;
    }

}
