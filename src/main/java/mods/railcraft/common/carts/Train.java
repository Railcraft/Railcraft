/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.charge.CapabilitiesCharge;
import mods.railcraft.api.charge.IBatteryCart;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unused")
public final class Train implements Iterable<EntityMinecart> {
    public static final String TRAIN_NBT = "rcTrain";

    private final UUID uuid;
    private final LinkedList<UUID> carts = new LinkedList<>();
    private final List<UUID> safeCarts = Collections.unmodifiableList(carts);
    private final Set<UUID> locks = new HashSet<>();
    private @Nullable World world;
    private TrainState state;
    private boolean dirty = true;

    Train(EntityMinecart cart) {
        this(UUID.randomUUID(),
                TrainState.NORMAL,
                Collections.singleton(cart.getPersistentID()),
                Collections.emptySet());
        this.world = cart.world;
        rebuild(cart);
    }

    Train(UUID id, TrainState state, Collection<UUID> carts, Set<UUID> locks) {
        this.uuid = id;
        this.state = state;
        this.carts.addAll(carts);
        this.locks.addAll(locks);
    }

    private static TrainManager getManager(World world) {
        return TrainManager.forWorld(world);
    }

    public static Train getTrain(EntityMinecart cart) {
        TrainManager manager = getManager(cart.world);
        Train train = manager.get(getTrainUUID(cart));
        if (train != null) {
            if ((!train.contains(cart) || !train.isValid() || train.isEmpty())) {
                train.rebuild(cart);
            }
        }
        if (train == null) {
            train = new Train(cart);
            manager.add(train);
        }
        return train;
    }

    private static Optional<Train> getTrainUnsafe(@Nullable EntityMinecart cart) {
        if (cart == null)
            return Optional.empty();
        return Optional.ofNullable(getManager(cart.world).get(getTrainUUID(cart)));
    }

    public static @Nullable UUID getTrainUUID(EntityMinecart cart) {
        NBTTagCompound nbt = cart.getEntityData();
        return NBTPlugin.readUUID(nbt, TRAIN_NBT);
    }

    public static boolean areInSameTrain(@Nullable EntityMinecart cart1, @Nullable EntityMinecart cart2) {
        if (cart1 == null || cart2 == null)
            return false;
        if (cart1 == cart2)
            return true;

        UUID train1 = getTrainUUID(cart1);
        UUID train2 = getTrainUUID(cart2);

        return train1 != null && Objects.equals(train1, train2);
    }

    private static Optional<Train> getLongestTrainUnsafe(EntityMinecart cart1, EntityMinecart cart2) {
        Optional<Train> train1 = getTrainUnsafe(cart1);
        Optional<Train> train2 = getTrainUnsafe(cart2);

        if (train1.equals(train2))
            return train1;
        if (!train1.isPresent())
            return train2;
        if (!train2.isPresent())
            return train1;

        if (train1.get().size() >= train2.get().size())
            return train1;
        return train2;
    }

    public static void removeTrainTag(EntityMinecart cart) {
        cart.getEntityData().removeTag(TRAIN_NBT);
    }

    public void addTrainTag(EntityMinecart cart) {
        UUID trainId = getUUID();
        NBTPlugin.writeUUID(cart.getEntityData(), TRAIN_NBT, trainId);
    }

    void setWorld(World world) {
        this.world = world;
    }

    private @Nullable EntityMinecart getCart(UUID cartID) {
        if (world == null)
            return null;
        return CartTools.getCartFromUUID(world, cartID);
    }

    public void rebuild(EntityMinecart first) {
        forEach(Train::removeTrainTag);
        carts.clear();
        rebuild(null, first);
        markDirty();
    }

    private void rebuild(@Nullable EntityMinecart prev, EntityMinecart next) {
        if (prev == null || carts.getFirst() == prev.getPersistentID())
            carts.addFirst(next.getPersistentID());
        else if (carts.getLast() == prev.getPersistentID())
            carts.addLast(next.getPersistentID());
        else
            throw new RuntimeException("Something went horribly wrong in the linkage code!");

        deleteTrain(next);
        addTrainTag(next);

        LinkageManager lm = LinkageManager.INSTANCE;
        EntityMinecart linkA = lm.getLinkedCartA(next);
        EntityMinecart linkB = lm.getLinkedCartB(next);

        if (linkA != null && linkA != prev && !contains(linkA))
            rebuild(next, linkA);

        if (linkB != null && linkB != prev && !contains(linkB))
            rebuild(next, linkB);
    }

    private boolean isValid() {
        return carts.stream().allMatch(this::isCartValid);
    }

    private boolean isCartValid(UUID cartID) {
        EntityMinecart cart = getCart(cartID);
        return cart != null && uuid.equals(getTrainUUID(cart));
    }

    public static void repairTrain(EntityMinecart cart1, EntityMinecart cart2) {
        getLongestTrainUnsafe(cart1, cart2).ifPresent(t -> t.rebuild(cart1));
    }

    public static void deleteTrain(EntityMinecart cart) {
//        Game.log(Level.WARN, "Thread: " + Thread.currentThread().getName());
        getManager(cart.world).remove(getTrainUUID(cart)).ifPresent(Train::resetTrain);
        removeTrainTag(cart);
    }

    private void resetTrain() {
        forEach(Train::removeTrainTag);
        carts.clear();
        locks.clear();
        markDirty();
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isPassenger(Entity entity) {
        return stream().anyMatch(c -> c.isPassenger(entity));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(@Nullable EntityMinecart cart) {
        return cart != null && carts.contains(cart.getPersistentID());
    }

    public boolean isTrainEnd(@Nullable EntityMinecart cart) {
        return cart != null && getEnds().contains(cart.getPersistentID());
    }

    public Collection<UUID> getEnds() {
        Set<UUID> ends = new HashSet<>();
        if (!carts.isEmpty()) {
            ends.add(carts.getFirst());
            ends.add(carts.getLast());
        }
        return ends;
    }

    public @Nullable EntityLocomotive getHeadLocomotive() {
        return getEnds().stream()
                .map(this::getCart)
                .flatMap(Streams.toType(EntityLocomotive.class))
                .findFirst().orElse(null);
    }

    public Stream<EntityMinecart> stream() {
        return safeCarts.stream()
                .map(this::getCart)
                .filter(Objects::nonNull);
    }

    public <T extends EntityMinecart> Stream<T> stream(Class<T> cartClass) {
        return stream().flatMap(Streams.toType(cartClass));
    }

    @Override
    public Iterator<EntityMinecart> iterator() {
        return stream().iterator();
    }

    public int getNumRunningLocomotives() {
        return (int) stream(EntityLocomotive.class).filter(EntityLocomotive::isRunning).count();
    }

    public <T extends EntityMinecart> List<T> getCarts(Class<T> cartClass) {
        return stream(cartClass).collect(Collectors.toList());
    }

    public List<UUID> getUUIDs() {
        return safeCarts;
    }

    public @Nullable IItemHandler getItemHandler() {
        List<IItemHandlerModifiable> cartHandlers = stream()
                .map(InvTools::getItemHandler)
                .flatMap(Streams.toType(IItemHandlerModifiable.class))
                .collect(Collectors.toList());
        if (cartHandlers.isEmpty())
            return null;
        return new CombinedInvWrapper(cartHandlers.toArray(new IItemHandlerModifiable[0]));
    }

    public @Nullable IFluidHandler getFluidHandler() {
        List<IFluidHandler> cartHandlers = stream()
                .map(FluidTools::getFluidHandler)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (cartHandlers.isEmpty())
            return null;
        return new FluidHandlerConcatenate(cartHandlers);
    }

    public int size() {
        return carts.size();
    }

    public boolean isEmpty() {
        return carts.isEmpty();
    }

    public void refreshMaxSpeed() {
        setMaxSpeed(getMaxSpeed());
    }

    public float getMaxSpeed() {
        float speed = 1.2F;
        int numLocomotives = getNumRunningLocomotives();
        for (EntityMinecart c : this) {
            float baseSpeed = c.getMaxCartSpeedOnRail();
            if (numLocomotives > 0 && !(c instanceof CartBaseEnergy) && c.hasCapability(CapabilitiesCharge.CART_BATTERY, null)) {
                IBatteryCart battery = c.getCapability(CapabilitiesCharge.CART_BATTERY, null);
                if (battery != null && battery.getType() != IBatteryCart.Type.USER) {
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
        return !locks.isEmpty();
    }

    public void addLock(UUID lock) {
        locks.add(lock);
        markDirty();
    }

    public void removeLock(UUID lock) {
        locks.remove(lock);
        markDirty();
    }

    public boolean isIdle() {
        return state == TrainState.IDLE || isTrainLockedDown();
    }

    public boolean isStopped() {
        return state == TrainState.STOPPED;
    }

    public void setTrainState(TrainState state) {
        if (this.state != state) {
            this.state = state;
            markDirty();
        }
    }

    public enum TrainState {

        STOPPED,
        IDLE,
        NORMAL
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Train)) {
            return false;
        }
        Train other = (Train) obj;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    static @Nullable Train readFromNBT(NBTTagCompound tag) {
        UUID id = NBTPlugin.readUUID(tag, "id");
        if (id == null)
            return null;
        TrainState state = NBTPlugin.readEnumOrdinal(tag, "state", TrainState.values(), TrainState.NORMAL);
        List<UUID> carts = NBTPlugin.getNBTList(tag, "carts", NBTTagCompound.class).stream().map(NBTUtil::getUUIDFromTag).collect(Collectors.toList());
        Set<UUID> locks = NBTPlugin.getNBTList(tag, "locks", NBTTagCompound.class).stream().map(NBTUtil::getUUIDFromTag).collect(Collectors.toSet());
        return new Train(id, state, carts, locks);
    }

    void writeToNBT(NBTTagCompound tag) {
        NBTPlugin.writeUUID(tag, "id", uuid);
        NBTPlugin.writeEnumOrdinal(tag, "state", state);
        NBTTagList listTag = new NBTTagList();
        for (UUID uuid : carts) {
            listTag.appendTag(NBTUtil.createUUIDTag(uuid));
        }
        tag.setTag("carts", listTag);

        NBTTagList locks = new NBTTagList();
        for (UUID uuid : this.locks) {
            locks.appendTag(NBTUtil.createUUIDTag(uuid));
        }
        tag.setTag("locks", locks);
    }

    void markDirty() {
        setDirty(true);
    }

    boolean isDirty() {
        return dirty;
    }

    void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
