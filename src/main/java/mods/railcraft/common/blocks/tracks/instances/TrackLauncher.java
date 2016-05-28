/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerReinforced;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.misc.SafeNBTWrapper;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackLauncher extends TrackPowered implements IGuiReturnHandler {

    public static final int MIN_LAUNCH_FORCE = 5;
    private static final float LAUNCH_THRESHOLD = 0.01f;
    private byte launchForce = 5;

    public TrackLauncher() {
        speedController = SpeedControllerReinforced.instance();
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.LAUNCHER;
    }

    @Override
    public boolean isFlexibleRail() {
        return false;
    }

    @Override
    public boolean blockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, ItemStack heldItem) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, getPos())) {
                GuiHandler.openGui(EnumGui.TRACK_LAUNCHER, player, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
                crowbar.onWhack(player, current, getPos());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMinecartPass(@Nonnull EntityMinecart cart) {
        if (isPowered()) {
            if (Math.abs(cart.motionX) > LAUNCH_THRESHOLD) {
                cart.motionX = Math.copySign(0.6f, cart.motionX);
            }
            if (Math.abs(cart.motionZ) > LAUNCH_THRESHOLD) {
                cart.motionZ = Math.copySign(0.6f, cart.motionZ);
            }
            cart.setMaxSpeedAirLateral(0.6f);
            cart.setMaxSpeedAirVertical(0.5f);
            cart.setDragAir(0.99999);
            cart.motionY = getLaunchForce() * 0.1;
            cart.getEntityData().setInteger("Launched", 1);
            cart.setCanUseRail(false);
            cart.moveEntity(cart.motionX, 1.5, cart.motionZ);
        }
    }

    @Override
    public void writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("force", getLaunchForce());
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        SafeNBTWrapper safe = new SafeNBTWrapper(data);

        setLaunchForce(safe.getByte("force"));
    }

    @Override
    public void writePacketData(@Nonnull DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(launchForce);
    }

    @Override
    public void readPacketData(@Nonnull DataInputStream data) throws IOException {
        super.readPacketData(data);
        launchForce = data.readByte();
    }

    @Override
    public void writeGuiData(@Nonnull DataOutputStream data) throws IOException {
        data.writeByte(launchForce);
    }

    @Override
    public void readGuiData(@Nonnull DataInputStream data, EntityPlayer sender) throws IOException {
        launchForce = data.readByte();
    }

    public byte getLaunchForce() {
        return launchForce;
    }

    public void setLaunchForce(int force) {
        force = Math.max(force, MIN_LAUNCH_FORCE);
        force = Math.min(force, RailcraftConfig.getLaunchRailMaxForce());
        launchForce = (byte) force;
    }
}
