/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.google.common.collect.Lists;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin.EnumNBTType;
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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unused")
public final class Train implements Iterable<EntityMinecart> {
    public static final String TRAIN_NBT = "rcTrain";

    private final UUID uuid;
    private final Deque<UUID> carts;
    private final Collection<UUID> locks;
    private final World world;
    private final TrainInfo info;
    private final Collection<UUID> safeCarts;

    Train(EntityMinecart cart) {
        this(UUID.randomUUID(), new ArrayDeque<>(), new HashSet<>(), cart.world, TrainState.NORMAL);
        buildTrain(cart);
    }

    private Train(UUID uuid, Deque<UUID> carts, Set<UUID> locks, World world, TrainState state) {
        this(new TrainInfo(uuid, state, carts, locks), world);
    }

    Train(TrainInfo info, World world) {
        this.info = info;
        this.uuid = info.id;
        this.carts = info.carts;
        this.locks = info.locks;
        this.world = world;
        safeCarts = Collections.unmodifiableCollection(carts);
    }

    public static Map<UUID, Train> getTrainMap(World world) {
        return TrainManager.getInstance(world).trains;
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

    @Nullable
    private static Train getTrainUnsafe(@Nullable EntityMinecart cart) {
        if (cart == null)
            return null;
        return getTrainMap(cart.world).get(getTrainUUID(cart));
    }

    @Nullable
    public static UUID getTrainUUID(EntityMinecart cart) {
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

        return train1 != null && train1 == train2;
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

    @Nullable
    private static Train getLongestTrainUnsafe(EntityMinecart cart1, EntityMinecart cart2) {
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

//    @Nullable
//    public static Train getTrain(@Nullable UUID cartUUID) {
//        if (cartUUID == null)
//            return null;
//        EntityMinecart cart = CartTools.getCartFromUUID(cartUUID);
//        if (cart == null)
//            return null;
//        return getTrain(cart);
//    }

    public Stream<EntityMinecart> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Iterator<EntityMinecart> iterator() {
        LinkageManager lm = LinkageManager.instance();
        List<EntityMinecart> entities = new ArrayList<>(carts.size());
        for (UUID cart : carts) {
            EntityMinecart entity = CartTools.getCartFromUUID(world, cart);
            if (entity != null)
                entities.add(entity);
        }
        return entities.iterator();
    }

    private void buildTrain(EntityMinecart first) {
        resetTrain();
        buildTrain(null, first);
    }

    private void buildTrain(@Nullable EntityMinecart prev, EntityMinecart next) {
        addLinkInternal(prev, next);

        LinkageManager lm = LinkageManager.instance();
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

    protected void deleteTrain() {
        resetTrain();
        getTrainMap(world).remove(getUUID());
    }

    public static void deleteTrain(EntityMinecart cart) {
        Train train = getTrainMap(cart.world).remove(getTrainUUID(cart));
        removeTrainTag(cart);
        if (train != null)
            train.deleteTrain();
    }

    protected void resetTrain() {
        LinkageManager lm = LinkageManager.instance();
        for (UUID id : carts) {
            EntityMinecart cart = CartTools.getCartFromUUID(world, id);
            if (cart != null) {
                removeTrainTag(cart);
            }
        }
        carts.clear();
        locks.clear();
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
                deleteTrain();
            }
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

    @Nullable
    public EntityLocomotive getLocomotive() {
        LinkageManager lm = LinkageManager.instance();
        for (UUID id : getEnds()) {
            EntityMinecart cart = CartTools.getCartFromUUID(world, id);
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

    public Collection<UUID> getUUIDs() {
        return safeCarts;
    }

    @Nullable
    public IItemHandler getItemHandler() {
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

    @Nullable
    public IFluidHandler getFluidHandler() {
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
            //TODO remove this nonsense, if we keep it leave it somewhere else
//            if (numLocomotives > 0 && !(c instanceof CartBaseEnergy) && c.hasCapability(CapabilitiesCharge.CART_BATTERY, null)) {
//                ICartBattery battery = c.getCapability(CapabilitiesCharge.CART_BATTERY, null);
//                if (battery.getType() != ICartBattery.Type.USER) {
//                    baseSpeed = Math.min(0.2F, 0.03F + (numLocomotives - 1) * 0.075F);
//                }
//            }
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
    }

    public void removeLock(UUID lock) {
        locks.remove(lock);
    }

    public boolean isIdle() {
        return info.state == TrainState.IDLE || isTrainLockedDown();
    }

    public boolean isStopped() {
        return info.state == TrainState.STOPPED;
    }

    public void setTrainState(TrainState state) {
        this.info.state = state;
    }

    public enum TrainState {

        STOPPED,
        IDLE,
        NORMAL
    }

    TrainInfo getInfo() {
        return info;
    }

    static final class TrainInfo {

        UUID id;
        TrainState state;
        Deque<UUID> carts;
        Set<UUID> locks;

        TrainInfo() {
        }

        TrainInfo(UUID id, TrainState state, Deque<UUID> carts, Set<UUID> locks) {
            this.id = id;
            this.state = state;
            this.carts = carts;
            this.locks = locks;
        }

        void readFromNBT(NBTTagCompound tag) {
            id = NBTPlugin.readUUID(tag, "id");
            state = NBTPlugin.readEnumOrdinal(tag, "state", TrainState.values(), TrainState.NORMAL);
            carts = new ArrayDeque<>();
            for (NBTTagCompound each : NBTPlugin.<NBTTagCompound>getNBTList(tag, "carts", EnumNBTType.COMPOUND)) {
                carts.add(NBTUtil.getUUIDFromTag(each));
            }
            locks = new HashSet<>();
            for (NBTTagCompound each : NBTPlugin.<NBTTagCompound>getNBTList(tag, "locks", EnumNBTType.COMPOUND)) {
                locks.add(NBTUtil.getUUIDFromTag(each));
            }
        }

        void writeToNBT(NBTTagCompound tag) {
            NBTPlugin.writeUUID(tag, "id", id);
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

        Train build(World world) {
            return new Train(this, world);
        }
    }
}
