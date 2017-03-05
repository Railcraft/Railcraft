/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

public class TrackKitMessenger extends TrackKitRailcraft {

    ITextComponent text = new TextComponentString("");
    WeakReference<EntityMinecart> lastCart;
    long lastTime;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.MESSENGER;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        if (heldItem != null) {
            if (heldItem.hasDisplayName()) {
                text = new TextComponentString(heldItem.getDisplayName());
            }
        }
        return super.blockActivated(player, hand, heldItem);
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        long time = cart.worldObj.getWorldTime();
        if (lastCart != null && lastCart.get() == cart) {
            if (time - lastTime > 1) {
                sendMessage(cart);
            }
        } else {
            sendMessage(cart);
            lastCart = new WeakReference<>(cart);
        }
        lastTime = time;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        data.setString("Message", ITextComponent.Serializer.componentToJson(text));
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        text = ITextComponent.Serializer.jsonToComponent(data.getString("Message"));
    }

    void sendMessage(EntityMinecart cart) {
        cart.addChatMessage(text);
        cart.getRecursivePassengers().forEach(e -> e.addChatMessage(text));
    }
}
