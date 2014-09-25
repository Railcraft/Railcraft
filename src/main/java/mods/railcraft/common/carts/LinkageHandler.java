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
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Map;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import mods.railcraft.api.carts.ILinkableCart;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.RailTools;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import mods.railcraft.common.util.collections.CircularVec3Queue;
import mods.railcraft.common.util.misc.Vec2D;
import net.minecraft.util.Vec3;

public class LinkageHandler {

    public static final String LINK_A_TIMER = "linkA_timer";
    public static final String LINK_B_TIMER = "linkB_timer";
    public static final double LINK_DRAG = 0.95;
    public static final float MAX_DISTANCE = 8f;
    private static final float STIFFNESS = 0.7f;
    private static final float HS_STIFFNESS = 0.7f;
//    private static final float TRANSFER = 0.15f;
    private static final float DAMPING = 0.4f;
    private static final float HS_DAMPING = 0.3f;
    private static final float FORCE_LIMITER = 6f;
    private static final int TICK_HISTORY = 200;
    private static LinkageHandler instance;
    private static Map<EntityMinecart, CircularVec3Queue> history = new MapMaker().weakKeys().makeMap();

    private LinkageHandler() {
    }

    public static LinkageHandler getInstance() {
        if (instance == null)
            instance = new LinkageHandler();
        return instance;
    }

    /**
     * Returns the optimal distance between two linked carts that the
     * LinkageHandler will attempt to maintain at all times.
     *
     * @param cart1
     * @param cart2
     * @return The optimal distance
     */
    private float getOptimalDistance(EntityMinecart cart1, EntityMinecart cart2) {
        float dist = 0;
        if (cart1 instanceof ILinkableCart)
            dist += ((ILinkableCart) cart1).getOptimalDistance(cart2);
        else
            dist += LinkageManager.OPTIMAL_DISTANCE;
        if (cart2 instanceof ILinkableCart)
            dist += ((ILinkableCart) cart2).getOptimalDistance(cart1);
        else
            dist += LinkageManager.OPTIMAL_DISTANCE;
        return dist;
    }

    private boolean canCartBeAdjustedBy(EntityMinecart cart1, EntityMinecart cart2) {
        if (cart1 == cart2)
            return false;
        if (cart1 instanceof ILinkableCart && !((ILinkableCart) cart1).canBeAdjusted(cart2))
            return false;
        if (RailTools.isCartLockedDown(cart1))
            return false;
        return true;
    }

    /**
     * This is where the physics magic actually gets performed. It uses Spring
     * Forces and Damping Forces to maintain a fixed distance between carts.
     *
     * @param cart1
     * @param cart2
     * @param link
     */
    protected void adjustVelocity(EntityMinecart cart1, EntityMinecart cart2, char link) {
        String timer = LINK_A_TIMER;
        if (link == 'B')
            timer = LINK_B_TIMER;
        if (cart1.worldObj.provider.dimensionId != cart2.worldObj.provider.dimensionId) {
            short count = cart1.getEntityData().getShort(timer);
            count++;
            if (count > 200) {
                LinkageManager.instance().breakLink(cart1, cart2);
                LinkageManager.printDebug("Reason For Broken Link: Carts in different dimensions.");
            }
            cart1.getEntityData().setShort(timer, count);
            return;
        }
        cart1.getEntityData().setShort(timer, (short) 0);

        double dist = cart1.getDistanceToEntity(cart2);
        if (dist > MAX_DISTANCE) {
            LinkageManager.instance().breakLink(cart1, cart2);
            LinkageManager.printDebug("Reason For Broken Link: Max distance exceeded.");
            return;
        }

        boolean adj1 = canCartBeAdjustedBy(cart1, cart2);
        boolean adj2 = canCartBeAdjustedBy(cart2, cart1);

        Vec2D cart1Pos = new Vec2D(cart1.posX, cart1.posZ);
        Vec2D cart2Pos = new Vec2D(cart2.posX, cart2.posZ);

        Vec2D unit = Vec2D.subtract(cart2Pos, cart1Pos);
        unit.normalize();

        // Energy transfer

//        double transX = TRANSFER * (cart2.motionX - cart1.motionX);
//        double transZ = TRANSFER * (cart2.motionZ - cart1.motionZ);
//
//        transX = limitForce(transX);
//        transZ = limitForce(transZ);
//
//        if(adj1) {
//            cart1.motionX += transX;
//            cart1.motionZ += transZ;
//        }
//
//        if(adj2) {
//            cart2.motionX -= transX;
//            cart2.motionZ -= transZ;
//        }

        // Spring force

        float optDist = getOptimalDistance(cart1, cart2);
        double stretch = dist - optDist;
//        if(Math.abs(stretch) > 0.5) {
//            stretch *= 2;
//        }

        boolean highSpeed = cart1.getEntityData().getBoolean("HighSpeed");

        double stiffness = highSpeed ? HS_STIFFNESS : STIFFNESS;
        double springX = stiffness * stretch * unit.getX();
        double springZ = stiffness * stretch * unit.getY();

        springX = limitForce(springX);
        springZ = limitForce(springZ);

        if (adj1) {
            cart1.motionX += springX;
            cart1.motionZ += springZ;
        }

        if (adj2) {
            cart2.motionX -= springX;
            cart2.motionZ -= springZ;
        }

        // Damping

        Vec2D cart1Vel = new Vec2D(cart1.motionX, cart1.motionZ);
        Vec2D cart2Vel = new Vec2D(cart2.motionX, cart2.motionZ);

        double dot = Vec2D.subtract(cart2Vel, cart1Vel).dotProduct(unit);

        double damping = highSpeed ? HS_DAMPING : DAMPING;
        double dampX = damping * dot * unit.getX();
        double dampZ = damping * dot * unit.getY();

        dampX = limitForce(dampX);
        dampZ = limitForce(dampZ);

        if (adj1) {
            cart1.motionX += dampX;
            cart1.motionZ += dampZ;
        }

        if (adj2) {
            cart2.motionX -= dampX;
            cart2.motionZ -= dampZ;
        }
    }

    private float getTrainMaxSpeed(EntityMinecart cart) {
        LinkageManager lm = LinkageManager.instance();
        EntityMinecart linkA = lm.getLinkedCartA(cart);
        EntityMinecart linkB = lm.getLinkedCartB(cart);

        float speed = cart.getMaxCartSpeedOnRail();

        float min1 = speed;
        min1 = Math.min(min1, getTrainMaxSpeedRecursive(linkA, cart));

        float min2 = speed;
        min2 = Math.min(min2, getTrainMaxSpeedRecursive(linkB, cart));


        return Math.min(min1, min2);
    }

    private float getTrainMaxSpeedRecursive(EntityMinecart cart, EntityMinecart prev) {
        if (cart == null)
            return Float.MAX_VALUE;
        LinkageManager lm = LinkageManager.instance();
        EntityMinecart linkA = lm.getLinkedCartA(cart);
        EntityMinecart linkB = lm.getLinkedCartB(cart);

        float speed = cart.getMaxCartSpeedOnRail();

        float min1 = speed;
        if (linkA != prev)
            min1 = Math.min(min1, getTrainMaxSpeedRecursive(linkA, cart));

        float min2 = speed;
        if (linkB != prev)
            min2 = Math.min(min2, getTrainMaxSpeedRecursive(linkB, cart));

        return Math.min(min1, min2);
    }

    private void setTrainMaxSpeed(EntityMinecart cart, float trainSpeed) {
        LinkageManager lm = LinkageManager.instance();
        EntityMinecart linkA = lm.getLinkedCartA(cart);
        EntityMinecart linkB = lm.getLinkedCartB(cart);

        setTrainMaxSpeedRecursive(linkA, cart, trainSpeed);

        setTrainMaxSpeedRecursive(linkB, cart, trainSpeed);

        cart.setCurrentCartSpeedCapOnRail(trainSpeed);
    }

    private void setTrainMaxSpeedRecursive(EntityMinecart cart, EntityMinecart prev, float trainSpeed) {
        if (cart == null)
            return;
        LinkageManager lm = LinkageManager.instance();
        EntityMinecart linkA = lm.getLinkedCartA(cart);
        EntityMinecart linkB = lm.getLinkedCartB(cart);

        if (linkA != prev)
            setTrainMaxSpeedRecursive(linkA, cart, trainSpeed);

        if (linkB != prev)
            setTrainMaxSpeedRecursive(linkB, cart, trainSpeed);

        cart.setCurrentCartSpeedCapOnRail(trainSpeed);
    }

    private double limitForce(double force) {
        return Math.copySign(Math.min(Math.abs(force), FORCE_LIMITER), force);
    }

    /**
     * This function inspects the links and determines if any physics
     * adjustments need to be made.
     *
     * @param cart
     * @param lm
     */
    private void adjustCart(EntityMinecart cart, LinkageManager lm) {
        int launched = cart.getEntityData().getInteger("Launched");
        if (launched > 0)
            return;

        if (isOnElevator(cart))
            return;

        boolean linked = false;

        EntityMinecart link_A = lm.getLinkedCartA(cart);
        if (link_A != null) {
            launched = link_A.getEntityData().getInteger("Launched");
            if (launched <= 0 && !isOnElevator(link_A)) {
                linked = true;
                adjustVelocity(cart, link_A, 'A');
                adjustCartFromHistory(cart, link_A);
            }
        }

        EntityMinecart link_B = lm.getLinkedCartB(cart);
        if (link_B != null) {
            launched = link_B.getEntityData().getInteger("Launched");
            if (launched <= 0 && !isOnElevator(link_B)) {
                linked = true;
                adjustVelocity(cart, link_B, 'B');
                adjustCartFromHistory(cart, link_B);
            }
        }

        if (linked && ModuleManager.isModuleLoaded(Module.LOCOMOTIVES)) {
            cart.motionX *= LINK_DRAG;
            cart.motionZ *= LINK_DRAG;
        }

        if (link_A == null && link_B != null || link_A != null && link_B == null) {
            float trainSpeed = getTrainMaxSpeed(cart);
            setTrainMaxSpeed(cart, trainSpeed);
        } else if (link_A == null && link_B == null)
            setTrainMaxSpeed(cart, 1.2f);
    }

    /**
     * Determines whether a cart is leading another.
     *
     * @param leader
     * @param follower
     * @return true if leader is leading follower
     */
    private boolean isCartLeading(EntityMinecart leader, EntityMinecart follower) {
        return true; // TODO: magic
    }

    /**
     * Adjust the current cart's position based on the linked cart its following
     * so that it follows the same path at a set distance.
     *
     * @param current
     * @param linked
     */
    private void adjustCartFromHistory(EntityMinecart current, EntityMinecart linked) {
        // If we are leading, we don't want to adjust anything
        if (isCartLeading(current, linked))
            return;

        CircularVec3Queue leaderHistory = history.get(linked);

        // Optimal distance is how far apart the carts should be
        double optimalDist = getOptimalDistance(current, linked);
        optimalDist *= optimalDist;

        double currentDistance = linked.getDistanceSqToEntity(current);

        // Search the history for the point closest to the optimal distance.
        // There may be some issues with it chosing the wrong side of the cart.
        // Probably needs some kind of logic to compare the distance from the 
        // new position to the current position and determine if its a valid position.
        Vec3 closestPoint = null;
        Vec3 linkedVec = Vec3.createVectorHelper(linked.posX, linked.posY, linked.posZ);
        double distance = Math.abs(optimalDist - currentDistance);
        for (Vec3 pos : leaderHistory) {
            double historyDistance = linkedVec.squareDistanceTo(pos);
            double diff = Math.abs(optimalDist - historyDistance);
            if (diff < distance) {
                closestPoint = pos;
                distance = diff;
            }
        }

        // If we found a point closer to our desired distance, move us there
        if (closestPoint != null)
            current.setPosition(closestPoint.xCoord, closestPoint.yCoord, closestPoint.zCoord);
    }

    /**
     * Saved the position history of the cart every tick in a Circular Buffer.
     *
     * @param cart
     */
    private void savePosition(EntityMinecart cart) {
        CircularVec3Queue myHistory = history.get(cart);
        if (myHistory == null) {
            myHistory = new CircularVec3Queue(TICK_HISTORY);
            history.put(cart, myHistory);
        }
        myHistory.add(cart.posX, cart.posY, cart.posZ);
    }

    /**
     * This is our entry point, its triggered once per tick per cart.
     *
     * @param event
     */
    @SubscribeEvent
    public void onMinecartUpdate(MinecartUpdateEvent event) {
        EntityMinecart cart = event.minecart;

        LinkageManager lm = LinkageManager.instance();

        if (cart.isDead) {
            lm.removeLinkageId(cart);
            return;
        }

        // Causes a link id cache store
        lm.getLinkageId(cart);

        // Physics done here
        adjustCart(cart, lm);

        savePosition(cart);
    }

    @SubscribeEvent
    public void onMinecartInteract(MinecartInteractEvent event) {
        EntityPlayer player = event.player;
        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IToolCrowbar)
            event.setCanceled(true);
    }

    private boolean isOnElevator(EntityMinecart cart) {
        int elevator = cart.getEntityData().getByte("elevator");
        return elevator > 0;
    }

}
