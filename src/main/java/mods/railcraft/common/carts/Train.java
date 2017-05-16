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
import com.google.common.collect.MapMaker;
import mods.railcraft.common.blocks.charge.CapabilityCartBattery;
import mods.railcraft.common.blocks.charge.ICartBattery;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unused")
public class Train implements Iterable<EntityMinecart> {
    public static final String TRAIN_NBT = "rcTrain";
    private static final Map<World, Map<UUID, Train>> trains = new MapMaker().weakKeys().makeMap();
    private final UUID uuid;
    private final LinkedList<UUID> carts = new LinkedList<UUID>();
    private final List<UUID> safeCarts = Collections.unmodifiableList(carts);
    private final Collection<UUID> lockingTracks = new HashSet<UUID>();
    private final World world;
    private TrainState trainState = TrainState.NORMAL;

    public Train(EntityMinecart cart) {
        uuid = UUID.randomUUID();
        world = cart.worldObj;

        buildTrain(cart);
    }

    public static Map<UUID, Train> getTrainMap(World world) {
        return trains.computeIfAbsent(world, k -> new HashMap<>());
    }

    public static Train getTrain(EntityMinecart cart) {
        Map<UUID, Train> trainMap = getTrainMap(cart.worldObj);
        Train train = trainMap.get(getTrainUUID(cart));
        if (train != null && (!train.containsCart(cart) || !train.isValid(cart.worldObj) || train.isEmpty())) {
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
        return getTrainMap(cart.worldObj).get(getTrainUUID(cart));
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

    public static void addTrainTag(EntityMinecart cart, Train train) {
        UUID trainId = train.getUUID();
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
        _addLink(prev, next);

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
        Train train = getTrainMap(cart.worldObj).remove(getTrainUUID(cart));
        if (train != null)
            train.deleteTrain();
        removeTrainTag(cart);
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
        lockingTracks.clear();
    }

    public UUID getUUID() {
        return uuid;
    }

    public void rebuild(EntityMinecart cart) {
        buildTrain(cart);
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
        Set<UUID> ends = new HashSet<UUID>();
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

    public List<UUID> getUUIDs() {
        return safeCarts;
    }

    @Nullable
    public IItemHandler getItemHandler() {
        ArrayList<IItemHandlerModifiable> cartHandlers = new ArrayList<>();
        for (EntityMinecart cart : this) {
            IItemHandler itemHandler = InvTools.getItemHandler(cart);
            if (itemHandler instanceof IItemHandlerModifiable)
                cartHandlers.add((IItemHandlerModifiable) itemHandler);
        }
        if (cartHandlers.isEmpty())
            return null;
        return new CombinedInvWrapper(cartHandlers.toArray(new IItemHandlerModifiable[cartHandlers.size()]));
    }

    @Nullable
    public IFluidHandler getFluidHandler() {
        ArrayList<IFluidHandler> cartHandlers = new ArrayList<>();
        for (EntityMinecart cart : this) {
            IFluidHandler fluidHandler = FluidTools.getFluidHandler(null, cart);
            if (fluidHandler != null)
                cartHandlers.add(fluidHandler);
        }
        if (cartHandlers.isEmpty())
            return null;
        return new FluidHandlerConcatenate(cartHandlers.toArray(new IFluidHandler[cartHandlers.size()]));
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
            if (numLocomotives > 0 && !(c instanceof CartBaseEnergy) && c.hasCapability(CapabilityCartBattery.CHARGE_CART_CAPABILITY, null)) {
                ICartBattery battery = c.getCapability(CapabilityCartBattery.CHARGE_CART_CAPABILITY, null);
                if (battery.getType() != ICartBattery.Type.USER) {
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
