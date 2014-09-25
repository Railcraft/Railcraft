/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TrackLockingBase extends TrackBaseRailcraft {

    @Override
    public boolean canUpdate() {
        return true;
    }

    public void migrateTrack(TrackNextGenLocking.LockingProfileType type) {
        Game.log(Level.WARN, "Migrating Legacy Track Type to new implementation: <{0}, {1}, {2}> {3} -> {4}", getX(), getY(), getZ(), getTrackSpec().getTrackTag(), EnumTrack.LOCKING.getTag());
        ITrackInstance track = EnumTrack.LOCKING.getTrackSpec().createInstanceFromSpec();
        TrackNextGenLocking trackLocking = (TrackNextGenLocking) track;
        trackLocking.setProfile(type);
        TileEntity te = TrackFactory.makeTrackTile(track);
        getWorld().setTileEntity(getX(), getY(), getZ(), te);
        getWorld().markBlockForUpdate(getX(), getY(), getZ());
    }

}
