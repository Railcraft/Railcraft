/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import mods.railcraft.api.electricity.IElectricMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unused")
public class Train implements Iterable<EntityMinecart> {
    public static final String TRAIN_HIGH = "rcTrainHigh";
    public static final String TRAIN_LOW = "rcTrainLow";
    private static final Map<UUID, Train> trains = new HashMap<UUID, Train>();
    private final UUID uuid;
    private final LinkedList<UUID> carts = new LinkedList<UUID>();
    private final List<UUID> safeCarts = Collections.unmodifiableList(carts);
    private final Collection<UUID> lockingTracks = new HashSet<UUID>();
    private TrainState trainState = TrainState.NORMAL;

    public Train(EntityMinecart cart) {
        uuid = UUID.randomUUID();

        buildTrain(null, cart);
    }

    public static Train getTrain(EntityMinecart cart) {
        Train train = trains.get(getTrainUUID(cart));
        if (train != null && !train.containsCart(cart)) {
            train.releaseTrain();
            trains.remove(train.getUUID());
            train = null;
        }
        if (train != null) {
            train.validate();
            if (train.isEmpty()) {
                train = null;
            }
        }
        if (train == null) {
            train = new Train(cart);
            trains.put(train.getUUID(), train);
        }
        return train;
    }

    private static Train getTrainUnsafe(EntityMinecart cart) {
        if (cart == null)
            return null;
        return trains.get(getTrainUUID(cart));
    }

    public static UUID getTrainUUID(EntityMinecart cart) {
        NBTTagCompound nbt = cart.getEntityData();
        if (nbt.hasKey(TRAIN_HIGH)) {
            long high = nbt.getLong(TRAIN_HIGH);
            long low = nbt.getLong(TRAIN_LOW);
            return new UUID(high, low);
        }
        return null;
    }

    public static void resetTrain(EntityMinecart cart) {
        Train train = trains.remove(getTrainUUID(cart));
        if (train != null)
            train.releaseTrain();
    }

    public static void resetTrain(UUID uuid) {
        Train train = trains.remove(uuid);
        if (train != null)
            train.releaseTrain();
    }

    public static boolean areInSameTrain(@Nullable EntityMinecart cart1, @Nullable EntityMinecart cart2) {
        if (cart1 == null || cart2 == null)
            return false;
        if (cart1 == cart2)
            return true;

        UUID train1 = getTrainUUID(cart1);
        UUID train2 = getTrainUUID(cart2);

        return Objects.equals(train1, train2);
    }

    public static Train getLongestTrain(EntityMinecart cart1, EntityMinecart cart2) {
        Train train1 = getTrain(cart1);
        Train train2 = getTrain(cart2);

        if (train1 == train2)
            return train1;
        if (train1.size() >= train2.size())
            return train1;
        return train2;
    }

    public static void removeTrainTag(EntityMinecart cart) {
        cart.getEntityData().removeTag(TRAIN_HIGH);
        cart.getEntityData().removeTag(TRAIN_LOW);
    }

    public static void addTrainTag(EntityMinecart cart, Train train) {
        UUID trainId = train.getUUID();
        cart.getEntityData().setLong(TRAIN_HIGH, trainId.getMostSignificantBits());
        cart.getEntityData().setLong(TRAIN_LOW, trainId.getLeastSignificantBits());
    }

    @Nullable
    public Train getTrain(@Nullable UUID cartUUID) {
        if (cartUUID == null)
            return null;
        EntityMinecart cart = LinkageManager.instance().getCartFromUUID(cartUUID);
        if (cart == null)
            return null;
        return getTrain(cart);
    }

    public Stream<EntityMinecart> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Iterator<EntityMinecart> iterator() {
        LinkageManager lm = LinkageManager.instance();
        return Iterators.transform(Iterators.unmodifiableIterator(carts.iterator()), lm::getCartFromUUID);
//        return new Iterator<EntityMinecart>() {
//            private final PeekingIterator<UUID> it = Iterators.peekingIterator(carts.iterator());
//            private final LinkageManager lm = LinkageManager.instance();
//            @Nullable
//            EntityMinecart nextCart;
//
//            @Override
//            public boolean hasNext() {
//                return it.hasNext() && (nextCart = lm.getCartFromUUID(it.peek())) != null;
//            }
//
//            @Override
//            public EntityMinecart next() {
//                it.next();
//                Objects.requireNonNull(nextCart);
//                return nextCart;
//            }
//
//            @Override
//            public void remove() {
//                throw new UnsupportedOperationException("Removing not supported.");
//            }
//        };
    }

    public Iterable<EntityMinecart> orderedIterable(final EntityMinecart head) {
        return () -> new Iterator<EntityMinecart>() {
            private final LinkageManager lm = LinkageManager.instance();
            private EntityMinecart last;
            private EntityMinecart current = head;

            @Override
            public boolean hasNext() {
                EntityMinecart next = lm.getLinkedCartA(current);
                if (next != null && next != last)
                    return true;
                next = lm.getLinkedCartB(current);
                return next != null && next != last;
            }

            @Override
            public EntityMinecart next() {
                EntityMinecart next = lm.getLinkedCartA(current);
                if (next != null && next != last) {
                    last = current;
                    current = next;
                    return current;
                }
                next = lm.getLinkedCartB(current);
                if (next != null && next != last) {
                    last = current;
                    current = next;
                    return current;
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removing not supported.");
            }
        };
    }

    private void buildTrain(@Nullable EntityMinecart prev, EntityMinecart next) {
        _addLink(prev, next);

        LinkageManager lm = LinkageManager.instance();
        EntityMinecart linkA = lm.getLinkedCartA(next);
        EntityMinecart linkB = lm.getLinkedCartB(next);

        if (linkA != null && linkA != prev && !containsCart(linkA))
            buildTrain(next, linkA);

        if (linkB != null && linkB != prev && !containsCart(linkB))
            buildTrain(next, linkB);
    }

    private void dropCarts(EntityMinecart cart) {
        _removeCart(cart);

        LinkageManager lm = LinkageManager.instance();
        EntityMinecart linkA = lm.getLinkedCartA(cart);
        EntityMinecart linkB = lm.getLinkedCartB(cart);

        if (linkA != null && containsCart(linkA)) dropCarts(linkA);
        if (linkB != null && containsCart(linkB)) dropCarts(linkB);
    }

    public void validate() {
        if (!isValid()) {
            UUID first = null;
            for (UUID id : carts) {
                if (isCartValid(id)) {
                    first = id;
                    break;
                }
            }
            releaseTrain();
            EntityMinecart firstCart;
            if (first == null || (firstCart = LinkageManager.instance().getCartFromUUID(first)) == null)
                trains.remove(getUUID());
            else
                buildTrain(null, firstCart);
        }
    }

    private boolean isValid() {
        for (UUID id : carts) {
            if (!isCartValid(id))
                return false;
        }
        return true;
    }

    private boolean isCartValid(UUID cartId) {
        EntityMinecart cart = LinkageManager.instance().getCartFromUUID(cartId);
        return cart != null && uuid.equals(getTrainUUID(cart));
    }

    protected void releaseTrain() {
        LinkageManager lm = LinkageManager.instance();
        for (UUID id : carts) {
            EntityMinecart cart = lm.getCartFromUUID(id);
            if (cart != null) {
                removeTrainTag(cart);
            }
        }
        carts.clear();
        lockingTracks.clear();
    }

    public UUID getUUID() {
        return uuid;
    }

    public void removeAllLinkedCarts(EntityMinecart cart) {
        UUID cartId = cart.getPersistentID();
        if (carts.contains(cartId)) {
            dropCarts(cart);
        }
    }

    public void addLink(EntityMinecart cart1, EntityMinecart cart2) {
        if (isTrainEnd(cart1))
            buildTrain(cart1, cart2);
        else if (isTrainEnd(cart2))
            buildTrain(cart2, cart1);
    }

    private void _addLink(@Nullable EntityMinecart cartBase, EntityMinecart cartNew) {
        if (cartBase == null || carts.getFirst() == cartBase.getPersistentID())
            carts.addFirst(cartNew.getPersistentID());
        else if (carts.getLast() == cartBase.getPersistentID())
            carts.addLast(cartNew.getPersistentID());
        else
            return;
        Train train = getTrainUnsafe(cartNew);
        if (train != null && train != this)
            train._removeCart(cartNew);
        addTrainTag(cartNew, this);
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean _removeCart(EntityMinecart cart) {
        boolean removed = _removeCart(cart.getPersistentID());
        if (removed && uuid.equals(getTrainUUID(cart))) {
            removeTrainTag(cart);
        }
        return removed;
    }

    private boolean _removeCart(UUID cart) {
        boolean removed = carts.remove(cart);
        if (removed) {
            if (carts.isEmpty()) {
                releaseTrain();
                trains.remove(getUUID());
            }
        }
        return removed;
    }

    public boolean containsCart(EntityMinecart cart) {
        return cart != null && carts.contains(cart.getPersistentID());
    }

    public boolean isTrainEnd(EntityMinecart cart) {
        return cart != null && getEnds().contains(cart.getPersistentID());
    }

    public Collection<UUID> getEnds() {
        Set<UUID> ends = new HashSet<UUID>();
        ends.add(carts.getFirst());
        ends.add(carts.getLast());
        return ends;
    }

    public EntityLocomotive getLocomotive() {
        LinkageManager lm = LinkageManager.instance();
        for (UUID id : getEnds()) {
            EntityMinecart cart = lm.getCartFromUUID(id);
            if (cart instanceof EntityLocomotive)
                return (EntityLocomotive) cart;
        }
        return null;
    }

    public <T extends EntityMinecart> Collection<T> getCarts(Class<T> cartClass) {
        List<T> list = Lists.newArrayList();
        for (EntityMinecart cart : this) {
            if (cartClass.isInstance(cart))
                list.add(cartClass.cast(cart));
        }
        return list;
    }

    public List<UUID> getUUIDs() {
        return safeCarts;
    }

    public int size() {
        return carts.size();
    }

    public boolean isEmpty() {
        return carts.isEmpty();
    }

    public int getNumRunningLocomotives() {
        int count = 0;
        for (EntityMinecart cart : this) {
            if (cart instanceof EntityLocomotive && ((EntityLocomotive) cart).isRunning())
                count++;
        }
        return count;
    }

    public void refreshMaxSpeed() {
        setMaxSpeed(getMaxSpeed());
    }

    public float getMaxSpeed() {
        float speed = 1.2F;
        int numLocomotives = getNumRunningLocomotives();
        for (EntityMinecart c : this) {
            float baseSpeed = c.getMaxCartSpeedOnRail();
            if (numLocomotives > 0 && !(c instanceof CartBaseEnergy) && c instanceof IElectricMinecart) {
                IElectricMinecart e = (IElectricMinecart) c;
                if (e.getChargeHandler().getType() != IElectricMinecart.ChargeHandler.Type.USER) {
                    baseSpeed = Math.min(0.2F, 0.03F + (numLocomotives - 1) * 0.075F);
                }
            }
            speed = Math.min(speed, baseSpeed);
        }
        return speed;
    }

    public void setMaxSpeed(float trainSpeed) {
        for (EntityMinecart c : this) {
            c.setCurrentCartSpeedCapOnRail(trainSpeed);
        }
    }

    public boolean isTrainLockedDown() {
        return !lockingTracks.isEmpty();
    }

    public void addLockingTrack(UUID track) {
        lockingTracks.add(track);
    }

    public void removeLockingTrack(UUID track) {
        lockingTracks.remove(track);
    }

    public boolean isIdle() {
        return trainState == TrainState.IDLE || isTrainLockedDown();
    }

    public boolean isStopped() {
        return trainState == TrainState.STOPPED;
    }

    public void setTrainState(TrainState state) {
        this.trainState = state;
    }

    public enum TrainState {

        STOPPED,
        IDLE,
        NORMAL
    }
}
