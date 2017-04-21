/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityLocomotive.LocoSpeed;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.misc.EnumTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitThrottle extends TrackKitPowered {
    private LocoSpeed speed = LocoSpeed.MAX;
    private boolean reverse = false;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.THROTTLE;
    }

    @Override
    public int getRenderState() {
        int state = speed.ordinal();
        if (getReverse())
        	state = 14;
        if (isPowered()) {
            if (getReverse())
            	state += 1;
            else
            	state += 7;
    	}
        return state;
    }

    public LocoSpeed getSpeed() {
        return speed;
    }
    
    public boolean getReverse() {
    	return reverse;
    }

    public void setSpeed(LocoSpeed speed) {
        if (this.speed != speed) {
            this.speed = speed;
            if (Game.isClient(theWorldAsserted()))
                markBlockNeedsUpdate();
        }
    }
    
    public void setReverse(boolean state) {
    	if (this.reverse != state) {
    		this.reverse = state;
    		if (Game.isClient(theWorldAsserted()))
    			markBlockNeedsUpdate();
    	}
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        if (heldItem != null && heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, hand, heldItem, getPos())) {
            	if (speed == LocoSpeed.MAX) {
            		if (!getReverse()) {
            			setReverse(true);
            		} else {
            			setReverse(false);
            			setSpeed(EnumTools.next(speed, LocoSpeed.VALUES));
            		}
            	} else {
            		setSpeed(EnumTools.next(speed, LocoSpeed.VALUES));
            	}
                crowbar.onWhack(player, hand, heldItem, getPos());
                sendUpdateToClient();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered()) {
            if (cart instanceof EntityLocomotive) {
            	if (getReverse()) {
            		((EntityLocomotive) cart).setReverse(true);
            		((EntityLocomotive) cart).setSpeed(LocoSpeed.SLOWER);
            	} else {
            		((EntityLocomotive) cart).setReverse(false);
            		((EntityLocomotive) cart).setSpeed(getSpeed());
            	}
            }
        }
    }

    @Override
    public int getPowerPropagation() {
        return 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setString("locoSpeed", speed.getName());
        NBTPlugin.writeEnumName(data, "locoSpeed", speed);
        data.setBoolean("locoReverse", reverse);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("locoSpeed"))
            speed = NBTPlugin.readEnumName(data, "locoSpeed", LocoSpeed.MAX);
        if (data.hasKey("locoReverse"))
        	reverse = data.getBoolean("locoReverse");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        ((RailcraftOutputStream) data).writeEnum(speed);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        setSpeed(((RailcraftInputStream) data).readEnum(LocoSpeed.VALUES));
    }
}
