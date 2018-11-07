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
import mods.railcraft.common.util.misc.Game;
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
    private final World world;
    private TrainState state;
    private boolean dirty = true;

    Train(EntityMinecart cart) {
        this(UUID.randomUUID(),
                TrainState.NORMAL,
                Collections.singleton(cart.getPersistentID()),
                Collections.emptySet(),
                cart.world);
        buildTrain(cart);
    }

    Train(UUID id, TrainState state, Collection<UUID> carts, Set<UUID> locks, World world) {
        this.uuid = id;
        this.state = state;
        this.carts.addAll(carts);
        this.locks.addAll(locks);
        this.world = world;
        if (Game.DEVELOPMENT_ENVIRONMENT && TrainManager.forWorld(world).trains().containsKey(id)) {
            throw new RuntimeException("Duplicate trains, things will be broken!");
        }
    }

    public static Map<UUID, Train> getTrainMap(World world) {
        return TrainManager.forWorld(world).trains();
    }

    public static Train getTrain(EntityMinecart cart) {
        Map<UUID, Train> trainMap = getTrainMap(cart.world);
        Train train = trainMap.get(getTrainUUID(cart));
        if (train != null && (!train.containsCart(cart) || !train.isValid(cart.world) || train.isEmpty())) {
            train.buildTrain(cart);
        }
        if (train == null) {
            train = new Train(cart);
            trainMap.put(train.getUUID(), train);
        }
        return train;
    }

    private static @Nullable Train getTrainUnsafe(@Nullable EntityMinecart cart) {
        if (cart == null)
            return null;
        return getTrainMap(cart.world).get(getTrainUUID(cart));
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

    public static Train getLongestTrain(EntityMinecart cart1, EntityMinecart cart2) {
        Train train1 = getTrain(cart1);
        Train train2 = getTrain(cart2);

        if (train1 == train2)
            return train1;
        if (train1.size() >= train2.size())
            return train1;
        return train2;
    }

    private static @Nullable Train getLongestTrainUnsafe(EntityMinecart cart1, EntityMinecart cart2) {
        Train train1 = getTrainUnsafe(cart1);
        Train train2 = getTrainUnsafe(cart2);

        if (train1 == train2)
            return train1;
        if (train1 == null)
            return train2;
        if (train2 == null)
            return train1;

        if (train1.size() >= train2.size())
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

    public Stream<EntityMinecart> stream() {
        return carts.stream()
                .map(cart -> CartTools.getCartFromUUID(world, cart))
                .filter(Objects::nonNull);
    }

    @Override
    public Iterator<EntityMinecart> iterator() {
        return stream().iterator();
    }

    private void buildTrain(EntityMinecart first) {
        resetTrain();
        buildTrain(null, first);
    }

    private void buildTrain(@Nullable EntityMinecart prev, EntityMinecart next) {
        addLinkInternal(prev, next);

        LinkageManager lm = LinkageManager.INSTANCE;
        EntityMinecart linkA = lm.getLinkedCartA(next);
        EntityMinecart linkB = lm.getLinkedCartB(next);

        if (linkA != null && linkA != prev && !containsCart(linkA))
            buildTrain(next, linkA);

        if (linkB != null && linkB != prev && !containsCart(linkB))
            buildTrain(next, linkB);
    }

    private boolean isValid(World world) {
        return carts.stream().allMatch(id -> isCartValid(world, id));
    }

    private boolean isCartValid(World world, UUID cartId) {
        EntityMinecart cart = CartTools.getCartFromUUID(world, cartId);
        return cart != null && uuid.equals(getTrainUUID(cart));
    }

    public static void repairTrain(EntityMinecart cart1, EntityMinecart cart2) {
        Train train = getLongestTrainUnsafe(cart1, cart2);
        if (train != null)
            train.buildTrain(cart1);
    }

    public static void deleteTrain(EntityMinecart cart) {
        Train train = getTrainMap(cart.world).remove(getTrainUUID(cart));
        removeTrainTag(cart);
        if (train != null)
            train.resetTrain();
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

    public void rebuild(EntityMinecart cart) {
        buildTrain(cart);
    }

    private void addLinkInternal(@Nullable EntityMinecart cartBase, EntityMinecart cartNew) {
        if (cartBase == null || carts.getFirst() == cartBase.getPersistentID())
            carts.addFirst(cartNew.getPersistentID());
        else if (carts.getLast() == cartBase.getPersistentID())
            carts.addLast(cartNew.getPersistentID());
        else
            return;
        Train train = getTrainUnsafe(cartNew);
        if (train != this && train != null)
            train.removeCartInternal(cartNew);
        addTrainTag(cartNew);
        markDirty();
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean removeCartInternal(EntityMinecart cart) {
        boolean removed = removeCartId(cart.getPersistentID());
        if (removed && uuid.equals(getTrainUUID(cart))) {
            removeTrainTag(cart);
        }
        return removed;
    }

    private boolean removeCartId(UUID cart) {
        boolean removed = carts.remove(cart);
        if (removed) {
            if (carts.isEmpty()) {
                getTrainMap(world).remove(getUUID());
                TrainManager.forWorld(world).data.markDirty();
                resetTrain();
            }
            markDirty();
        }
        return removed;
    }

    public boolean isPassenger(Entity entity) {
        return stream().anyMatch(c -> c.isPassenger(entity));
    }

    public boolean containsCart(@Nullable EntityMinecart cart) {
        return cart != null && carts.contains(cart.getPersistentID());
    }

    public boolean isTrainEnd(@Nullable EntityMinecart cart) {
        return cart != null && getEnds().contains(cart.getPersistentID());
    }

    public Collection<UUID> getEnds() {
        Set<UUID> ends = new HashSet<>();
        ends.add(carts.getFirst());
        ends.add(carts.getLast());
        return ends;
    }

    public @Nullable EntityLocomotive getLocomotive() {
        LinkageManager lm = LinkageManager.INSTANCE;
        return (EntityLocomotive) getEnds().stream()
                .map(id -> CartTools.getCartFromUUID(world, id))
                .filter(cart -> cart instanceof EntityLocomotive)
                .findFirst().orElse(null);
    }

    public <T extends EntityMinecart> List<T> getCarts(Class<T> cartClass) {
        return stream().flatMap(Streams.toType(cartClass)).collect(Collectors.toList());
    }

    public List<UUID> getUUIDs() {
        return safeCarts;
    }

    public @Nullable IItemHandler getItemHandler() {
        List<IItemHandlerModifiable> cartHandlers = new ArrayList<>();
        for (EntityMinecart cart : this) {
            IItemHandler itemHandler = InvTools.getItemHandler(cart);
            if (itemHandler instanceof IItemHandlerModifiable)
                cartHandlers.add((IItemHandlerModifiable) itemHandler);
        }
        if (cartHandlers.isEmpty())
            return null;
        return new CombinedInvWrapper(cartHandlers.toArray(new IItemHandlerModifiable[0]));
    }

    public @Nullable IFluidHandler getFluidHandler() {
        List<IFluidHandler> cartHandlers = new ArrayList<>();
        for (EntityMinecart cart : this) {
            IFluidHandler fluidHandler = FluidTools.getFluidHandler(null, cart);
            if (fluidHandler != null)
                cartHandlers.add(fluidHandler);
        }
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
            if (numLocomotives > 0 && !(c instanceof CartBaseEnergy) && c.hasCapability(CapabilitiesCharge.CART_BATTERY, null)) {
                IBatteryCart battery = c.getCapability(CapabilitiesCharge.CART_BATTERY, null);
                if (battery.getType() != IBatteryCart.Type.USER) {
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
        return world.provider.getDimension() == other.world.provider.getDimension() && uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return world.provider.getDimension() ^ uuid.hashCode();
    }

    static @Nullable Train readFromNBT(NBTTagCompound tag, World world) {
        UUID id = NBTPlugin.readUUID(tag, "id");
        if (id == null)
            return null;
        TrainState state = NBTPlugin.readEnumOrdinal(tag, "state", TrainState.values(), TrainState.NORMAL);
        List<UUID> carts = NBTPlugin.getNBTList(tag, "carts", NBTTagCompound.class).stream().map(NBTUtil::getUUIDFromTag).collect(Collectors.toList());
        Set<UUID> locks = NBTPlugin.getNBTList(tag, "locks", NBTTagCompound.class).stream().map(NBTUtil::getUUIDFromTag).collect(Collectors.toSet());
        return new Train(id, state, carts, locks, world);
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
