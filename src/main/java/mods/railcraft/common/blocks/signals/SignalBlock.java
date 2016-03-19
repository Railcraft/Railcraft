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
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.api.tracks.RailTools;
import mods.railcraft.api.tracks.TrackScanner;
import mods.railcraft.common.blocks.tracks.TrackTools;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.MessageFormatMessage;

import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class SignalBlock extends AbstractPair {

    private static final Level DEBUG_LEVEL = Level.INFO;
    //    private static final Map<UUID, Deque<WorldCoordinate>> savedData = new HashMap<UUID, Deque<WorldCoordinate>>();
    private final Map<WorldCoordinate, WorldCoordinate> trackCache = new HashMap<WorldCoordinate, WorldCoordinate>();
    private final Map<WorldCoordinate, TrackScanner.ScanResult> trackScans = new HashMap<WorldCoordinate, TrackScanner.ScanResult>();
    private final Set<WorldCoordinate> waitingForRetest = new HashSet<WorldCoordinate>();
    private WorldCoordinate trackLocation;
    private int update = rand.nextInt();
    //    private UUID uuid = UUID.randomUUID();
    private boolean changedAspect = false;

    public SignalBlock(String locTag, TileEntity tile, int numPairs) {
        super(locTag, tile, numPairs);
    }

    private SignalBlock getSignalAt(WorldCoordinate coord) {
        TileEntity recv = getPairAt(coord);
        if (recv != null)
            return ((ISignalBlockTile) recv).getSignalBlock();
        return null;
    }

    public abstract SignalAspect getSignalAspect();

    public void log(Level level, String msg, Object... args) {
        if (msg != null)
            LogManager.getLogger("Railcraft").log(level, new MessageFormatMessage(msg, args));
    }

    private void printDebug(String msg, Object... args) {
        if (SignalTools.printSignalDebug)
            log(DEBUG_LEVEL, msg, args);
    }

    private void printDebugPair(String msg, TileEntity ot) {
        if (SignalTools.printSignalDebug)
            if (ot == null)
                log(DEBUG_LEVEL, msg + " source:[{0}] target:[null]", tile.getPos());
            else
                log(DEBUG_LEVEL, msg + " source:[{0}] target:[{1}] target class:{2}", tile.getPos(), ot.getPos(), ot.getClass());

    }

    private void printDebugPair(String msg, WorldCoordinate coord) {
        if (SignalTools.printSignalDebug)
            if (coord == null)
                log(DEBUG_LEVEL, msg + " source:[{0}] target:[null]", tile.getPos());
            else
                log(DEBUG_LEVEL, msg + " source:[{0}] target:[{1}]", tile.getPos(), coord);
    }

    @Override
    protected void saveNBT(NBTTagCompound data) {
        super.saveNBT(data);
//        MiscTools.writeUUID(data, "uuid", uuid);
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<WorldCoordinate, WorldCoordinate> cache : trackCache.entrySet()) {
            NBTTagCompound entry = new NBTTagCompound();
            if (cache.getKey() != null && cache.getValue() != null) {
                cache.getKey().writeToNBT(entry, "key");
                cache.getValue().writeToNBT(entry, "value");
                tagList.appendTag(entry);
            }
        }
        data.setTag("trackCache", tagList);
//        if (RailcraftConfig.printSignalDebug()) {
//            Deque<WorldCoordinate> test = new LinkedList<WorldCoordinate>();
//            NBTTagList list = data.getTagList("pairings", 10);
//            for (byte entry = 0; entry < list.tagCount(); entry++) {
//                NBTTagCompound tag = list.getCompoundTagAt(entry);
//                int[] c = tag.getIntArray("coords");
//                test.add(new WorldCoordinate(c[0], c[1], c[2], c[3]));
//            }
//            boolean isConsistent = test.containsAll(getPairs());
//            printDebug("Signal Block saved NBT. [{0}, {1}, {2}] [verified: {3}] [changedAspect: {4}] [data: {5}]", tile.xCoord, tile.yCoord, tile.zCoord, isConsistent, changedAspect, test);
        printDebug("Signal Block saved NBT. [{0}] [changedAspect: {1}] [data: {1}]", tile.getPos(), changedAspect, pairings);
//            savedData.put(uuid, new LinkedList<WorldCoordinate>(pairings));
//        }
    }

    @Override
    protected void loadNBT(NBTTagCompound data) {
        super.loadNBT(data);
//        uuid = MiscTools.readUUID(data, "uuid");
        if (data.hasKey("trackCache")) {
            NBTTagList tagList = data.getTagList("trackCache", 10);
            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound nbt = tagList.getCompoundTagAt(i);
                WorldCoordinate key = WorldCoordinate.readFromNBT(nbt, "key");
                WorldCoordinate value = WorldCoordinate.readFromNBT(nbt, "value");
                trackCache.put(key, value);
            }
        }
//        if (RailcraftConfig.printSignalDebug()) {
//            String isConsistent = "unknown";
//            Deque<WorldCoordinate> lastSave = savedData.get(uuid);
//            if (lastSave != null) {
//                if (pairings.containsAll(lastSave))
//                    isConsistent = "true";
//                else
//                    isConsistent = "false";
//            }

        printDebug("Signal Block loaded NBT. [{0}] [data: {1}]", tile.getPos(), pairings);
//        }
    }

    @Override
    public void clearPairing(WorldCoordinate other) {
        printDebugPair("Signal Block pair cleared. ", other);
        if (SignalTools.printSignalDebug) {
//            logTrace(DEBUG_LEVEL, 10, "Signal Block code Path");

            Block block = tile.getWorld().getBlockState(other).getBlock();
            if (block != null)
                log(DEBUG_LEVEL, "Signal Block target block [{0}, {1}, {2}] = {3}, {4}", other, block.getClass(), block.getUnlocalizedName());
            else
                log(DEBUG_LEVEL, "Signal Block target block [{0}, {1}, {2}] = null", other);
            TileEntity t = tile.getWorld().getTileEntity(other);
            if (t != null)
                log(DEBUG_LEVEL, "Signal Block target tile [{0}] = {1}", t.getPos(), t.getClass());
            else
                log(DEBUG_LEVEL, "Signal Block target tile [{0}] = null", other);
        }
        super.clearPairing(other);
    }

    private void clearSignalBlockPairing(WorldCoordinate other, String reason, Object... args) {
        printDebug(reason, args);
        if (other == null)
            clearPairings();
        else
            clearPairing(other);
    }

    @Override
    protected void addPairing(WorldCoordinate other) {
        pairings.remove(other);
        pairings.add(other);
        while (pairings.size() > getMaxPairings()) {
            WorldCoordinate pair = pairings.remove();
            printDebugPair("Signal Block dropped because too many pairs.", pair);
        }
        SignalTools.packetBuilder.sendPairPacketUpdate(this);
    }

    @Override
    public boolean isValidPair(WorldCoordinate otherCoord, TileEntity otherTile) {
        if (otherTile instanceof ISignalBlockTile) {
            SignalBlock signalBlock = ((ISignalBlockTile) otherTile).getSignalBlock();
            return signalBlock.isPairedWith(getCoords());
        }
        return false;
    }

    @Override
    public void cleanPairings() {
        if (!invalidPairings.isEmpty())
            printDebug("Signal Block pairs cleaned: source:[{0}] targets: {1}", tile.getPos(), invalidPairings);
        super.cleanPairings();
    }

    //    @Override
//    public void startPairing() {
//        clearSignalBlockPairing("Signal Block pairing cleared in preparation to start a new pairing.  [{0}, {1}, {2}]", tile.xCoord, tile.yCoord, tile.zCoord);
//        super.startPairing();
//    }
    public boolean createSignalBlock(SignalBlock other) {
        if (other == this) {
            printDebugPair("Signal Block creation was aborted, cannot pair with self.", other.tile);
            return false;
        }
        printDebugPair("Signal Block creation being attempted.", other.tile);
        locateTrack();
        other.locateTrack();
        WorldCoordinate myTrack = getTrackLocation();
        WorldCoordinate otherTrack = other.getTrackLocation();
        if (myTrack == null || otherTrack == null) {
            printDebugPair("Signal Block creation failed, could not find Track.", other.tile);
            return false;
        }
        TrackScanner.ScanResult scan = TrackScanner.scanStraightTrackSection(tile.getWorld(), myTrack, otherTrack);
        if (!scan.areConnected) {
            printDebugPair("Signal Block creation failed, could not find Path.", other.tile);
            return false;
        }
        addPairing(other.getCoords());
        other.addPairing(getCoords());
        endPairing();
        other.endPairing();
        trackScans.put(otherTrack, scan);
        printDebugPair("Signal Block created successfully.", other.tile);
        return true;
    }

    protected abstract void updateSignalAspect();

    protected abstract SignalAspect getSignalAspectForPair(WorldCoordinate otherCoord);

    public SignalAspect determineAspect(WorldCoordinate otherCoord) {
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

        TrackScanner.ScanResult scan = getOrCreateTrackScan(otherTrack);
        int y1 = scan.minY;
        int y2 = scan.maxY + 1;

        int x1 = Math.min(myTrack.getX(), otherTrack.getX());
        int z1 = Math.min(myTrack.getZ(), otherTrack.getZ());
        int x2 = Math.max(myTrack.getX(), otherTrack.getX()) + 1;
        int z2 = Math.max(myTrack.getZ(), otherTrack.getZ()) + 1;

        boolean zAxis = Math.abs(myTrack.getX() - otherTrack.getX()) < Math.abs(myTrack.getZ() - otherTrack.getZ());
        int xOffset = otherTrack.getX() > myTrack.getX() ? -3 : 3;
        int zOffset = otherTrack.getZ() > myTrack.getZ() ? -3 : 3;

        List<EntityMinecart> carts = CartTools.getMinecartsIn(tile.getWorld(), new BlockPos(x1, y1, z1), new BlockPos(x2, y2, z2));
//        System.out.printf("%d, %d, %d, %d, %d, %d\n", i1, j1, k1, i2, j2, k2);
//        System.out.println("carts = " + carts.size());
        SignalAspect newAspect = SignalAspect.GREEN;
        for (EntityMinecart cart : carts) {
            int cartX = MathHelper.floor_double(cart.posX);
            int cartZ = MathHelper.floor_double(cart.posZ);
            if (Math.abs(cart.motionX) < 0.08 && Math.abs(cart.motionZ) < 0.08)
                return SignalAspect.RED;
            else if (zAxis)
                if (cartZ > myTrack.getZ() + zOffset && cart.motionZ < 0)
                    return SignalAspect.RED;
                else if (cartZ < myTrack.getZ() + zOffset && cart.motionZ > 0)
                    return SignalAspect.RED;
                else
                    newAspect = SignalAspect.YELLOW;
            else if (cartX > myTrack.getX() + xOffset && cart.motionX < 0)
                return SignalAspect.RED;
            else if (cartX < myTrack.getX() + xOffset && cart.motionX > 0)
                return SignalAspect.RED;
            else
                newAspect = SignalAspect.YELLOW;
        }
        return newAspect;
    }

    private TrackScanner.ScanResult getOrCreateTrackScan(WorldCoordinate otherTrack) {
        TrackScanner.ScanResult scan = trackScans.get(otherTrack);
        if (scan == null) {
            WorldCoordinate myTrack = getTrackLocation();
            scan = TrackScanner.scanStraightTrackSection(tile.getWorld(), myTrack, otherTrack);
            trackScans.put(otherTrack, scan);
        }
        return scan;
    }

    private WorldCoordinate getOtherTrackLocation(WorldCoordinate otherCoord) {
        SignalBlock other = getSignalAt(otherCoord);
        if (other != null) {
            WorldCoordinate track = other.getTrackLocation();
            if (track != null)
                trackCache.put(otherCoord, track);
            return track;
        }
        return trackCache.get(otherCoord);
    }

    private TrackValidationStatus isSignalBlockValid(WorldCoordinate other) {
        if (other == null)
            return new TrackValidationStatus(true, "UNVERIFIABLE_COORD_NULL");
        SignalBlock otherSignalBlock = getSignalAt(other);
        if (otherSignalBlock == null)
            return new TrackValidationStatus(true, "UNVERIFIABLE_OTHER_SIGNAL_NULL");
        WorldCoordinate myTrack = getTrackLocation();
        if (myTrack == null)
            return new TrackValidationStatus(false, "INVALID_MY_TRACK_NULL");
        Status otherTrackStatus = otherSignalBlock.getTrackStatus();
        if (otherTrackStatus == Status.INVALID)
            return new TrackValidationStatus(false, "INVALID_OTHER_TRACK_INVALID");
        WorldCoordinate otherTrack = trackCache.get(other);
        if (otherTrackStatus == Status.UNKNOWN) {
            if (otherTrack == null)
                return new TrackValidationStatus(true, "UNVERIFIABLE_OTHER_TRACK_UNKNOWN");
        } else {
            otherTrack = otherSignalBlock.getTrackLocation();
            if (otherTrack != null)
                trackCache.put(other, otherTrack);
        }
        if (otherTrack == null)
            return new TrackValidationStatus(true, "UNVERIFIABLE_OTHER_TRACK_NULL");
        TrackScanner.ScanResult scan = TrackScanner.scanStraightTrackSection(tile.getWorld(), myTrack, otherTrack);
        trackScans.put(otherTrack, scan);
        if (scan.verdict == TrackScanner.ScanResult.Verdict.VALID)
            return new TrackValidationStatus(true, "VALID");
        if (scan.verdict == TrackScanner.ScanResult.Verdict.UNKNOWN)
            return new TrackValidationStatus(true, "UNVERIFIABLE_UNLOADED_CHUNK");
        return new TrackValidationStatus(false, "INVALID_SCAN_FAIL: " + scan.verdict.name());
    }

    @Override
    public void tickServer() {
        super.tickServer();
        update++;
        try {
            if (!isLoaded())
                return;
        } catch (Throwable ex) {
//            Game.logErrorAPI("Railcraft", ex, AbstractPair.class);
        }
        if (update % Signals.getSignalUpdateInterval() == 0) {
            SignalAspect prev = getSignalAspect();
            if (prev != SignalAspect.BLINK_RED)
                changedAspect = true;
            updateSignalAspect();
            if (getSignalAspect() == SignalAspect.BLINK_RED && prev != SignalAspect.BLINK_RED)
                printDebug("Signal Block changed aspect to BLINK_RED: source:[{0}] pairs: {1}", tile.getPos(), pairings);
        }
        if (update % Signals.VALIDATION_CHECK_INTERVAL == 0) {
            Status trackStatus = getTrackStatus();
            switch (trackStatus) {
                case INVALID:
                    clearSignalBlockPairing(null, "Signal Block dropped because no track was found near Signal. [{0}]", tile.getPos());
                    break;
                case VALID:
                    for (WorldCoordinate otherCoord : waitingForRetest) {
                        TrackValidationStatus status = isSignalBlockValid(otherCoord);
                        if (!status.isValid)
                            clearSignalBlockPairing(otherCoord, "Signal Block dropped because track between Signals was invalid. source:[{0}] target:[{1}, {2}, {3}] reason:{4}", tile.getPos(), otherCoord, status.message);
                    }
                    waitingForRetest.clear();
                    for (WorldCoordinate otherCoord : getPairs()) {
                        if (!isSignalBlockValid(otherCoord).isValid)
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
        if (!tile.getWorld().isBlockLoaded(trackLocation))
            return Status.UNKNOWN;
        if (!RailTools.isRailBlockAt(tile.getWorld(), trackLocation)) {
            trackLocation = null;
            return Status.INVALID;
        }
        return Status.VALID;
    }

    private Status locateTrack() {
        int x = tile.getPos().getX();
        int y = tile.getPos().getY();
        int z = tile.getPos().getZ();
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
        World world = tile.getWorld();
        for (int jj = -2; jj < 4; jj++) {
            BlockPos pos = new BlockPos(x, y - jj, z);
            if (!world.isBlockLoaded(pos))
                return Status.UNKNOWN;
            if (RailTools.isRailBlockAt(world, pos)) {
                trackLocation = new WorldCoordinate(world.provider.getDimensionId(), pos);
                return Status.VALID;
            }
        }
        return Status.INVALID;
    }

    public enum Status {

        VALID, INVALID, UNKNOWN
    }

    private static class TrackValidationStatus {
        public final boolean isValid;
        public final String message;

        public TrackValidationStatus(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
    }
}
