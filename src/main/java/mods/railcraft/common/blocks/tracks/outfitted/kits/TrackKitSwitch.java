/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.tracks.ISwitchActuator;
import mods.railcraft.api.tracks.ISwitchActuator.ArrowDirection;
import mods.railcraft.api.tracks.ITrackKitSwitch;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TrackKitSwitch extends TrackKitRailcraft implements ITrackKitSwitch {
    private static final int SPRING_DURATION = 30;
    protected boolean mirrored;
    protected boolean shouldSwitch;
    protected Set<UUID> lockingCarts = new HashSet<>();
    protected Set<UUID> springingCarts = new HashSet<>();
    protected Set<UUID> decidingCarts = new HashSet<>();
    private byte sprung;
    private byte locked;
    private @Nullable UUID currentCart;
    private @Nullable ISwitchActuator switchDevice;
    private boolean clientSwitched;

    @Override
    public List<ItemStack> getDrops(int fortune) {
        return Collections.emptyList();
    }

    @Override
    public boolean isMirrored() {
        return mirrored;
    }

    @Override
    public boolean isVisuallySwitched() {
        if (Game.isHost(theWorldAsserted()))
            return !isLocked() && (shouldSwitch || isSprung());
        return clientSwitched;
    }

    /**
     * This is a method provided to the subclasses to determine more accurately for
     * the passed in cart whether the switch is sprung or not. It caches the server
     * responses for the clients to use.
     * Note: This method should not modify any variables except the cache, we leave
     * that to update().
     */
    protected boolean shouldSwitchForCart(@Nullable EntityMinecart cart) {
        if (cart == null || Game.isClient(theWorldAsserted()))
            return isVisuallySwitched();

        if (springingCarts.contains(cart.getPersistentID()))
            return true; // Carts at the spring entrance always are on switched tracks

        if (lockingCarts.contains(cart.getPersistentID()))
            return false; // Carts at the locking entrance always are on locked tracks

        boolean sameTrain = Train.get(cart).map(t -> t.contains(currentCart)).orElse(false);

        boolean shouldSwitch = (switchDevice != null) && switchDevice.shouldSwitch(cart);

        if (isSprung()) {
            // we're either same train or switched so return true
            return shouldSwitch || sameTrain;
            // detected new train, we can safely treat this as not switched
        }

        if (isLocked()) {
            // detected new train, we can safely treat this as switched
            return shouldSwitch && !sameTrain;
            // other cases we obey locked
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
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        determineRailDirection();
        determineMirror(false);

        // Notify any neighboring switches that we exist so they know to register themselves with us
        ((TileRailcraft) getTile()).notifyBlocksOfNeighborChange();
    }

    @Override
    public void onBlockRemoved() {
        super.onBlockRemoved();
        // Notify any neighboring switches that we exist so they know to register themselves with us
        ((TileRailcraft) getTile()).notifyBlocksOfNeighborChange();
    }

    protected void determineRailDirection() {
        World world = theWorldAsserted();
        EnumRailDirection dir = TrackTools.getTrackDirectionRaw(world.getBlockState(getPos()));
        if (TrackTools.isRailBlockAt(world, getPos().east()) && TrackTools.isRailBlockAt(world, getPos().west())) {
            if (dir != EnumRailDirection.EAST_WEST)
                TrackTools.setTrackDirection(world, getPos(), EnumRailDirection.EAST_WEST);
//        } else if (TrackTools.isRailBlockAt(world, getPos().north()) && TrackTools.isRailBlockAt(world, getPos().south())) {
//            if (dir != EnumRailDirection.NORTH_SOUTH)
//                TrackTools.setTrackDirection(world, getPos(), EnumRailDirection.NORTH_SOUTH);
        } else if (dir != EnumRailDirection.NORTH_SOUTH)
            TrackTools.setTrackDirection(world, getPos(), EnumRailDirection.NORTH_SOUTH);
    }

    protected void determineMirror(boolean updateClient) {
        World world = theWorldAsserted();
        EnumRailDirection dir = TrackTools.getTrackDirection(world, getPos());
        boolean prevValue = isMirrored();
        if (dir == EnumRailDirection.NORTH_SOUTH) {
            BlockPos offset = getPos();
            if (TrackTools.isRailBlockAt(world, offset.west())) {
                offset = offset.west();
                mirrored = true; // West
            } else {
                offset = offset.east();
                mirrored = false; // East
            }
            if (TrackTools.isRailBlockAt(world, offset)) {
                EnumRailDirection otherDir = TrackTools.getTrackDirection(world, offset);
                if (otherDir == EnumRailDirection.NORTH_SOUTH)
                    TrackTools.setTrackDirection(world, offset, EnumRailDirection.EAST_WEST);
            }
        } else if (dir == EnumRailDirection.EAST_WEST)
            mirrored = TrackTools.isRailBlockAt(world, getPos().north());

        if (updateClient && prevValue != isMirrored())
            sendUpdateToClient();
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, @Nullable Block block) {
        if (Game.isHost(theWorldAsserted())) {
            determineRailDirection();
            determineMirror(true);
        }
        super.onNeighborBlockChange(state, block);
    }

    private void writeCartsToNBT(String key, Set<UUID> carts, NBTTagCompound data) {
        data.setByte(key + "Size", (byte) carts.size());
        int i = 0;
        for (UUID uuid : carts)
            NBTPlugin.writeUUID(data, key + i++, uuid);
    }

    private Set<UUID> readCartsFromNBT(String key, NBTTagCompound data) {
        Set<UUID> cartUUIDs = new HashSet<>();
        String sizeKey = key + "Size";
        if (data.hasKey(sizeKey)) {
            byte size = data.getByte(sizeKey);
            cartUUIDs = IntStream.range(0, size)
                    .mapToObj(i -> NBTPlugin.readUUID(data, key + i))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
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
        NBTPlugin.writeUUID(data, "currentCart", currentCart);
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
        currentCart = NBTPlugin.readUUID(data, "currentCart");
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
    public void update() {
        super.update();

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
        // mainly for visual purposes as the subclass's getRailDirectionRaw()
        // determines which direction the carts actually take.
        List<EntityMinecart> cartsOnTrack = EntitySearcher.findMinecarts().around(getPos()).outTo(-0.3f).in(theWorldAsserted());
        Set<UUID> uuidOnTrack = cartsOnTrack.stream().map(Entity::getUniqueID).collect(Collectors.toSet());

        EntityMinecart bestCart = getBestCartForVisualState(cartsOnTrack);

        // We must ask the switch every tick so we can update shouldSwitch properly
        switchDevice = getSwitchDevice();
        if (switchDevice == null) {
            shouldSwitch = false;
        } else {
            shouldSwitch = switchDevice.shouldSwitch(bestCart);
        }

        // Only allow cartsOnTrack to actually spring or lock the track
        if (bestCart != null && uuidOnTrack.contains(bestCart.getPersistentID())) {
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
        double cx = getPos().getX() + .5; // Why not calc this outside and cache it?
        double cz = getPos().getZ() + .5; // b/c this is a rare occurrence that we need to calc this
        return Math.abs(cart.posX - cx) + Math.abs(cart.posZ - cz); // not the real distance function but enough for us
    }

    // To render the state of the track most accurately, we choose the "best" cart from our set of
    // carts based on distance.
    private @Nullable EntityMinecart getBestCartForVisualState(List<EntityMinecart> cartsOnTrack) {
        World world = theWorldAsserted();
        if (!cartsOnTrack.isEmpty()) {
            return cartsOnTrack.get(0);
        } else {
            EntityMinecart closestCart = null;
            ArrayList<UUID> allCarts = new ArrayList<>();
            allCarts.addAll(lockingCarts);
            allCarts.addAll(springingCarts);
            allCarts.addAll(decidingCarts);

            for (UUID testCartUUID : allCarts) {
                if (closestCart == null) {
                    closestCart = CartTools.getCartFromUUID(world, testCartUUID);
                } else {
                    double closestDist = crudeDistance(closestCart);
                    EntityMinecart testCart = CartTools.getCartFromUUID(world, testCartUUID);
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

    public abstract EnumFacing getActuatorLocation();

    public abstract ArrowDirection getRedSignDirection();

    public abstract ArrowDirection getWhiteSignDirection();

    public @Nullable ISwitchActuator getSwitchDevice() {
        TileEntity entity = ((TileRailcraft) getTile()).getTileCache().getTileOnSide(getActuatorLocation());
        if (entity instanceof ISwitchActuator) {
            return (ISwitchActuator) entity;
        }
        return null;
    }
}
