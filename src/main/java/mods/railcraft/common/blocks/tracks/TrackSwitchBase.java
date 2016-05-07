/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.ISwitchDevice;
import mods.railcraft.api.tracks.ISwitchDevice.ArrowDirection;
import mods.railcraft.api.tracks.ITrackSwitch;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.carts.CartUtils;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TrackSwitchBase extends TrackBaseRailcraft implements ITrackSwitch {
    private static final int SPRING_DURATION = 30;
    protected boolean mirrored;
    protected boolean shouldSwitch;
    protected Set<UUID> lockingCarts = new HashSet<UUID>();
    protected Set<UUID> springingCarts = new HashSet<UUID>();
    protected Set<UUID> decidingCarts = new HashSet<UUID>();
    private byte sprung;
    private byte locked;
    private UUID currentCart;
    private ISwitchDevice switchDevice;
    private boolean clientSwitched;

    @Override
    public boolean canMakeSlopes() {
        return false;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public boolean isFlexibleRail() {
        return false;
    }

    @Override
    public boolean isMirrored() {
        return mirrored;
    }

    @Override
    public boolean isVisuallySwitched() {
        if (Game.isHost(getWorld()))
            return !isLocked() && (shouldSwitch || isSprung());
        return clientSwitched;
    }

    /**
     * This is a method provided to the subclasses to determine more accurately for
     * the passed in cart whether the switch is sprung or not. It caches the server
     * responses for the clients to use.
     * Note: This method should not modify any variables except the cache, we leave
     * that to updateEntity().
     */
    protected boolean shouldSwitchForCart(EntityMinecart cart) {
        if (cart == null || Game.isNotHost(getWorld()))
            return isVisuallySwitched();

        if (springingCarts.contains(cart.getPersistentID()))
            return true; // Carts at the spring entrance always are on switched tracks

        if (lockingCarts.contains(cart.getPersistentID()))
            return false; // Carts at the locking entrance always are on locked tracks

        boolean sameTrain = Train.areInSameTrain(LinkageManager.instance().getCartFromUUID(currentCart), cart);

        boolean shouldSwitch = (switchDevice != null) ? switchDevice.shouldSwitch(this, cart) : false;

        if (isSprung()) {
            if (shouldSwitch || sameTrain) {
                // we're either same train or switched so return true
                return true;
            }
            // detected new train, we can safely treat this as not switched
            return false;
        }

        if (isLocked()) {
            if (shouldSwitch && !sameTrain) {
                // detected new train, we can safely treat this as switched
                return true;
            }
            // other cases we obey locked
            return false;
        }

        // we're not sprung or locked so we should return shouldSwitch
        return shouldSwitch;
    }

    private void springTrack(UUID cartOnTrack) {
        sprung = SPRING_DURATION;
        locked = 0;
        currentCart = cartOnTrack;
    }

    private void lockTrack(UUID cartOnTrack) {
        locked = SPRING_DURATION;
        sprung = 0;
        currentCart = cartOnTrack;
    }

    public boolean isLocked() {
        return locked > 0;
    }

    public boolean isSprung() {
        return sprung > 0;
    }

    @Override
    public void onBlockPlaced() {
        determineTrackMeta();
        determineMirror();

        // Notify any neighboring switches that we exist so they know to register themselves with us
        ((RailcraftTileEntity) tileEntity).notifyBlocksOfNeighborChange();
    }

    @Override
    public void onBlockRemoved() {
        super.onBlockRemoved();
        // Notify any neighboring switches that we exist so they know to register themselves with us
        ((RailcraftTileEntity) tileEntity).notifyBlocksOfNeighborChange();
    }

    protected void determineTrackMeta() {
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        int meta = tileEntity.getBlockMetadata();
        if (TrackTools.isRailBlockAt(getWorld(), x + 1, y, z) && TrackTools.isRailBlockAt(getWorld(), x - 1, y, z)) {
            if (meta != EnumTrackMeta.EAST_WEST.ordinal())
                getWorld().setBlockMetadataWithNotify(x, y, z, EnumTrackMeta.EAST_WEST.ordinal(), 3);
        } else if (TrackTools.isRailBlockAt(getWorld(), x, y, z + 1) && TrackTools.isRailBlockAt(getWorld(), x, y, z - 1)) {
            if (meta != EnumTrackMeta.NORTH_SOUTH.ordinal())
                getWorld().setBlockMetadataWithNotify(x, y, z, EnumTrackMeta.NORTH_SOUTH.ordinal(), 3);
        } else if (meta != EnumTrackMeta.NORTH_SOUTH.ordinal())
            getWorld().setBlockMetadataWithNotify(x, y, z, EnumTrackMeta.NORTH_SOUTH.ordinal(), 3);
    }

    protected void determineMirror() {
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        int meta = tileEntity.getBlockMetadata();
        boolean prevValue = isMirrored();
        if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            int ii = x;
            if (TrackTools.isRailBlockAt(getWorld(), x - 1, y, z)) {
                ii--;
                mirrored = true; // West
            } else {
                ii++;
                mirrored = false; // East
            }
            if (TrackTools.isRailBlockAt(getWorld(), ii, y, z)) {
                int otherMeta = getWorld().getBlockMetadata(ii, y, z);
                if (otherMeta == EnumTrackMeta.NORTH_SOUTH.ordinal())
                    getWorld().setBlockMetadataWithNotify(ii, y, z, EnumTrackMeta.EAST_WEST.ordinal(), 3);
            }
        } else if (meta == EnumTrackMeta.EAST_WEST.ordinal())
            mirrored = TrackTools.isRailBlockAt(getWorld(), x, y, z - 1);

        if (prevValue != isMirrored())
            sendUpdateToClient();
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        if (Game.isHost(getWorld())) {
            determineTrackMeta();
            determineMirror();
        }
        super.onNeighborBlockChange(block);
    }

    private void writeCartsToNBT(String key, Set<UUID> carts, NBTTagCompound data) {
        data.setByte(key + "Size", (byte) carts.size());
        int i = 0;
        for (UUID uuid : carts)
            MiscTools.writeUUID(data, key + i++, uuid);
    }

    private Set<UUID> readCartsFromNBT(String key, NBTTagCompound data) {
        Set<UUID> cartUUIDs = new HashSet<UUID>();
        String sizeKey = key + "Size";
        if (data.hasKey(sizeKey)) {
            byte size = data.getByte(sizeKey);
            for (int i = 0; i < size; i++) {
                UUID id = MiscTools.readUUID(data, key + i);
                if (id != null)
                    cartUUIDs.add(id);
            }
        }
        return cartUUIDs;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("Direction", mirrored);
        data.setBoolean("Switched", shouldSwitch);
        data.setByte("sprung", sprung);
        data.setByte("locked", locked);
        writeCartsToNBT("springingCarts", springingCarts, data);
        writeCartsToNBT("lockingCarts", lockingCarts, data);
        writeCartsToNBT("decidingCarts", lockingCarts, data);
        MiscTools.writeUUID(data, "currentCart", currentCart);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        mirrored = data.getBoolean("Direction");
        shouldSwitch = data.getBoolean("Switched");
        sprung = data.getByte("sprung");
        locked = data.getByte("locked");
        springingCarts = readCartsFromNBT("springingCarts", data);
        lockingCarts = readCartsFromNBT("lockingCarts", data);
        decidingCarts = readCartsFromNBT("decidingCarts", data);
        currentCart = MiscTools.readUUID(data, "currentCart");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(mirrored);
        data.writeBoolean(isVisuallySwitched());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        boolean changed = false;
        boolean m = data.readBoolean();
        if (m != mirrored) {
            mirrored = m;
            changed = true;
        }
        boolean cs = data.readBoolean();
        if (cs != clientSwitched) {
            clientSwitched = cs;
            changed = true;
        }

        if (changed) {
            switchDevice = getSwitchDevice();
            if (switchDevice != null)
                switchDevice.updateArrows();
            markBlockNeedsUpdate();
        }
    }

    private void updateSet(Set<UUID> setToUpdate, List<UUID> potentialUpdates, Set<UUID> reject1, Set<UUID> reject2) {
        for (UUID cartUUID : potentialUpdates) {
            reject1.remove(cartUUID);
            reject2.remove(cartUUID);
            setToUpdate.add(cartUUID);
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        boolean wasSwitched = isVisuallySwitched();

        if (locked > 0)
            locked--;
        if (sprung > 0)
            sprung--;

        if (locked == 0 && sprung == 0) {
            lockingCarts.clear(); // Clear out our sets so we don't keep
            springingCarts.clear(); // these carts forever
            decidingCarts.clear();
            currentCart = null;
        }

        // updating carts we just found in appropriate sets
        // this keeps exiting carts from getting mixed up with entering carts
        updateSet(lockingCarts, getCartsAtLockEntrance(), springingCarts, decidingCarts);
        updateSet(springingCarts, getCartsAtSpringEntrance(), lockingCarts, decidingCarts);
        updateSet(decidingCarts, getCartsAtDecisionEntrance(), lockingCarts, springingCarts);

        // We only set sprung/locked when a cart enters our track, this is
        // mainly for visual purposes as the subclass's getBasicRailMetadata()
        // determines which direction the carts actually take.
        List<UUID> cartsOnTrack = CartUtils.getMinecartUUIDsAt(
                getWorld(), tileEntity.xCoord, tileEntity.yCoord,
                tileEntity.zCoord, 0.3f);

        EntityMinecart bestCart = getBestCartForVisualState(cartsOnTrack);

        // We must ask the switch every tick so we can update shouldSwitch properly
        switchDevice = getSwitchDevice();
        if (switchDevice == null) {
            shouldSwitch = false;
        } else {
            shouldSwitch = switchDevice.shouldSwitch(this, bestCart);
        }

        // Only allow cartsOnTrack to actually spring or lock the track
        if (bestCart != null && cartsOnTrack.contains(bestCart.getPersistentID())) {
            if (shouldSwitchForCart(bestCart)) {
                springTrack(bestCart.getPersistentID());
            } else {
                lockTrack(bestCart.getPersistentID());
            }
        }

        if (isVisuallySwitched() != wasSwitched) {
            if (switchDevice != null) {
                switchDevice.onSwitch(isVisuallySwitched());
            }
            sendUpdateToClient();
        }
    }

    private double crudeDistance(EntityMinecart cart) {
        double cx = getX() + .5; // Why not calc this outside and cache it?
        double cz = getZ() + .5; // b/c this is a rare occurance that we need to calc this
        return Math.abs(cart.posX - cx) + Math.abs(cart.posZ - cz); // not the real distance function but enough for us
    }

    // To render the state of the track most accurately, we choose the "best" cart from our set of
    // carts based on distance.
    private EntityMinecart getBestCartForVisualState(List<UUID> cartsOnTrack) {
        UUID cartUUID = null;
        if (!cartsOnTrack.isEmpty()) {
            cartUUID = cartsOnTrack.get(0);
            return LinkageManager.instance().getCartFromUUID(cartUUID);
        } else {
            EntityMinecart closestCart = null;
            ArrayList<UUID> allCarts = new ArrayList<UUID>();
            allCarts.addAll(lockingCarts);
            allCarts.addAll(springingCarts);
            allCarts.addAll(decidingCarts);

            for (UUID testCartUUID : allCarts) {
                if (closestCart == null) {
                    closestCart = LinkageManager.instance().getCartFromUUID(testCartUUID);
                } else {
                    double closestDist = crudeDistance(closestCart);
                    EntityMinecart testCart = LinkageManager.instance().getCartFromUUID(testCartUUID);
                    if (testCart != null) {
                        double testDist = crudeDistance(testCart);
                        if (testDist < closestDist)
                            closestCart = testCart;
                    }
                }
            }
            return closestCart;
        }
    }

    protected abstract List<UUID> getCartsAtLockEntrance();

    protected abstract List<UUID> getCartsAtSpringEntrance();

    protected abstract List<UUID> getCartsAtDecisionEntrance();

    public abstract ForgeDirection getActuatorLocation();

    public abstract ArrowDirection getRedSignDirection();

    public abstract ArrowDirection getWhiteSignDirection();

    public ISwitchDevice getSwitchDevice() {
        TileEntity entity = ((RailcraftTileEntity) this.tileEntity).getTileCache().getTileOnSide(getActuatorLocation());
        if (entity instanceof ISwitchDevice) {
            return (ISwitchDevice) entity;
        }
        return null;
    }
}
