/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.AbstractPair;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class SignalBlock extends AbstractPair {
    Map<WorldCoordinate, WorldCoordinate> trackCache = new HashMap<WorldCoordinate, WorldCoordinate>();
    Map<WorldCoordinate, TrackTools.TrackScan> trackScans = new HashMap<WorldCoordinate, TrackTools.TrackScan>();
    Set<WorldCoordinate> waitingForRetest = new HashSet<WorldCoordinate>();
    private WorldCoordinate trackLocation;
    private int update = rand.nextInt();

    public SignalBlock(RailcraftTileEntity tile, int numPairs) {
        super(tile.getName(), tile, numPairs);
    }

    public SignalBlock getSignalAt(WorldCoordinate coord) {
        TileEntity recv = getPairAt(coord);
        if (recv != null)
            return ((ISignalBlockTile) recv).getSignalBlock();
        return null;
    }

    public abstract SignalAspect getSignalAspect();

    private void printDebug(String msg, Object... args) {
        if (RailcraftConfig.printSignalDebug())
            Game.log(Level.DEBUG, msg, args);
    }

    public void clearSignalBlockPairing(WorldCoordinate other, String reason, Object... args) {
        printDebug(reason, args);
        if (other == null)
            clearPairings();
        else
            clearPairing(other);
    }

    @Override
    public boolean isValidPair(TileEntity tile) {
        boolean isValid = false;
        if (tile instanceof ISignalBlockTile) {
            SignalBlock block = ((ISignalBlockTile) tile).getSignalBlock();
            isValid = block.isPairedWith(getCoords());
        }
        if (!isValid)
            if (tile != null)
                printDebug("Signal Block dropped because pair was no longer paired or was not a valid Signal [{0}, {1}, {2}]", tile.xCoord, tile.yCoord, tile.zCoord);
            else
                printDebug("Signal Block dropped because pair was no longer paired or was not a valid Signal [null]");
        return isValid;
    }

    //    @Override
//    public void startPairing() {
//        clearSignalBlockPairing("Signal Block pairing cleared in preparation to start a new pairing.  [{0}, {1}, {2}]", tile.xCoord, tile.yCoord, tile.zCoord);
//        super.startPairing();
//    }
    public boolean createSignalBlock(SignalBlock other) {
        locateTrack();
        other.locateTrack();
        WorldCoordinate myTrack = getTrackLocation();
        WorldCoordinate otherTrack = other.getTrackLocation();
        if (myTrack == null || otherTrack == null)
            return false;
        TrackTools.TrackScan scan = TrackTools.scanStraightTrackSection(tile.getWorldObj(), myTrack.x, myTrack.y, myTrack.z, otherTrack.x, otherTrack.y, otherTrack.z);
        if (!scan.areConnected)
            return false;
        addPairing(other.getCoords());
        other.addPairing(getCoords());
        endPairing();
        other.endPairing();
        trackScans.put(otherTrack, scan);
        return true;
    }

    protected abstract void updateSignalAspect();

    protected abstract SignalAspect getSignalAspectForPair(WorldCoordinate otherCoord);

    protected SignalAspect determineAspect(WorldCoordinate otherCoord) {
        if (isWaitingForRetest() || isBeingPaired())
            return SignalAspect.BLINK_YELLOW;
        if (!isPaired())
            return SignalAspect.BLINK_RED;
        SignalAspect otherAspect = SignalAspect.GREEN;
        SignalBlock other = getSignalAt(otherCoord);
        if (other != null)
            otherAspect = other.getSignalAspectForPair(getCoords());
        SignalAspect myAspect = determineMyAspect(otherCoord);
        return SignalAspect.mostRestrictive(myAspect, otherAspect);
    }

    private SignalAspect determineMyAspect(WorldCoordinate otherCoord) {
        WorldCoordinate myTrack = getTrackLocation();
        if (myTrack == null)
            return SignalAspect.RED;
        WorldCoordinate otherTrack = getOtherTrackLocation(otherCoord);
        if (otherTrack == null)
            return SignalAspect.YELLOW;

        int y1, y2;
        TrackTools.TrackScan scan = trackScans.get(otherTrack);
        if (scan != null) {
            y1 = scan.minY;
            y2 = scan.maxY + 1;
        } else {
            y1 = Math.min(myTrack.y, otherTrack.y);
            y2 = Math.max(myTrack.y, otherTrack.y) + 1;
        }

        int x1 = Math.min(myTrack.x, otherTrack.x);
        int z1 = Math.min(myTrack.z, otherTrack.z);
        int x2 = Math.max(myTrack.x, otherTrack.x) + 1;
        int z2 = Math.max(myTrack.z, otherTrack.z) + 1;

        boolean zAxis = Math.abs(myTrack.x - otherTrack.x) < Math.abs(myTrack.z - otherTrack.z);
        int xOffset = otherTrack.x > myTrack.x ? -3 : 3;
        int zOffset = otherTrack.z > myTrack.z ? -3 : 3;

        List<EntityMinecart> carts = CartTools.getMinecartsIn(tile.getWorldObj(), x1, y1, z1, x2, y2, z2);
//        System.out.printf("%d, %d, %d, %d, %d, %d\n", i1, j1, k1, i2, j2, k2);
//        System.out.println("carts = " + carts.size());
        SignalAspect newAspect = SignalAspect.GREEN;
        for (EntityMinecart cart : carts) {
            int cartX = MathHelper.floor_double(cart.posX);
            int cartZ = MathHelper.floor_double(cart.posZ);
            if (Math.abs(cart.motionX) < 0.08 && Math.abs(cart.motionZ) < 0.08)
                return SignalAspect.RED;
            else if (zAxis)
                if (cartZ > myTrack.z + zOffset && cart.motionZ < 0)
                    return SignalAspect.RED;
                else if (cartZ < myTrack.z + zOffset && cart.motionZ > 0)
                    return SignalAspect.RED;
                else
                    newAspect = SignalAspect.YELLOW;
            else if (cartX > myTrack.x + xOffset && cart.motionX < 0)
                return SignalAspect.RED;
            else if (cartX < myTrack.x + xOffset && cart.motionX > 0)
                return SignalAspect.RED;
            else
                newAspect = SignalAspect.YELLOW;
        }
        return newAspect;
    }

    protected WorldCoordinate getOtherTrackLocation(WorldCoordinate otherCoord) {
        SignalBlock other = getSignalAt(otherCoord);
        if (other != null) {
            WorldCoordinate track = other.getTrackLocation();
            trackCache.put(otherCoord, track);
            return track;
        }
        return trackCache.get(otherCoord);
    }

    private boolean isSignalBlockValid(WorldCoordinate other) {
        if (other == null)
            return true;
        if (getSignalAt(other) == null)
            return true;
        WorldCoordinate myTrack = getTrackLocation();
        WorldCoordinate otherTrack = getOtherTrackLocation(other);
        if (myTrack == null || otherTrack == null)
            return false;
        TrackTools.TrackScan scan = TrackTools.scanStraightTrackSection(tile.getWorldObj(), myTrack.x, myTrack.y, myTrack.z, otherTrack.x, otherTrack.y, otherTrack.z);
        trackScans.put(otherTrack, scan);
        if (!scan.areConnected)
            return false;
        return true;
    }

    @Override
    public void tickServer() {
        super.tickServer();
        update++;
        if (update % Signals.getSignalUpdateInterval() == 0)
            updateSignalAspect();
        if (update % Signals.VALIDATION_CHECK_INTERVAL == 0) {
            Status trackStatus = getTrackStatus();
            switch (trackStatus) {
                case INVALID:
                    clearSignalBlockPairing(null, "Signal Block dropped because no track was found near Signal. [{0}, {1}, {2}]", tile.xCoord, tile.yCoord, tile.zCoord);
                    break;
                case VALID:
                    for (WorldCoordinate otherCoord : waitingForRetest) {
                        if (!isSignalBlockValid(otherCoord))
                            clearSignalBlockPairing(otherCoord, "Signal Block dropped because track between Signals was invalid. [{0}, {1}, {2}]", tile.xCoord, tile.yCoord, tile.zCoord);
                    }
                    waitingForRetest.clear();
                    for (WorldCoordinate otherCoord : pairings) {
                        if (!isSignalBlockValid(otherCoord))
                            waitingForRetest.add(otherCoord);
                    }
                    break;
            }
        }
    }

    public boolean isWaitingForRetest() {
        return !waitingForRetest.isEmpty();
    }

    @Override
    protected String getTagName() {
        return "SignalBlock";
    }

    public WorldCoordinate getTrackLocation() {
        if (trackLocation == null)
            locateTrack();
        return trackLocation;
    }

    private Status getTrackStatus() {
        if (trackLocation == null)
            return locateTrack();
        if (!tile.getWorldObj().blockExists(trackLocation.x, trackLocation.y, trackLocation.z))
            return Status.UNKNOWN;
        if (!TrackTools.isRailBlockAt(tile.getWorldObj(), trackLocation.x, trackLocation.y, trackLocation.z)) {
            trackLocation = null;
            return Status.INVALID;
        }
        return Status.VALID;
    }

    private Status locateTrack() {
        int x = tile.xCoord;
        int y = tile.yCoord;
        int z = tile.zCoord;
        Status status = testForTrack(x, y, z);
        if (status != Status.INVALID)
            return status;
        status = testForTrack(x - 1, y, z);
        if (status != Status.INVALID)
            return status;
        status = testForTrack(x + 1, y, z);
        if (status != Status.INVALID)
            return status;
        status = testForTrack(x, y, z - 1);
        if (status != Status.INVALID)
            return status;
        status = testForTrack(x, y, z + 1);
        if (status != Status.INVALID)
            return status;
        status = testForTrack(x - 2, y, z);
        if (status != Status.INVALID)
            return status;
        status = testForTrack(x + 2, y, z);
        if (status != Status.INVALID)
            return status;
        status = testForTrack(x, y, z - 2);
        if (status != Status.INVALID)
            return status;
        status = testForTrack(x, y, z + 2);
        if (status != Status.INVALID)
            return status;
        return Status.INVALID;
    }

    private Status testForTrack(int x, int y, int z) {
        World world = tile.getWorldObj();
        for (int jj = -2; jj < 4; jj++) {
            if (!world.blockExists(x, y - jj, z))
                return Status.UNKNOWN;
            if (TrackTools.isRailBlockAt(world, x, y - jj, z)) {
                trackLocation = new WorldCoordinate(world.provider.dimensionId, x, y - jj, z);
                return Status.VALID;
            }
        }
        return Status.INVALID;
    }

    public static enum Status {

        VALID, INVALID, UNKNOWN
    }
}
