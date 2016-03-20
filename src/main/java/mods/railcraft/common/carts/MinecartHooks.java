/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;
import mods.railcraft.api.tracks.RailTools;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.TrackSpeed;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.Vec2D;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IMinecartCollisionHandler;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

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
    public void onItemUse(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        World world = player.worldObj;
        if (Game.isNotHost(world))
            return;

        ItemStack itemStack = player.getHeldItem();
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && itemStack != null) {
            Item item = itemStack.getItem();
            if (item != null && CartUtils.vanillaCartItemMap.containsKey(item)) {
                event.useItem = Event.Result.DENY;
                EntityMinecart placedCart = CartUtils.placeCart(
                        CartUtils.vanillaCartItemMap.get(item),
                        player.getGameProfile(), itemStack, world,
                        event.x, event.y, event.z);
                if (placedCart != null && !player.capabilities.isCreativeMode)
                    itemStack.stackSize--;
            }
        }
    }

    @Override
    public void onEntityCollision(EntityMinecart cart, Entity other) {
        if (Game.isNotHost(cart.worldObj) || other == cart.riddenByEntity || !other.isEntityAlive() || !cart.isEntityAlive())
            return;

        ILinkageManager lm = LinkageManager.instance();
        EntityMinecart link = lm.getLinkedCartA(cart);
        if (link != null && (link == other || other == link.riddenByEntity))
            return;
        link = lm.getLinkedCartB(cart);
        if (link != null && (link == other || other == link.riddenByEntity))
            return;

        boolean isLiving = other instanceof EntityLivingBase;
        boolean isPlayer = other instanceof EntityPlayer;

        if (isLiving && !isPlayer && cart.canBeRidden() && !(other instanceof EntityIronGolem)
                && cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0.001D
                && cart.riddenByEntity == null && other.ridingEntity == null) {
            int mountPrevention = cart.getEntityData().getInteger("MountPrevention");
            if (mountPrevention <= 0)
                other.mountEntity(cart);
        }

        int i = MathHelper.floor_double(cart.posX);
        int j = MathHelper.floor_double(cart.posY);
        int k = MathHelper.floor_double(cart.posZ);
        Block block = cart.worldObj.getBlock(i, j, k);
        if (isLiving && RailcraftBlocks.getBlockElevator() != null && block == RailcraftBlocks.getBlockElevator())
            return;

//        System.out.println(cart.getClass().getSimpleName() + ": " + cart.entityId + " collided with " + other.getClass().getSimpleName() + ": " + other.entityId);
        Vec2D cartPos = new Vec2D(cart.posX, cart.posZ);
        Vec2D otherPos = new Vec2D(other.posX, other.posZ);

        Vec2D unit = Vec2D.subtract(otherPos, cartPos);
        unit.normalize();

        double distance = cart.getDistanceToEntity(other);
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
                if (!RailTools.isCartLockedDown(cart))
                    cart.addVelocity(forceX, 0, forceZ);
            if (!otherCart.isPoweredCart() || cart.isPoweredCart())
                if (!RailTools.isCartLockedDown(otherCart))
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
            if (!RailTools.isCartLockedDown(cart))
                cart.addVelocity(forceX, 0, forceZ);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBox(EntityMinecart cart, Entity other) {
        if (other instanceof EntityItem && RailcraftConfig.doCartsCollideWithItems())
            return other.boundingBox;
        if (other instanceof EntityPlayer)
            return other.canBePushed() ? other.boundingBox : null; //            return other.boundingBox.contract(COLLISION_EXPANSION, 0, COLLISION_EXPANSION);
        return null;
    }

    @Override
    public AxisAlignedBB getMinecartCollisionBox(EntityMinecart cart) {
        double yaw = Math.toRadians(cart.rotationYaw);
        double diff = ((CART_LENGTH - CART_WIDTH) / 2.0) + MinecartHooks.COLLISION_EXPANSION;
        double x = diff * Math.abs(Math.cos(yaw));
        double z = diff * Math.abs(Math.sin(yaw));
        return cart.boundingBox.expand(x, MinecartHooks.COLLISION_EXPANSION, z);
    }

    @Override
    public AxisAlignedBB getBoundingBox(EntityMinecart cart) {
        if (cart == null || cart.isDead)
            return null;
        if (RailcraftConfig.areCartsSolid())
            return cart.boundingBox;
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
        EntityMinecart cart = event.minecart;
        NBTTagCompound data = cart.getEntityData();

// Code Added by Yopu to replace vanilla carts, deemed incomplete and unnecessary, pursuing other solutions
//        if (classReplacements.containsKey(cart.getClass())) {
//            cart.setDead();
//            if (Game.isHost(cart.worldObj)) {
//                EnumCart enumCart = classReplacements.get(cart.getClass());
//                GameProfile cartOwner = CartTools.getCartOwner(cart);
//                int x = MathHelper.floor_double(cart.posX);
//                int y = MathHelper.floor_double(cart.posY);
//                int z = MathHelper.floor_double(cart.posZ);
//                CartUtils.placeCart(enumCart, cartOwner, enumCart.getCartItem(), cart.worldObj, x, y, z);
//            }
//            return;
//        }
        int x = (int) event.x;
        int y = (int) event.y;
        int z = (int) event.z;
        Block block = cart.worldObj.getBlock(x, y, z);
        int launched = data.getInteger("Launched");
        if (TrackTools.isRailBlock(block)) {
            cart.fallDistance = 0;
            if (cart.riddenByEntity != null)
                cart.riddenByEntity.fallDistance = 0;
            if (launched > 1)
                land(cart);
        } else if (launched == 1) {
            data.setInteger("Launched", 2);
            cart.setCanUseRail(true);
        } else if (launched > 1 && (cart.onGround || cart.isInsideOfMaterial(Material.circuits)))
            land(cart);

        int mountPrevention = data.getInteger("MountPrevention");
        if (mountPrevention > 0) {
            mountPrevention--;
            data.setInteger("MountPrevention", mountPrevention);
        }

        byte elevator = data.getByte("elevator");
        if (elevator > 0) {
            elevator--;
            data.setByte("elevator", elevator);
        }

        if (data.getBoolean("explode")) {
            cart.getEntityData().setBoolean("explode", false);
            CartUtils.explodeCart(cart);
        }

        if (data.getBoolean("HighSpeed"))
            if (CartUtils.cartVelocityIsLessThan(cart, TrackSpeed.SPEED_CUTOFF))
                data.setBoolean("HighSpeed", false);
            else if (!TrackSpeed.isTrackHighSpeedCapable(cart.worldObj, x, y, z))
                CartUtils.explodeCart(cart);

        cart.motionX = Math.copySign(Math.min(Math.abs(cart.motionX), 9.5), cart.motionX);
        cart.motionY = Math.copySign(Math.min(Math.abs(cart.motionY), 9.5), cart.motionY);
        cart.motionZ = Math.copySign(Math.min(Math.abs(cart.motionZ), 9.5), cart.motionZ);

//        List entities = cart.worldObj.getEntitiesWithinAABB(EntityLiving.class, getMinecartCollisionBox(cart, COLLISION_EXPANSION));
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
        EntityMinecart cart = event.minecart;
        Entity other = event.collider;
        if (other == cart.riddenByEntity)
            return;

        if (other instanceof EntityMinecart)
            LinkageManager.instance().tryAutoLink(cart, (EntityMinecart) other);

        testHighSpeedCollision(cart, other);

        int i = MathHelper.floor_double(cart.posX);
        int j = MathHelper.floor_double(cart.posY);
        int k = MathHelper.floor_double(cart.posZ);

        if (EntityMinecart.getCollisionHandler() != this)
            if (other instanceof EntityLivingBase && RailcraftBlocks.getBlockElevator() != null && cart.worldObj.getBlock(i, j, k) == RailcraftBlocks.getBlockElevator())
                if (other.boundingBox.minY < cart.boundingBox.maxY) {
                    other.moveEntity(0, cart.boundingBox.maxY - other.boundingBox.minY, 0);
                    other.onGround = true;
                }

        if (MiscTools.getRand().nextFloat() < 0.001f) {
            List<EntityMinecart> carts = CartTools.getMinecartsAt(cart.worldObj, i, j, k, 0);
            if (carts.size() >= 12)
                primeToExplode(cart);
        }
    }

    private void testHighSpeedCollision(EntityMinecart cart, Entity other) {
        boolean highSpeed = cart.getEntityData().getBoolean("HighSpeed");
        if (highSpeed) {
            if (other instanceof EntityMinecart && Train.areInSameTrain(cart, (EntityMinecart) other))
                return;
            for (EntityMinecart c : Train.getTrain(cart)) {
                if (other == c.riddenByEntity)
                    return;
            }

            if (other instanceof EntityMinecart) {
                boolean otherHighSpeed = other.getEntityData().getBoolean("HighSpeed");
                if (!otherHighSpeed || (cart.motionX > 0 ^ other.motionX > 0) || (cart.motionZ > 0 ^ other.motionZ > 0)) {
                    primeToExplode(cart);
                    return;
                }
            }

            if (RailcraftConfig.isEntityExcludedFromHighSpeedExplosions(other))
                return;

            primeToExplode(cart);
        }
    }

    private void primeToExplode(EntityMinecart cart) {
        cart.getEntityData().setBoolean("explode", true);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onMinecartInteract(MinecartInteractEvent event) {
        EntityMinecart cart = event.minecart;
        EntityPlayer player = event.player;

        if (!CartTools.doesCartHaveOwner(cart))
            CartTools.setCartOwner(cart, player);

        if (!(cart instanceof EntityTunnelBore) && player.getDistanceSqToEntity(cart) > MAX_INTERACT_DIST_SQ) {
            event.setCanceled(true);
            return;
        }
        if (cart.isDead) {
            event.setCanceled(true);
            return;
        }
        if (cart.canBeRidden()) {
            if (cart.riddenByEntity != null && player.ridingEntity != cart) {
                event.setCanceled(true);
                return;
            }
            if (player.ridingEntity != null && player.ridingEntity != cart) {
                event.setCanceled(true);
                return;
            }
            if (player.ridingEntity != cart && player.isOnLadder()) {
                event.setCanceled(true);
                return;
            }
        }
        if (!player.canEntityBeSeen(cart)) {
            event.setCanceled(true);
        }
    }
}
