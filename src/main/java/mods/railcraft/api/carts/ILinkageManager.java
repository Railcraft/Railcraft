/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.carts;

import net.minecraft.entity.item.EntityMinecart;

import java.util.UUID;

/**
 * The LinkageManager contains all the functions needed to link and interact
 * with linked carts.
 * <p/>
 * To obtain an instance of this interface, call CartTools.getLinkageManager().
 * <p/>
 * Each cart can up to two links. They are called Link A and Link B. Some carts
 * will have only Link A, for example the Tunnel Bore.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 * @see CartTools, ILinkableCart
 */
public interface ILinkageManager {

    /**
     * The default max distance at which carts can be linked, divided by 2.
     */
    public static final float LINKAGE_DISTANCE = 1.25f;
    /**
     * The default distance at which linked carts are maintained, divided by 2.
     */
    public static final float OPTIMAL_DISTANCE = 0.78f;

    boolean setAutoLink(EntityMinecart cart, boolean autoLink);

    boolean hasAutoLink(EntityMinecart cart);

    boolean tryAutoLink(EntityMinecart cart1, EntityMinecart cart2);

    /**
     * Creates a link between two carts, but only if there is nothing preventing
     * such a link.
     *
     * @param cart1
     * @param cart2
     * @return True if the link succeeded.
     */
    boolean createLink(EntityMinecart cart1, EntityMinecart cart2);

    boolean hasFreeLink(EntityMinecart cart);

    /**
     * Returns the cart linked to Link A or null if nothing is currently
     * occupying Link A.
     *
     * @param cart The cart for which to get the link
     * @return The linked cart or null
     */
    EntityMinecart getLinkedCartA(EntityMinecart cart);

    /**
     * Returns the cart linked to Link B or null if nothing is currently
     * occupying Link B.
     *
     * @param cart The cart for which to get the link
     * @return The linked cart or null
     */
    EntityMinecart getLinkedCartB(EntityMinecart cart);

    /**
     * Returns true if the two carts are linked to each other.
     *
     * @param cart1
     * @param cart2
     * @return True if linked
     */
    boolean areLinked(EntityMinecart cart1, EntityMinecart cart2);

    /**
     * Breaks a link between two carts, if any link exists.
     *
     * @param cart1
     * @param cart2
     */
    void breakLink(EntityMinecart cart1, EntityMinecart cart2);

    /**
     * Breaks all links the cart has.
     *
     * @param cart
     */
    void breakLinks(EntityMinecart cart);

    /**
     * Break only link A.
     *
     * @param cart
     */
    void breakLinkA(EntityMinecart cart);

    /**
     * Break only link B.
     *
     * @param cart
     */
    void breakLinkB(EntityMinecart cart);

    /**
     * Counts how many carts are in the train.
     *
     * @param cart Any cart in the train
     * @return The number of carts in the train
     */
    int countCartsInTrain(EntityMinecart cart);

    Iterable<EntityMinecart> getCartsInTrain(EntityMinecart cart);

    /**
     * Given a persistent Entity UUID, it will return a matching minecart,
     * assuming one is loaded in the world.
     * <p/>
     * The Mapping is stored in a Map<UUID, EntityMinecart> so its fairly fast.
     * <p/>
     * This would probably be better in CartTools, but
     * Railcraft really only uses it for linking and this was the
     * easiest way to expose it.
     *
     * @param id Persistent Entity UUID
     * @return A Minecart
     */
    EntityMinecart getCartFromUUID(UUID id);

}
