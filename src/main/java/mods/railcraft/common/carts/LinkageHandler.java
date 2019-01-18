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
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.modules.ModuleLocomotives;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.misc.Vec2D;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class LinkageHandler {
    public static final String LINK_A_TIMER = "linkA_timer";
    public static final String LINK_B_TIMER = "linkB_timer";
    public static final double LINK_DRAG = 0.95;
    public static final float MAX_DISTANCE = 8F;
    private static final float STIFFNESS = 0.7F;
    private static final float HS_STIFFNESS = 0.7F;
    //    private static final float TRANSFER = 0.15f;
    private static final float DAMPING = 0.4F;
    private static final float HS_DAMPING = 0.3F;
    private static final float FORCE_LIMITER = 6F;
    //    private static final int TICK_HISTORY = 200;
    private static LinkageHandler instance;
//    private static Map<EntityMinecart, CircularVec3Queue> history = new MapMaker().weakKeys().makeMap();

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
     * @param cart1 EntityMinecart
     * @param cart2 EntityMinecart
     * @return The optimal distance
     */
    private float getOptimalDistance(EntityMinecart cart1, EntityMinecart cart2) {
        float dist = 0;
        if (cart1 instanceof ILinkableCart)
            dist += ((ILinkableCart) cart1).getOptimalDistance(cart2);
        else
            dist += ILinkageManager.OPTIMAL_DISTANCE;
        if (cart2 instanceof ILinkableCart)
            dist += ((ILinkableCart) cart2).getOptimalDistance(cart1);
        else
            dist += ILinkageManager.OPTIMAL_DISTANCE;
        return dist;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean canCartBeAdjustedBy(EntityMinecart cart1, EntityMinecart cart2) {
        if (cart1 == cart2)
            return false;
        if (cart1 instanceof ILinkableCart && !((ILinkableCart) cart1).canBeAdjusted(cart2))
            return false;
        return !TrackToolsAPI.isCartLockedDown(cart1);
    }

    /**
     * This is where the physics magic actually gets performed. It uses Spring
     * Forces and Damping Forces to maintain a fixed distance between carts.
     *
     * @param cart1 EntityMinecart
     * @param cart2 EntityMinecart
     */
    protected void adjustVelocity(EntityMinecart cart1, EntityMinecart cart2, LinkageManager.LinkType linkType) {
        String timer = LINK_A_TIMER;
        if (linkType == LinkageManager.LinkType.LINK_B)
            timer = LINK_B_TIMER;
        if (cart1.world.provider.getDimension() != cart2.world.provider.getDimension()) {
            short count = cart1.getEntityData().getShort(timer);
            count++;
            if (count > 200) {
                LinkageManager.INSTANCE.breakLink(cart1, cart2);
                LinkageManager.printDebug("Reason For Broken Link: Carts in different dimensions.");
            }
            cart1.getEntityData().setShort(timer, count);
            return;
        }
        cart1.getEntityData().setShort(timer, (short) 0);

        double dist = cart1.getDistance(cart2);
        if (dist > MAX_DISTANCE) {
            LinkageManager.INSTANCE.breakLink(cart1, cart2);
            LinkageManager.printDebug("Reason For Broken Link: Max distance exceeded.");
            return;
        }

        boolean adj1 = canCartBeAdjustedBy(cart1, cart2);
        boolean adj2 = canCartBeAdjustedBy(cart2, cart1);

        Vec2D cart1Pos = new Vec2D(cart1);
        Vec2D cart2Pos = new Vec2D(cart2);

        Vec2D unit = Vec2D.unit(cart2Pos, cart1Pos);

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
//        stretch = Math.max(0.0, stretch);
//        if(Math.abs(stretch) > 0.5) {
//            stretch *= 2;
//        }

        boolean highSpeed = CartTools.isTravellingHighSpeed(cart1);

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

    private double limitForce(double force) {
        return Math.copySign(Math.min(Math.abs(force), FORCE_LIMITER), force);
    }

    /**
     * This function inspects the links and determines if any physics
     * adjustments need to be made.
     *
     * @param cart EntityMinecart
     */
    private void adjustCart(EntityMinecart cart) {
        if (isLaunched(cart))
            return;

        if (isOnElevator(cart))
            return;

        boolean linkedA = adjustLinkedCart(cart, LinkageManager.LinkType.LINK_A);
        boolean linkedB = adjustLinkedCart(cart, LinkageManager.LinkType.LINK_B);
        boolean linked = linkedA || linkedB;

        // Centroid
//        List<BlockPos> points = Train.streamCarts(cart).map(Entity::getPosition).collect(Collectors.toList());
//        Vec2D centroid = new Vec2D(MathTools.centroid(points));
//
//        Vec2D cartPos = new Vec2D(cart);
//        Vec2D unit = Vec2D.unit(cartPos, centroid);
//
//        double amount = 0.2;
//        double pushX = amount * unit.getX();
//        double pushZ = amount * unit.getY();
//
//        pushX = limitForce(pushX);
//        pushZ = limitForce(pushZ);
//
//        cart.motionX += pushX;
//        cart.motionZ += pushZ;

        // Drag
        if (linked && RailcraftModuleManager.isModuleEnabled(ModuleLocomotives.class) && !CartTools.isTravellingHighSpeed(cart)) {
            cart.motionX *= LINK_DRAG;
            cart.motionZ *= LINK_DRAG;
        }

        // Speed & End Drag
        Train.get(cart).ifPresent(train -> {
            if (train.isTrainEnd(cart)) {
                train.refreshMaxSpeed();
//                if (linked && !(cart instanceof EntityLocomotive)) {
//                    double drag = 0.97;
//                    cart.motionX *= drag;
//                    cart.motionZ *= drag;
//                }
            }
        });

    }

    private boolean adjustLinkedCart(EntityMinecart cart, LinkageManager.LinkType linkType) {
        boolean linked = false;
        LinkageManager lm = LinkageManager.INSTANCE;
        EntityMinecart link = lm.getLinkedCart(cart, linkType);
        if (link != null) {
            // sanity check to ensure links are consistent
            if (!lm.areLinked(cart, link)) {
                boolean success = lm.repairLink(cart, link);
                //TODO something should happen here
            }
            if (!isLaunched(link) && !isOnElevator(link)) {
                linked = true;
                adjustVelocity(cart, link, linkType);
//                adjustCartFromHistory(cart, link);
            }
        }
        return linked;
    }

//    /**
//     * Determines whether a cart is leading another.
//     *
//     * @param leader EntityMinecart
//     * @param follower EntityMinecart
//     * @return true if leader is leading follower
//     */
//    private boolean isCartLeading(EntityMinecart leader, EntityMinecart follower) {
//        return true; // magic goes here
//    }

//    /**
//     * Adjust the current cart's position based on the linked cart its following
//     * so that it follows the same path at a set distance.
//     *
//     * @param current EntityMinecart
//     * @param linked EntityMinecart
//     */
//    private void adjustCartFromHistory(EntityMinecart current, EntityMinecart linked) {
//        // If we are leading, we don't want to adjust anything
//        if (isCartLeading(current, linked))
//            return;
//
//        CircularVec3Queue leaderHistory = history.get(linked);
//
//        // Optimal distance is how far apart the carts should be
//        double optimalDist = getOptimalDistance(current, linked);
//        optimalDist *= optimalDist;
//
//        double currentDistance = linked.getDistanceSq(current);
//
//        // Search the history for the point closest to the optimal distance.
//        // There may be some issues with it choosing the wrong side of the cart.
//        // Probably needs some kind of logic to compare the distance from the
//        // new position to the current position and determine if its a valid position.
//        Vec3 closestPoint = null;
//        Vec3 linkedVec = new Vec3(linked.posX, linked.posY, linked.posZ);
//        double distance = Math.abs(optimalDist - currentDistance);
//        for (Vec3 pos : leaderHistory) {
//            double historyDistance = linkedVec.squareDistanceTo(pos);
//            double diff = Math.abs(optimalDist - historyDistance);
//            if (diff < distance) {
//                closestPoint = pos;
//                distance = diff;
//            }
//        }
//
//        // If we found a point closer to our desired distance, move us there
//        if (closestPoint != null)
//            current.setPosition(closestPoint.x, closestPoint.y, closestPoint.z);
//    }

//    /**
//     * Saved the position history of the cart every tick in a Circular Buffer.
//     *
//     * @param cart EntityMinecart
//     */
//    private void savePosition(EntityMinecart cart) {
//        CircularVec3Queue myHistory = history.get(cart);
//        if (myHistory == null) {
//            myHistory = new CircularVec3Queue(TICK_HISTORY);
//            history.put(cart, myHistory);
//        }
//        myHistory.add(cart.posX, cart.posY, cart.posZ);
//    }

    /**
     * This is our entry point, its triggered once per tick per cart.
     *
     * @param event MinecartUpdateEvent
     */
    @SubscribeEvent
    public void onMinecartUpdate(MinecartUpdateEvent event) {
        EntityMinecart cart = event.getMinecart();

        // Physics done here
        adjustCart(cart);

//        savePosition(cart);
    }

    public boolean isLaunched(EntityMinecart cart) {
        int launched = cart.getEntityData().getInteger("Launched");
        return launched > 0;
    }

    public boolean isOnElevator(EntityMinecart cart) {
        int elevator = cart.getEntityData().getByte("elevator");
        return elevator > 0;
    }

    @SubscribeEvent
    public void canMinecartTick(EntityEvent.CanUpdate event) {
        if (event.getEntity() instanceof EntityMinecart) {
            EntityMinecart cart = (EntityMinecart) event.getEntity();
            if (Train.streamCarts(cart).flatMap(Streams.toType(EntityCartWorldspike.class)).anyMatch(EntityCartWorldspike::hasActiveTicket)) {
                event.setCanUpdate(true);
            }
        }
    }
}
