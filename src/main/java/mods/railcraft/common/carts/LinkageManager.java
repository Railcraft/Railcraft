/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.ILinkableCart;
import mods.railcraft.api.carts.ILinkageManager;
import mods.railcraft.api.events.CartLinkEvent;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MathTools;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

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
public enum LinkageManager implements ILinkageManager {
    INSTANCE;
    public static final String AUTO_LINK_A = "rcAutoLinkA";
    public static final String AUTO_LINK_B = "rcAutoLinkB";
    public static final String LINK_A_HIGH = "rcLinkAHigh";
    public static final String LINK_A_LOW = "rcLinkALow";
    public static final String LINK_B_HIGH = "rcLinkBHigh";
    public static final String LINK_B_LOW = "rcLinkBLow";

    public static void printDebug(String msg, Object... args) {
        if (RailcraftConfig.printLinkingDebug())
            Game.log().msg(Level.DEBUG, msg, args);
    }

    /**
     * Returns the linkage id of the cart and adds the cart the linkage cache.
     *
     * @param cart The EntityMinecart
     * @return The linkage id
     */
    public UUID getLinkageId(EntityMinecart cart) {
        return cart.getPersistentID();
    }

    /**
     * Returns the square of the max distance two carts can be and still be
     * linkable.
     *
     * @param cart1 First Cart
     * @param cart2 Second Cart
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

    private void removeAutoLinks(EntityMinecart cart) {
        for (LinkType link : LinkType.VALUES) {
            cart.getEntityData().removeTag(link.autoLink);
        }
    }

    @Override
    public boolean setAutoLink(EntityMinecart cart, boolean autoLink) {
        if (autoLink) {
            boolean ret = false;
            for (LinkType link : LinkType.VALUES) {
                if (hasFreeLink(cart, link)) {
                    cart.getEntityData().setBoolean(link.autoLink, true);
                    ret = true;
                    printDebug("Cart {0}({1}) Set To Auto Link on Link {2} With First Collision.", getLinkageId(cart), cart.getDisplayName(), link);
                }
            }
            return ret;
        } else {
            removeAutoLinks(cart);
            return true;
        }
    }

    @Override
    public boolean hasAutoLink(EntityMinecart cart) {
        if (!hasFreeLink(cart)) // safety check
            removeAutoLinks(cart);
        return cart.getEntityData().getBoolean(AUTO_LINK_A) || cart.getEntityData().getBoolean(AUTO_LINK_B);
    }

    @Override
    public boolean tryAutoLink(EntityMinecart cart1, EntityMinecart cart2) {
        if ((hasAutoLink(cart1) || hasAutoLink(cart2))
                && createLink(cart1, cart2)) {
            printDebug("Automatically Linked Carts {0}({1}) and {2}({3}).", getLinkageId(cart1), cart1.getDisplayName(), getLinkageId(cart2), cart2.getDisplayName());
            return true;
        }
        return false;
    }

    /**
     * Returns true if there is nothing preventing the two carts from being
     * linked.
     *
     * @param cart1 First Cart
     * @param cart2 Second Cart
     * @return True if can be linked
     */
    private boolean canLinkCarts(EntityMinecart cart1, EntityMinecart cart2) {
        if (cart1 == cart2)
            return false;

        if (!hasFreeLink(cart1) || !hasFreeLink(cart2))
            return false;

        if (cart1 instanceof ILinkableCart) {
            if (!((ILinkableCart) cart1).canLink(cart2))
                return false;
        }

        if (cart2 instanceof ILinkableCart) {
            if (!((ILinkableCart) cart2).canLink(cart1))
                return false;
        }

        if (areLinked(cart1, cart2))
            return false;

        if (cart1.getDistanceSq(cart2) > getLinkageDistanceSq(cart1, cart2))
            return false;

        return !Train.areInSameTrain(cart1, cart2);
    }

    /**
     * Creates a link between two carts, but only if there is nothing preventing
     * such a link.
     *
     * @param cart1 First Cart
     * @param cart2 Second Cart
     * @return True if the link succeeded.
     */
    @Override
    public boolean createLink(EntityMinecart cart1, EntityMinecart cart2) {
        if (canLinkCarts(cart1, cart2)) {
            setLinkUnidirectional(cart1, cart2);
            setLinkUnidirectional(cart2, cart1);

            if (cart1 instanceof ILinkableCart)
                ((ILinkableCart) cart1).onLinkCreated(cart2);
            if (cart2 instanceof ILinkableCart)
                ((ILinkableCart) cart2).onLinkCreated(cart1);

            MinecraftForge.EVENT_BUS.post(new CartLinkEvent.Link(cart1, cart2));
            return true;
        }
        return false;
    }

    @Override
    public boolean hasFreeLink(EntityMinecart cart) {
        return Arrays.stream(LinkType.VALUES).anyMatch(link -> hasFreeLink(cart, link));
    }

    public boolean hasFreeLink(EntityMinecart cart, LinkType type) {
        if (!hasLink(cart, type)) {
            return false;
        }
        return MathTools.isNil(getLink(cart, type));
    }

    public boolean hasLink(EntityMinecart cart, LinkType linkType) {
        if (cart instanceof ILinkableCart) {
            ILinkableCart linkable = (ILinkableCart) cart;
            return linkable.isLinkable() && (linkType != LinkType.LINK_B || linkable.hasTwoLinks());
        }
        return true;
    }

    private boolean setLinkUnidirectional(EntityMinecart from, EntityMinecart to) {
        for (LinkType link : LinkType.VALUES) {
            if (hasFreeLink(from, link)) {
                setLinkUnidirectional(from, to, link);
                return true;
            }
        }
        return false;
    }

    // Note: returns a nil uuid (0) if the link does not exist
    public UUID getLink(EntityMinecart cart, LinkType linkType) {
        long high = cart.getEntityData().getLong(linkType.tagHigh);
        long low = cart.getEntityData().getLong(linkType.tagLow);
        return new UUID(high, low);
    }

    public UUID getLinkA(EntityMinecart cart) {
        return getLink(cart, LinkType.LINK_A);
    }

    public UUID getLinkB(EntityMinecart cart) {
        return getLink(cart, LinkType.LINK_B);
    }

    private void setLinkUnidirectional(EntityMinecart source, EntityMinecart target, LinkType linkType) {
        // hasFreeLink(source, linkType) checked
        UUID id = getLinkageId(target);
        source.getEntityData().setLong(linkType.tagHigh, id.getMostSignificantBits());
        source.getEntityData().setLong(linkType.tagLow, id.getLeastSignificantBits());
        source.getEntityData().removeTag(linkType.autoLink); // So we don't need to worry outside
    }

    /**
     * Returns the cart linked to LinkType A or null if nothing is currently
     * occupying LinkType A.
     *
     * @param cart The cart for which to get the link
     * @return The linked cart or null
     */
    @Override
    public @Nullable EntityMinecart getLinkedCartA(EntityMinecart cart) {
        return getLinkedCart(cart, LinkType.LINK_A);
    }

    /**
     * Returns the cart linked to LinkType B or null if nothing is currently
     * occupying LinkType B.
     *
     * @param cart The cart for which to get the link
     * @return The linked cart or null
     */
    @Override
    public @Nullable EntityMinecart getLinkedCartB(EntityMinecart cart) {
        return getLinkedCart(cart, LinkType.LINK_B);
    }

    public @Nullable EntityMinecart getLinkedCart(EntityMinecart cart, LinkType type) {
        return CartTools.getCartFromUUID(cart.world, getLink(cart, type));
    }

    /**
     * Returns true if the two carts are linked directly to each other.
     *
     * @param cart1 First Cart
     * @param cart2 Second Cart
     * @return True if linked
     */
    @Override
    public boolean areLinked(EntityMinecart cart1, EntityMinecart cart2) {
        return areLinked(cart1, cart2, true);
    }

    /**
     * Returns true if the two carts are linked directly to each other.
     *
     * @param cart1  First Cart
     * @param cart2  Second Cart
     * @param strict true if both carts should have linking data pointing to the other cart,
     *               false if its ok if only one cart has the data (this is technically an invalid state, but its been known to happen)
     * @return True if linked
     */
    public boolean areLinked(EntityMinecart cart1, EntityMinecart cart2, boolean strict) {
        if (cart1 == cart2)
            return false;

        UUID id1 = getLinkageId(cart1);
        UUID id2 = getLinkageId(cart2);
        boolean cart1Linked = id2.equals(getLinkA(cart1)) || id2.equals(getLinkB(cart1));
        boolean cart2Linked = id1.equals(getLinkA(cart2)) || id1.equals(getLinkB(cart2));

        if (cart1Linked != cart2Linked) {
            Game.log().msg(Game.DEVELOPMENT_VERSION ? Game.DEBUG_REPORT : Level.WARN,
                    "Linking discrepancy between carts {0}({1}) and {2}({3}): The first cart reports {4} for linked while the second one reports {5}!",
                    getLinkageId(cart1), cart1.getDisplayName(), getLinkageId(cart2), cart2.getDisplayName(), cart1Linked, cart2Linked);
        }

        if (strict)
            return cart1Linked && cart2Linked;
        else
            return cart1Linked || cart2Linked;
    }

    /**
     * Repairs an asymmetrical link between carts
     *
     * @param cart1 First Cart
     * @param cart2 Second Cart
     * @return true if the repair was successful.
     */
    public boolean repairLink(EntityMinecart cart1, EntityMinecart cart2) {
        boolean repaired = repairLinkUnidirectional(cart1, cart2) && repairLinkUnidirectional(cart2, cart1);
        if (repaired)
            Train.repairTrain(cart1, cart2);
        else
            breakLink(cart1, cart2);
        return repaired;
    }

    private boolean repairLinkUnidirectional(EntityMinecart from, EntityMinecart to) {
        UUID link = getLinkageId(to);

        return link.equals(getLinkA(from)) || link.equals(getLinkB(from)) || setLinkUnidirectional(from, to);
    }

    @Override
    public void breakLink(EntityMinecart one, EntityMinecart two) {
        LinkType linkOne = getLinkType(one, two);
        LinkType linkTwo = getLinkType(two, one);

        breakLinkInternal(one, two, linkOne, linkTwo);
    }

    @Override
    public void breakLinks(EntityMinecart cart) {
        breakLinkA(cart);
        breakLinkB(cart);
    }

    /**
     * Break only link A.
     *
     * @param cart Cart
     */
    private void breakLinkA(EntityMinecart cart) {
        EntityMinecart other = getLinkedCartA(cart);
        if (other == null) {
            return;
        }

        LinkType otherLink = getLinkType(other, cart);
        breakLinkInternal(cart, other, LinkType.LINK_A, otherLink);
    }

    /**
     * Break only link B.
     *
     * @param cart Cart
     */
    private void breakLinkB(EntityMinecart cart) {
        EntityMinecart other = getLinkedCartB(cart);
        if (other == null) {
            return;
        }

        LinkType otherLink = getLinkType(other, cart);
        breakLinkInternal(cart, other, LinkType.LINK_B, otherLink);
    }

    /**
     * Breaks a bidirectional link with all the arguments given.
     *
     * This has the most argument and tries to prevent a recursion.
     *
     * @param one     One of the carts given
     * @param two     The cart, given or calculated via a link
     * @param linkOne The link from one, given or calculated
     * @param linkTwo The link from two, calculated
     */
    private void breakLinkInternal(EntityMinecart one, EntityMinecart two, @Nullable LinkType linkOne, @Nullable LinkType linkTwo) {
        if ((linkOne == null) != (linkTwo == null)) {
            Game.log().msg(Game.DEVELOPMENT_VERSION ? Game.DEBUG_REPORT : Level.WARN,
                    "Linking discrepancy between carts {0}({1}) and {2}({3}): The first cart reports {4} for linked while the second one reports {5}!",
                    getLinkageId(one), one.getDisplayName(), getLinkageId(two), two.getDisplayName(), linkOne == null, linkTwo == null);
        }

        if (linkOne != null) {
            breakLinkUnidirectional(one, two, linkOne);
        }
        if (linkTwo != null) {
            breakLinkUnidirectional(two, one, linkTwo);
        }

        MinecraftForge.EVENT_BUS.post(new CartLinkEvent.Unlink(one, two));
    }

    private @Nullable LinkType getLinkType(EntityMinecart from, EntityMinecart to) {
        UUID linkTo = getLinkageId(to);
        return Arrays.stream(LinkType.VALUES)
                .filter(link -> linkTo.equals(getLink(from, link)))
                .findFirst().orElse(null);
    }

    private void breakLinkUnidirectional(EntityMinecart cart, EntityMinecart other, LinkType linkType) {
        removeLinkTags(cart, linkType);
        if (cart instanceof ILinkableCart)
            ((ILinkableCart) cart).onLinkBroken(other);

        printDebug("Cart {0}({1}) unidirectionally unlinked {2}({3}) at ({4}).", getLinkageId(cart), cart.getDisplayName(), getLinkageId(other), other, linkType.name());
    }

    private void removeLinkTags(EntityMinecart cart, LinkType linkType) {
        cart.getEntityData().removeTag(linkType.tagHigh);
        cart.getEntityData().removeTag(linkType.tagLow);
    }

    /**
     * Counts how many carts are in the train.
     *
     * @param cart Any cart in the train
     * @return The number of carts in the train
     */
    @Override
    public int countCartsInTrain(EntityMinecart cart) {
        return Train.get(cart).map(Train::size).orElse(1);
    }

    @Override
    public Stream<EntityMinecart> streamTrain(EntityMinecart cart) {
        return Train.streamCarts(cart);
    }

    public Iterable<EntityMinecart> linkIterator(final EntityMinecart start, final LinkType type) {
        if (MathTools.isNil(getLink(start, type))) {
            return Collections.emptyList();
        }
        return () -> new Iterator<EntityMinecart>() {
            private final LinkageManager lm = INSTANCE;
            private @Nullable EntityMinecart last;
            private @Nullable EntityMinecart next;
            private EntityMinecart current = start;

            /**
             * Calculates the next minecart. Returns null if it cannot find one.
             *
             * @return The next minecart to be returned by the iterator, or null
             */
            private @Nullable EntityMinecart calculateNext() {
                if (last == null) {
                    return lm.getLinkedCart(current, type);
                }
                EntityMinecart cartA = lm.getLinkedCartA(current);
                if (cartA != null && cartA != last)
                    return cartA;

                EntityMinecart cartB = lm.getLinkedCartB(current);
                return cartB == last ? null : cartB;
            }

            @Override
            public boolean hasNext() {
                if (next == null) {
                    next = calculateNext();
                }
                return next != null;
            }

            @Override
            public EntityMinecart next() {
                if (next == null) {
                    next = calculateNext();

                    if (next == null) {
                        throw new NoSuchElementException();
                    }
                }

                last = current;
                current = next;
                next = null;
                return current;
            }
        };
    }

    public enum LinkType {
        LINK_A(LINK_A_HIGH, LINK_A_LOW, AUTO_LINK_A),
        LINK_B(LINK_B_HIGH, LINK_B_LOW, AUTO_LINK_B);
        public static final LinkType[] VALUES = values();
        public final String tagHigh;
        public final String tagLow;
        public final String autoLink;

        LinkType(String tagHigh, String tagLow, String autoLink) {
            this.tagHigh = tagHigh;
            this.tagLow = tagLow;
            this.autoLink = autoLink;
        }
    }
}
