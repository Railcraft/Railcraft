/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import java.util.*;
import net.minecraft.entity.item.EntityMinecart;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Train implements Iterable<EntityMinecart> {

    public enum TrainState {

        STOPPED,
        IDLE,
        NORMAL
    }

    private final UUID uuid;
    private final Set<UUID> carts = new HashSet<UUID>();
    private final Set<UUID> safeSet = Collections.unmodifiableSet(carts);
    private final Set<UUID> lockingTracks = new HashSet<UUID>();
    private final Set<UUID> frontback = new HashSet<UUID>();
    private TrainState trainState = TrainState.NORMAL;

    public Train(EntityMinecart cart) {
        uuid = UUID.randomUUID();

        addCartsToTrain(cart);
    }

    @Override
    public Iterator<EntityMinecart> iterator() {
        return new Iterator<EntityMinecart>() {
            private final Iterator<UUID> it = carts.iterator();
            private final LinkageManager lm = LinkageManager.instance();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public EntityMinecart next() {
                return lm.getCartFromUUID(it.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removing not supported.");
            }

        };
    }

    public Iterable<EntityMinecart> orderedIteratable(final EntityMinecart head) {
        return new Iterable<EntityMinecart>() {
            @Override
            public Iterator<EntityMinecart> iterator() {
                return new Iterator<EntityMinecart>() {
                    private final LinkageManager lm = LinkageManager.instance();
                    private EntityMinecart last = null;
                    private EntityMinecart current = head;

                    @Override
                    public boolean hasNext() {
                        EntityMinecart next = lm.getLinkedCartA(current);
                        if (next != null && next != last)
                            return true;
                        next = lm.getLinkedCartB(current);
                        if (next != null && next != last)
                            return true;
                        return false;
                    }

                    @Override
                    public EntityMinecart next() {
                        EntityMinecart next = lm.getLinkedCartA(current);
                        if (next != last) {
                            last = current;
                            current = next;
                            return current;
                        }
                        next = lm.getLinkedCartB(current);
                        if (next != last) {
                            last = current;
                            current = next;
                            return current;
                        }
                        return null;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Removing not supported.");
                    }

                };
            }

        };
    }

    private void addCartsToTrain(EntityMinecart cart) {
        addCart(cart);

        LinkageManager lm = LinkageManager.instance();
        EntityMinecart linkA = lm.getLinkedCartA(cart);
        EntityMinecart linkB = lm.getLinkedCartB(cart);

        if (linkA == null)
            frontback.add(cart.getPersistentID());
        else if (!containsCart(linkA))
            addCartsToTrain(linkA);

        if (linkB == null)
            frontback.add(cart.getPersistentID());
        else if (!containsCart(linkB))
            addCartsToTrain(linkB);
    }

    protected void releaseTrain() {
        LinkageManager lm = LinkageManager.instance();
        for (UUID id : carts) {
            EntityMinecart cart = lm.getCartFromUUID(id);
            if (cart != null) {
                cart.getEntityData().removeTag(LinkageManager.TRAIN_HIGH);
                cart.getEntityData().removeTag(LinkageManager.TRAIN_LOW);
            }
        }
        carts.clear();
        lockingTracks.clear();
    }

    public UUID getUUID() {
        return uuid;
    }

    private void addCart(EntityMinecart cart) {
        LinkageManager.instance().resetTrain(cart);
        carts.add(cart.getPersistentID());
        cart.getEntityData().setLong(LinkageManager.TRAIN_HIGH, uuid.getMostSignificantBits());
        cart.getEntityData().setLong(LinkageManager.TRAIN_LOW, uuid.getLeastSignificantBits());
    }

    public boolean containsCart(EntityMinecart cart) {
        if (cart == null)
            return false;
        return carts.contains(cart.getPersistentID());
    }

    public boolean isTrainEnd(EntityMinecart cart) {
        if (cart == null)
            return false;
        return frontback.contains(cart.getPersistentID());
    }

    public EntityLocomotive getLocomotive() {
        LinkageManager lm = LinkageManager.instance();
        for (UUID id : frontback) {
            EntityMinecart cart = lm.getCartFromUUID(id);
            if (cart instanceof EntityLocomotive)
                return (EntityLocomotive) cart;
        }
        return null;
    }

    public Set<UUID> getCartsInTrain() {
        return safeSet;
    }

    public int size() {
        return carts.size();
    }

    public int getNumRunningLocomotives() {
        int count = 0;
        for (EntityMinecart cart : this) {
            if (cart instanceof EntityLocomotive && ((EntityLocomotive) cart).isRunning())
                count++;
        }
        return count;
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

    public static Train getTrain(EntityMinecart cart) {
        if (cart == null)
            return null;
        LinkageManager lm = LinkageManager.instance();
        return lm.getTrain(cart);
    }

}
