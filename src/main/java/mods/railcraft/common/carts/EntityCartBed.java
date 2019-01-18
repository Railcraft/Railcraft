/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.client.core.SleepKeyHandler;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

/**
 * A class for the bed carts.
 */
public class EntityCartBed extends EntityCartBasic {

    WeakReference<EntityPlayer> sleeping = new WeakReference<>(null);
    boolean wokeUp;
    boolean rideAfterSleep;
    boolean shouldSleep;
    private boolean notifyRider;

    public EntityCartBed(World world) {
        super(world);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.BED;
    }

    @Override
    public boolean canBeRidden() {
        return true;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.CARPET.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.GRAY);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isClient(world)) {
            Entity passenger = getFirstPassenger();
            if (passenger instanceof EntityPlayer) {
                BedCartEventListener.setRenderOffset((EntityPlayer) passenger, motionX, motionZ);
            }
            if (notifyRider) {
                notifyRider = false;
                ChatPlugin.sendLocalizedHotBarMessageFromClient(
                        "gui.railcraft.cart.bed.key",
                        Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName(),
                        SleepKeyHandler.INSTANCE.getKeyDisplayName()
                );
            }
            return;
        }

        EntityPlayer sleeper = sleeping.get();
        if (sleeper != null) {
//            BedCartEventListener.setRenderOffset(sleeper, motionX, motionZ);
            if (sleeper.getRidingEntity() != this) {
                sleeper.startRiding(this, true);
            }
            // Riding enforced! Otherwise player dismounts once sleeps
            if (rideAfterSleep) {
                rideAfterSleep = false;
                sendPlayerRiding((EntityPlayerMP) sleeper);
            }
            if (wokeUp) {
                wokeUp = false;
                sleeping = new WeakReference<>(null);
                BedCartEventListener.INSTANCE.riderToBed.remove(sleeper);
            } else {
                sleeper.bedLocation = getPosition();
            }
        }

        Entity rider = getFirstPassenger();
        if (!(rider instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) rider;

        if (shouldSleep && world.provider.isSurfaceWorld() && !player.isPlayerSleeping()) {
            shouldSleep = false;
            EntityPlayer.SleepResult sleepResult = player.trySleep(getPosition());
            if (sleepResult == EntityPlayer.SleepResult.NOT_SAFE) {
                ChatPlugin.sendLocalizedHotBarMessageFromServer(player, "tile.bed.notSafe");
            } else if (sleepResult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                ChatPlugin.sendLocalizedHotBarMessageFromServer(player, "tile.bed.noSleep");
            }
        }
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (Game.isClient(world) && passenger instanceof EntityPlayerSP) {
            notifyRider = true;
            // vanilla sends a "press shift" message after adding passenger, so.
        }
    }

// Handled by super class
//    @Override
//    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
//        //No rider removal!
//    }

    private void sendPlayerRiding(EntityPlayerMP player) {
        player.connection.sendPacket(new SPacketSetPassengers(this));
    }

    public void attemptSleep() {
        shouldSleep = true;
    }

    protected @Nullable Entity getFirstPassenger() {
        List<Entity> passengers = getPassengers();
        return passengers.isEmpty() ? null : passengers.get(0);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        EntityPlayer player = sleeping.get();
        if (player != null) {
            compound.setUniqueId("sleeping", player.getUniqueID());
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasUniqueId("sleeping")) {
            UUID playerId = compound.getUniqueId("sleeping");
            if (playerId != null) {
                Entity ref = ((WorldServer) world).getEntityFromUuid(playerId);
                if (ref instanceof EntityPlayer) {
                    sleeping = new WeakReference<>((EntityPlayer) ref);
                    BedCartEventListener.INSTANCE.riderToBed.put((EntityPlayer) ref, this);
                }
            }
        }
    }
}
