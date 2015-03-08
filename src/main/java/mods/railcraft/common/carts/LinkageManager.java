/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import com.google.common.collect.MapMaker;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkableCart;
import mods.railcraft.api.carts.ILinkageManager;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The LinkageManager contains all the functions needed to link and interacted
 * with linked carts.
 * <p/>
 * One concept if import is that of the Linkage Id. Every cart is given a unique
 * identifier by the LinkageManager the first time it encounters the cart.
 * <p/>
 * This identifier is stored in the entity's NBT data between world loads so
 * that links are persistent rather than transitory.
 * <p/>
 * Links are also stored in NBT data as an Integer value that contains the
 * Linkage Id of the cart it is linked to.
 * <p/>
 * Generally you can ignore most of this and use the functions that don't
 * require or return Linkage Ids.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LinkageManager implements ILinkageManager {

    public static final String LINK_A_HIGH = "rcLinkAHigh";
    public static final String LINK_A_LOW = "rcLinkALow";
    public static final String LINK_B_HIGH = "rcLinkBHigh";
    public static final String LINK_B_LOW = "rcLinkBLow";
    public static final String TRAIN_HIGH = "rcTrainHigh";
    public static final String TRAIN_LOW = "rcTrainLow";
    private final Map<UUID, EntityMinecart> carts = new MapMaker().weakValues().makeMap();
    private final Map<UUID, Train> trains = new HashMap<UUID, Train>();

    private LinkageManager() {
    }

    /**
     * Return an instance of the LinkageManager
     *
     * @return LinkageManager
     */
    public static LinkageManager instance() {
        return (LinkageManager) CartTools.linkageManager;
    }

    public static void printDebug(String msg, Object... args) {
        if (RailcraftConfig.printLinkingDebug())
            Game.log(Level.DEBUG, msg, args);
    }

    public static void reset() {
        CartTools.linkageManager = new LinkageManager();
    }

    /**
     * Removes a id:cart pairing from the linkage registry.
     * <p/>
     * You should not need to call this function ever, it is needed only by the
     * LinkageHandler (internal RailcraftProxy code) in order to clean up dead
     * links left by dead carts.
     *
     * @param cart The cart to remove
     */
    public void removeLinkageId(EntityMinecart cart) {
        carts.remove(getLinkageId(cart));
    }

    /**
     * Returns the linkage id of the cart and adds the cart the linkage cache.
     *
     * @param cart The EntityMinecart
     * @return The linkage id
     */
    public UUID getLinkageId(EntityMinecart cart) {
        UUID id = cart.getPersistentID();
        if (!cart.isDead)
            carts.put(id, cart);
        return id;
    }

    /**
     * Returns a minecart from a persistent UUID.
     *
     * @param id
     * @return
     */
    @Override
    public EntityMinecart getCartFromUUID(UUID id) {
        EntityMinecart cart = carts.get(id);
        if (cart != null && cart.isDead) {
            carts.remove(id);
            return null;
        }
        return carts.get(id);
    }

    /**
     * Returns the square of the max distance two carts can be and still be
     * linkable.
     *
     * @param cart1
     * @param cart2
     * @return The square of the linkage distance
     */
    private float getLinkageDistanceSq(EntityMinecart cart1, EntityMinecart cart2) {
        float dist = 0;
        if (cart1 instanceof ILinkableCart)
            dist += ((ILinkableCart) cart1).getLinkageDistance(cart2);
        else
            dist += LINKAGE_DISTANCE;
        if (cart2 instanceof ILinkableCart)
            dist += ((ILinkableCart) cart2).getLinkageDistance(cart1);
        else
            dist += LINKAGE_DISTANCE;
        return dist * dist;
    }

    /**
     * Returns true if there is nothing preventing the two carts from being
     * linked.
     *
     * @param cart1
     * @param cart2
     * @return True if can be linked
     */
    private boolean canLinkCarts(EntityMinecart cart1, EntityMinecart cart2) {
        if (cart1 == null || cart2 == null)
            return false;

        if (cart1 == cart2)
            return false;

        if (cart1 instanceof ILinkableCart) {
            ILinkableCart link = (ILinkableCart) cart1;
            if (!link.isLinkable() || !link.canLinkWithCart(cart2))
                return false;
        }

        if (cart2 instanceof ILinkableCart) {
            ILinkableCart link = (ILinkableCart) cart2;
            if (!link.isLinkable() || !link.canLinkWithCart(cart1))
                return false;
        }

        if (areLinked(cart1, cart2))
            return false;

        if (cart1.getDistanceSqToEntity(cart2) > getLinkageDistanceSq(cart1, cart2))
            return false;

        if (!hasFreeLink(cart1) || !hasFreeLink(cart2))
            return false;

        return true;
    }

    /**
     * Creates a link between two carts, but only if there is nothing preventing
     * such a link.
     *
     * @param cart1
     * @param cart2
     * @return True if the link succeeded.
     */
    @Override
    public boolean createLink(EntityMinecart cart1, EntityMinecart cart2) {
        if (canLinkCarts(cart1, cart2)) {
            setLink(cart1, cart2);
            setLink(cart2, cart1);

            if (cart1 instanceof ILinkableCart)
                ((ILinkableCart) cart1).onLinkCreated(cart2);
            if (cart2 instanceof ILinkableCart)
                ((ILinkableCart) cart2).onLinkCreated(cart1);

//            sendLinkInfo(cart1);
//            sendLinkInfo(cart2);
            return true;
        }
        return false;
    }

    private boolean hasFreeLink(EntityMinecart cart) {
        return getLinkedCartA(cart) == null || (hasLinkB(cart) && getLinkedCartB(cart) == null);
    }

    private boolean hasLinkB(EntityMinecart cart) {
        if (cart instanceof ILinkableCart)
            return ((ILinkableCart) cart).hasTwoLinks();
        return true;
    }

    private void setLink(EntityMinecart cart1, EntityMinecart cart2) {
        if (getLinkedCartA(cart1) == null)
            setLinkA(cart1, cart2);
        else if (hasLinkB(cart1) && getLinkedCartB(cart1) == null)
            setLinkB(cart1, cart2);
    }

    public UUID getLinkA(EntityMinecart cart) {
        long high = cart.getEntityData().getLong(LINK_A_HIGH);
        long low = cart.getEntityData().getLong(LINK_A_LOW);
        return new UUID(high, low);
    }

    private void setLinkA(EntityMinecart cart1, EntityMinecart cart2) {
        resetTrain(cart1);
        resetTrain(cart2);
        UUID id = getLinkageId(cart2);
        cart1.getEntityData().setLong(LINK_A_HIGH, id.getMostSignificantBits());
        cart1.getEntityData().setLong(LINK_A_LOW, id.getLeastSignificantBits());
    }

    /**
     * Returns the cart linked to Link A or null if nothing is currently
     * occupying Link A.
     *
     * @param cart The cart for which to get the link
     * @return The linked cart or null
     */
    @Override
    public EntityMinecart getLinkedCartA(EntityMinecart cart) {
        return getCartFromUUID(getLinkA(cart));
    }

    public UUID getLinkB(EntityMinecart cart) {
        long high = cart.getEntityData().getLong(LINK_B_HIGH);
        long low = cart.getEntityData().getLong(LINK_B_LOW);
        return new UUID(high, low);
    }

    private void setLinkB(EntityMinecart cart1, EntityMinecart cart2) {
        if (!hasLinkB(cart1))
            return;
        resetTrain(cart1);
        resetTrain(cart2);
        UUID id = getLinkageId(cart2);
        cart1.getEntityData().setLong(LINK_B_HIGH, id.getMostSignificantBits());
        cart1.getEntityData().setLong(LINK_B_LOW, id.getLeastSignificantBits());
    }

    /**
     * Returns the cart linked to Link B or null if nothing is currently
     * occupying Link B.
     *
     * @param cart The cart for which to get the link
     * @return The linked cart or null
     */
    @Override
    public EntityMinecart getLinkedCartB(EntityMinecart cart) {
        return getCartFromUUID(getLinkB(cart));
    }

    public Train getTrain(EntityMinecart cart) {
        if (cart == null)
            return null;
        Train train = trains.get(getTrainUUID(cart));
        if (train != null && (!train.containsCart(cart) || !train.isValid())) {
            train.releaseTrain();
            trains.remove(train.getUUID());
            train = null;
        }
        if (train == null) {
            train = new Train(cart);
            trains.put(train.getUUID(), train);
        }
        return train;
    }

    public Train getTrain(UUID cartUUID) {
        if (cartUUID == null)
            return null;
        EntityMinecart cart = getCartFromUUID(cartUUID);
        if (cart == null)
            return null;
        return getTrain(cart);
    }

    public UUID getTrainUUID(EntityMinecart cart) {
        long high = cart.getEntityData().getLong(TRAIN_HIGH);
        long low = cart.getEntityData().getLong(TRAIN_LOW);
        return new UUID(high, low);
    }

    public void resetTrain(EntityMinecart cart) {
        Train train = trains.remove(getTrainUUID(cart));
        if (train != null)
            train.releaseTrain();
    }

    public boolean areInSameTrain(EntityMinecart cart1, EntityMinecart cart2) {
        if (cart1 == null || cart2 == null)
            return false;
        if (cart1 == cart2)
            return true;

        return getTrain(cart1) == getTrain(cart2);
    }

    /**
     * Returns true if the two carts are linked directly to each other.
     *
     * @param cart1
     * @param cart2
     * @return True if linked
     */
    @Override
    public boolean areLinked(EntityMinecart cart1, EntityMinecart cart2) {
        if (cart1 == null || cart2 == null)
            return false;
        if (cart1 == cart2)
            return false;

//        System.out.println("cart2 id = " + getLinkageId(cart2));
//        System.out.println("cart2 A = " + getLinkA(cart2));
//        System.out.println("cart2 B = " + getLinkB(cart2));
//        System.out.println("cart1 id = " + getLinkageId(cart1));
//        System.out.println("cart1 A = " + getLinkA(cart1));
//        System.out.println("cart1 B = " + getLinkB(cart1));
        boolean cart1Linked = false;
        UUID id1 = getLinkageId(cart1);
        UUID id2 = getLinkageId(cart2);
        if (id2.equals(getLinkA(cart1)) || id2.equals(getLinkB(cart1)))
            cart1Linked = true; //            System.out.println("cart1 linked");

        boolean cart2Linked = false;
        if (id1.equals(getLinkA(cart2)) || id1.equals(getLinkB(cart2)))
            cart2Linked = true; //            System.out.println("cart2 linked");

        return cart1Linked && cart2Linked;
    }

    /**
     * Breaks a link between two carts, if any link exists.
     *
     * @param cart1
     * @param cart2
     */
    @Override
    public void breakLink(EntityMinecart cart1, EntityMinecart cart2) {
        UUID link = getLinkageId(cart2);
        if (link.equals(getLinkA(cart1)))
            breakLinkA(cart1);

        if (link.equals(getLinkB(cart1)))
            breakLinkB(cart1);
    }

    /**
     * Breaks all links the passed cart has.
     *
     * @param cart
     */
    @Override
    public void breakLinks(EntityMinecart cart) {
        breakLinkA(cart);
        breakLinkB(cart);
    }

    /**
     * Break only link A.
     *
     * @param cart
     */
    @Override
    public void breakLinkA(EntityMinecart cart) {
        resetTrain(cart);
        UUID link = getLinkA(cart);
        cart.getEntityData().setLong(LINK_A_HIGH, 0);
        cart.getEntityData().setLong(LINK_A_LOW, 0);
        EntityMinecart other = getCartFromUUID(link);
        if (other != null)
            breakLink(other, cart);
        if (cart instanceof ILinkableCart)
            ((ILinkableCart) cart).onLinkBroken(other);

        printDebug("Carts {0}({1}) and {2}({3}) unlinked (A).", getLinkageId(cart), cart, link, other != null ? other : "null");
    }

    /**
     * Break only link B.
     *
     * @param cart
     */
    @Override
    public void breakLinkB(EntityMinecart cart) {
        resetTrain(cart);
        UUID link = getLinkB(cart);
        cart.getEntityData().setLong(LINK_B_HIGH, 0);
        cart.getEntityData().setLong(LINK_B_LOW, 0);
        EntityMinecart other = getCartFromUUID(link);
        if (other != null)
            breakLink(other, cart);
        if (cart instanceof ILinkableCart)
            ((ILinkableCart) cart).onLinkBroken(other);

        printDebug("Carts {0}({1}) and {2}({3}) unlinked (B).", getLinkageId(cart), cart, link, other != null ? other : "null");
    }

    /**
     * Counts how many carts are in the train.
     *
     * @param cart Any cart in the train
     * @return The number of carts in the train
     */
    @Override
    public int countCartsInTrain(EntityMinecart cart) {
        Train train = getTrain(cart);
        return train.size();
    }

    @Override
    public Iterable<EntityMinecart> getCartsInTrain(EntityMinecart cart) {
        return getTrain(cart);
    }

}
