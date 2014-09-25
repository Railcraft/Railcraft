/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerReinforced;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.misc.SafeNBTWrapper;
import mods.railcraft.common.util.network.IGuiReturnHandler;

public class TrackLauncher extends TrackReinforced implements ITrackPowered, IGuiReturnHandler {

    private boolean powered = false;
    private byte launchForce = 5;
    public static final int MIN_LAUNCH_FORCE = 5;
    private static final float LAUNCH_THRESHOLD = 0.01f;

    public TrackLauncher() {
        speedController = SpeedControllerReinforced.getInstance();
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
    public IIcon getIcon() {
        if (!isPowered()) {
            return getIcon(1);
        }
        return getIcon(0);
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, getX(), getY(), getZ())) {
                GuiHandler.openGui(EnumGui.TRACK_LAUNCHER, player, getWorld(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                crowbar.onWhack(player, current, getX(), getY(), getZ());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
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
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setByte("force", getLaunchForce());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        powered = data.getBoolean("powered");

        SafeNBTWrapper safe = new SafeNBTWrapper(data);

        setLaunchForce(safe.getByte("force"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(powered);
        data.writeByte(launchForce);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        powered = data.readBoolean();
        launchForce = data.readByte();

        markBlockNeedsUpdate();
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeByte(launchForce);
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
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
