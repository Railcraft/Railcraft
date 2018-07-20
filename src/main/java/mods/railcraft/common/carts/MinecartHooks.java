/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.carts.ILinkageManager;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.behaivor.HighSpeedTools;
import mods.railcraft.common.blocks.tracks.elevator.BlockTrackElevator;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.EntitySearcher;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.IMinecartCollisionHandler;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.jetbrains.annotations.Nullable;
import java.util.List;

import static mods.railcraft.common.util.inventory.InvTools.dec;
import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

public final class MinecartHooks implements IMinecartCollisionHandler {
    // --Commented out by Inspection (3/13/2016 2:18 PM):protected static float DRAG_FACTOR_GROUND = 0.5f;
    // --Commented out by Inspection (3/13/2016 2:18 PM):protected static float DRAG_FACTOR_AIR = 0.99999f;
    private static final float OPTIMAL_DISTANCE = 1.28f;
    //    protected static float OPTIMAL_DISTANCE_PLAYER = 1.8f;
    private static final float COEF_SPRING = 0.2f;
    private static final float COEF_SPRING_PLAYER = 0.5f;
    private static final float COEF_RESTITUTION = 0.2f;
    private static final float COEF_DAMPING = 0.4f;
    // --Commented out by Inspection (3/13/2016 2:18 PM):protected static float ENTITY_REDUCTION = 0.25f;
    private static final float CART_LENGTH = 1.22f;
    private static final float CART_WIDTH = 0.98f;
    private static final float COLLISION_EXPANSION = 0.2f;
    private static final int MAX_INTERACT_DIST_SQ = 5 * 5;
    private static MinecartHooks instance;

    private MinecartHooks() {
    }

    public static MinecartHooks getInstance() {
        if (instance == null)
            instance = new MinecartHooks();
        return instance;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = player.world;
        if (Game.isClient(world))
            return;

        ItemStack itemStack = event.getItemStack();
        if (!isEmpty(itemStack)) {
            Item item = itemStack.getItem();
            if (CartTools.vanillaCartItemMap.containsKey(item)) {
                event.setUseItem(Event.Result.DENY);
                EntityMinecart placedCart = CartTools.placeCart(
                        CartTools.vanillaCartItemMap.get(item),
                        player.getGameProfile(), itemStack, world,
                        event.getPos());
                if (placedCart != null && !player.capabilities.isCreativeMode)
                    dec(itemStack);
            }
        }
    }

    @Override
    public void onEntityCollision(EntityMinecart cart, Entity other) {
        if (Game.isClient(cart.world) || cart.isPassenger(other) || !other.isEntityAlive() || !cart.isEntityAlive())
            return;

        ILinkageManager lm = LinkageManager.instance();
        EntityMinecart link = lm.getLinkedCartA(cart);
        if (link != null && (link == other || link.isPassenger(other)))
            return;
        link = lm.getLinkedCartB(cart);
        if (link != null && (link == other || link.isPassenger(other)))
            return;

        boolean isLiving = other instanceof EntityLivingBase;
        boolean isPlayer = other instanceof EntityPlayer;

        //TODO: needs more thought in regards to passenger handling
        if (isLiving && !isPlayer && cart.canBeRidden() && !(other instanceof EntityIronGolem)
                && cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0.001D
                && !cart.isBeingRidden() && !other.isRiding()) {
            int mountPrevention = cart.getEntityData().getInteger("MountPrevention");
            if (mountPrevention <= 0)
                other.startRiding(cart);
        }

        if (isLiving && WorldPlugin.isBlockAt(cart.world, cart.getPosition(), RailcraftBlocks.TRACK_ELEVATOR.block()))
            return;

//        System.out.println(cart.getClass().getSimpleName() + ": " + cart.entityId + " collided with " + other.getClass().getSimpleName() + ": " + other.entityId);
        Vec2D cartPos = new Vec2D(cart.posX, cart.posZ);
        Vec2D otherPos = new Vec2D(other.posX, other.posZ);

        Vec2D unit = Vec2D.subtract(otherPos, cartPos);
        unit.normalize();

        double distance = cart.getDistance(other);
        double depth = distance - OPTIMAL_DISTANCE;

        double forceX = 0;
        double forceZ = 0;

        if (depth < 0) {
            double spring = isPlayer ? COEF_SPRING_PLAYER : COEF_SPRING;
            double penaltyX = spring * depth * unit.getX();
            double penaltyZ = spring * depth * unit.getY();

            forceX += penaltyX;
            forceZ += penaltyZ;

            if (!isPlayer) {
                double impulseX = unit.getX();
                double impulseZ = unit.getY();
                impulseX *= -(1.0 + COEF_RESTITUTION);
                impulseZ *= -(1.0 + COEF_RESTITUTION);

                Vec2D cartVel = new Vec2D(cart.motionX, cart.motionZ);
                Vec2D otherVel = new Vec2D(other.motionX, other.motionZ);

                double dot = Vec2D.subtract(otherVel, cartVel).dotProduct(unit);

                impulseX *= dot;
                impulseZ *= dot;
                impulseX *= 0.5;
                impulseZ *= 0.5;

                forceX -= impulseX;
                forceZ -= impulseZ;
            }
        }

        if (other instanceof EntityMinecart) {
            EntityMinecart otherCart = (EntityMinecart) other;
            if (!cart.isPoweredCart() || otherCart.isPoweredCart())
                if (!TrackToolsAPI.isCartLockedDown(cart))
                    cart.addVelocity(forceX, 0, forceZ);
            if (!otherCart.isPoweredCart() || cart.isPoweredCart())
                if (!TrackToolsAPI.isCartLockedDown(otherCart))
                    other.addVelocity(-forceX, 0, -forceZ);
        } else {
//            if(isPlayer) {
//                forceX += Math.abs(cart.motionX - other.motionX) / 2;
//                forceZ += Math.abs(cart.motionZ - other.motionZ) / 2;
//            }
//            System.out.printf("forceX=%f, forceZ=%f%n", forceX, forceZ);
            Vec2D cartVel = new Vec2D(cart.motionX + forceX, cart.motionZ + forceZ);
            Vec2D otherVel = new Vec2D(other.motionX - forceX, other.motionZ - forceZ);

            double dot = Vec2D.subtract(otherVel, cartVel).dotProduct(unit);

            double dampX = COEF_DAMPING * dot * unit.getX();
            double dampZ = COEF_DAMPING * dot * unit.getY();

            forceX += dampX;
            forceZ += dampZ;

//            System.out.printf("dampX=%f, dampZ=%f%n", dampX, dampZ);
            if (!isPlayer)
                other.addVelocity(-forceX, 0.0D, -forceZ);
            if (!TrackToolsAPI.isCartLockedDown(cart))
                cart.addVelocity(forceX, 0, forceZ);
        }
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(EntityMinecart cart, Entity other) {
        if (other instanceof EntityItem && RailcraftConfig.doCartsCollideWithItems())
            return other.getEntityBoundingBox();
        return other.canBePushed() ? other.getEntityBoundingBox() : null; //            return other.boundingBox.contract(COLLISION_EXPANSION, 0, COLLISION_EXPANSION);
    }

    @Override
    public AxisAlignedBB getMinecartCollisionBox(EntityMinecart cart) {
//        return cart.getEntityBoundingBox().expand(MinecartHooks.COLLISION_EXPANSION, 0, MinecartHooks.COLLISION_EXPANSION);
        double yaw = Math.toRadians(cart.rotationYaw);
        double diff = ((CART_LENGTH - CART_WIDTH) / 2.0) + MinecartHooks.COLLISION_EXPANSION;
        double x = diff * Math.abs(Math.cos(yaw));
        double z = diff * Math.abs(Math.sin(yaw));
        return cart.getEntityBoundingBox().expand(x, MinecartHooks.COLLISION_EXPANSION, z);
    }

    @Nullable
    @Override
    public AxisAlignedBB getBoundingBox(EntityMinecart cart) {
        if (cart == null || cart.isDead)
            return null;
        if (RailcraftConfig.areCartsSolid())
            return cart.getEntityBoundingBox();
        return null;
    }

    private void land(EntityMinecart cart) {
        cart.getEntityData().setInteger("Launched", 0);
        cart.setMaxSpeedAirLateral(EntityMinecart.defaultMaxSpeedAirLateral);
        cart.setMaxSpeedAirVertical(EntityMinecart.defaultMaxSpeedAirVertical);
        cart.setDragAir(EntityMinecart.defaultDragAir);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onMinecartUpdate(MinecartUpdateEvent event) {
        EntityMinecart cart = event.getMinecart();
        NBTTagCompound data = cart.getEntityData();

        // Fix flip TODO test this
//        float distance = MathTools.getDistanceBetweenAngles(cart.rotationYaw, cart.prevRotationYaw);
//        float cutoff = 120F;
//        if (distance < -cutoff || distance >= cutoff) {
//            cart.rotationYaw += 180.0F;
//            cart.isInReverse = !cart.isInReverse;
//            cart.rotationYaw = cart.rotationYaw % 360.0F;
//        }


//        if (SeasonPlugin.isGhostTrain(cart)) {
//            cart.setGlowing(true);
//            data.setBoolean("ghost", true);
//        } else
        if (data.getBoolean("ghost")) {
            cart.setGlowing(false);
            data.setBoolean("ghost", false);
        }

// Code Added by Yopu to replace vanilla carts, deemed incomplete and unnecessary, pursuing other solutions
//        if (classReplacements.containsKey(cart.getClass())) {
//            cart.setDead();
//            if (Game.isHost(cart.world)) {
//                EnumCart enumCart = classReplacements.get(cart.getClass());
//                GameProfile cartOwner = CartTools.getCartOwner(cart);
//                int x = MathHelper.floor_double(cart.posX);
//                int y = MathHelper.floor_double(cart.posY);
//                int z = MathHelper.floor_double(cart.posZ);
//                CartUtils.placeCart(enumCart, cartOwner, enumCart.getCartItem(), cart.world, x, y, z);
//            }
//            return;
//        }

        Block block = WorldPlugin.getBlock(cart.world, event.getPos());
        int launched = data.getInteger("Launched");
        if (TrackTools.isRailBlock(block)) {
            cart.fallDistance = 0;
            if (cart.isBeingRidden())
                cart.getPassengers().forEach(p -> p.fallDistance = 0);
            if (launched > 1)
                land(cart);
        } else if (launched == 1) {
            data.setInteger("Launched", 2);
            cart.setCanUseRail(true);
        } else if (launched > 1 && (cart.onGround || cart.isInsideOfMaterial(Material.CIRCUITS)))
            land(cart);

        int mountPrevention = data.getInteger("MountPrevention");
        if (mountPrevention > 0) {
            mountPrevention--;
            data.setInteger("MountPrevention", mountPrevention);
        }

        byte elevator = data.getByte("elevator");
        if (elevator < BlockTrackElevator.ELEVATOR_TIMER) {
            cart.setNoGravity(false);
        }
        if (elevator > 0) {
            elevator--;
            data.setByte("elevator", elevator);
        }

        byte derail = data.getByte("derail");
        if (derail > 0) {
            derail--;
            data.setByte("derail", derail);
        }

        if (data.getBoolean("explode")) {
            cart.getEntityData().setBoolean("explode", false);
            CartTools.explodeCart(cart);
        }

        if (data.getBoolean(CartTools.HIGH_SPEED_TAG))
            if (CartTools.cartVelocityIsLessThan(cart, HighSpeedTools.SPEED_CUTOFF))
                data.setBoolean(CartTools.HIGH_SPEED_TAG, false);
            else if (data.getInteger("Launched") == 0)
                HighSpeedTools.checkSafetyAndExplode(cart.world, event.getPos(), cart);


        cart.motionX = Math.copySign(Math.min(Math.abs(cart.motionX), 9.5), cart.motionX);
        cart.motionY = Math.copySign(Math.min(Math.abs(cart.motionY), 9.5), cart.motionY);
        cart.motionZ = Math.copySign(Math.min(Math.abs(cart.motionZ), 9.5), cart.motionZ);

//        List entities = cart.world.getEntitiesWithinAABB(EntityLiving.class, getMinecartCollisionBox(cart, COLLISION_EXPANSION));
//
//        if (entities != null) {
//            for (Entity entity : (List<Entity>) entities) {
//                if (entity != cart.riddenByEntity && entity.canBePushed()) {
//                    cart.applyEntityCollision(entity);
//                }
//            }
//        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onMinecartEntityCollision(MinecartCollisionEvent event) {
        EntityMinecart cart = event.getMinecart();
        Entity other = event.getCollider();
        if (cart.isPassenger(other))
            return;

        if (other instanceof EntityMinecart)
            LinkageManager.instance().tryAutoLink(cart, (EntityMinecart) other);

        testHighSpeedCollision(cart, other);

        if (EntityMinecart.getCollisionHandler() != this)
            if (other instanceof EntityLivingBase && WorldPlugin.isBlockAt(cart.world, cart.getPosition(), RailcraftBlocks.TRACK_ELEVATOR.block()))
                if (other.getEntityBoundingBox().minY < cart.getEntityBoundingBox().maxY) {
                    other.move(MoverType.SELF, 0, cart.getEntityBoundingBox().maxY - other.getEntityBoundingBox().minY, 0);
                    other.onGround = true;
                }

        if (MiscTools.RANDOM.nextFloat() < 0.001f) {
            List<EntityMinecart> carts = EntitySearcher.findMinecarts().collidingWith(cart)
                    .with(Predicates.notInstanceOf(EntityMinecartCommandBlock.class)).at(cart.world);
            if (carts.size() >= 12)
                primeToExplode(cart);
        }
    }

    private void testHighSpeedCollision(EntityMinecart cart, Entity other) {
        boolean highSpeed = CartTools.isTravellingHighSpeed(cart);
        if (highSpeed) {
            if (other instanceof EntityMinecart && Train.areInSameTrain(cart, (EntityMinecart) other))
                return;
            for (EntityMinecart c : Train.getTrain(cart)) {
                if (c != null && c.isPassenger(other))
                    return;
            }

            if (other instanceof EntityMinecart) {
                boolean otherHighSpeed = CartTools.isTravellingHighSpeed((EntityMinecart) other);
                if (!otherHighSpeed || (cart.motionX > 0 ^ other.motionX > 0) || (cart.motionZ > 0 ^ other.motionZ > 0)) {
                    primeToExplode(cart);
                    return;
                }
            }

            if (!other.isEntityAlive() || RailcraftConfig.isEntityExcludedFromHighSpeedExplosions(other))
                return;

            primeToExplode(cart);
        }
    }

    private void primeToExplode(EntityMinecart cart) {
        cart.getEntityData().setBoolean("explode", true);
    }

    @SubscribeEvent
    public void onMinecartInteract(MinecartInteractEvent event) {
        EntityMinecart cart = event.getMinecart();
        EntityPlayer player = event.getPlayer();

        if (!CartToolsAPI.doesCartHaveOwner(cart))
            CartToolsAPI.setCartOwner(cart, player);

        if (!(cart instanceof EntityTunnelBore) && player.getDistanceSq(cart) > MAX_INTERACT_DIST_SQ) {
            event.setCanceled(true);
            return;
        }
        if (cart.isDead) {
            event.setCanceled(true);
            return;
        }
        if (cart.canBeRidden()) {
            //TODO: this will interfere with carts that multiple players can ride, re-evaluate
            if (cart.isBeingRidden() && player.getRidingEntity() != cart) {
                event.setCanceled(true);
                return;
            }
            if (player.getRidingEntity() != null && player.getRidingEntity() != cart) {
                event.setCanceled(true);
                return;
            }
            if (player.getRidingEntity() != cart && player.isOnLadder()) {
                event.setCanceled(true);
                return;
            }
        }
        if (!player.canEntityBeSeen(cart)) {
            event.setCanceled(true);
        }
    }
}
