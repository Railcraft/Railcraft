/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.items.IToolCrowbar;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TrackKitDelayedLocking extends TrackKitLocking implements IGuiReturnHandler {

    public static final int MAX_DELAY = 1200;
    public static final int MIN_DELAY = 0;
    private int delay = 100;
    private int waited;

    public TrackKitDelayedLocking() {
        setProfile(LockingProfileType.HOLDING_TRAIN);
    }

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.DELAYED;
    }

    @Override
    public int getRenderState() {
        return redstone ? (waited == 0 ? 1 : 2) : 0;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (!InvTools.isEmpty(heldItem) && heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, hand, heldItem, getPos())) {
                crowbar.onWhack(player, hand, heldItem, getPos());
                GuiHandler.openGui(EnumGui.TRACK_DELAYED, player, theWorldAsserted(), getPos());
                return true;
            }
        }
        return false;
    }

    /**
     * The heart of the logic for this class is done here. If you understand what's going
     * on here, the rest will make much more sense to you. Basically, we're trying to determine
     * whether this track should be trying to lock the current or next cart that passes over it.
     * First of all we must realize that we only have 2 inputs: 1) whether a train/cart
     * is passing over us and 2) whether our track is receiving a redstone signal. If we try to
     * create a truth table with 2 boolean inputs to calculate "locked", we find that we can't quite
     * express the correct value for "locked". When we analyze the situation, we notice that when
     * a train is passing over the track, we need both the redstone to be off and the last cart to be
     * off the track in order to lock the track. However after the train has already left the track,
     * then we want the track to be "locked" when the redstone is off, regardless of whether a
     * new or old cart starts moving onto the track. In the end, what we're really after is
     * having 2 truth tables and a way to decide which of the 2 tables to use. To do this, we
     * use the boolean {@code trainLeaving} to indicate which table to use. As the name
     * implies, {@code trainLeaving} indicates whether the train or cart is in the process
     * of leaving the track.
     */
    @Override
    void calculateLocked() {
        if (currentCart == null) {
            waited = 0;
        }
        boolean isSameCart = isSameTrainOrCart();
        if (!redstone || waited >= delay) {
            locked = false;
            waited = 0;
            if (isSameCart) {
                trainLeaving = true;
            }
            return;
        }

        if (trainLeaving && isSameCart) {
            locked = false;
        } else {
            locked = true;
            trainLeaving = false;
        }

        if (currentCart != null && locked) {
            waited++;
            if (waited == 1) {
                sendUpdateToClient();
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("delay", delay);
        data.setInteger("waited", waited);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        delay = data.getInteger("delay");
        waited = data.getInteger("waited");
    }

    @Override
    public World theWorld() {
        return getTile().getWorld();
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeInt(waited);
        data.writeInt(delay);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        waited = data.readInt();
        delay = data.readInt();
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeInt(delay);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        delay = MathHelper.clamp(data.readInt(), MIN_DELAY, MAX_DELAY);
    }
}
