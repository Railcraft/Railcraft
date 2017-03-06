/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import com.google.gson.JsonParseException;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

public class TrackKitMessenger extends TrackKitPowered {

    protected static final TextComponentTranslation SUCCESS_MESSAGE = new TextComponentTranslation("railcraft.gui.message.set");
    protected ITextComponent text;
    protected WeakReference<EntityMinecart> lastCart;
    protected long lastTime;

    public TrackKitMessenger() {
        this.text = new TextComponentString("");
    }

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.MESSENGER;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        if (heldItem != null) {
            if (heldItem.hasTagCompound() && loadfrom(heldItem.getTagCompound())) {
                notifySuccessfulSet(player);
            } else if (heldItem.hasDisplayName()) {
                String name = heldItem.getDisplayName();
                this.text = new TextComponentString(name);
                notifySuccessfulSet(player);
            }
        }
        return super.blockActivated(player, hand, heldItem);
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (!isPowered()) {
            return;
        }
        long time = cart.worldObj.getWorldTime();
        if (this.lastCart != null && this.lastCart.get() == cart) {
            if (time - this.lastTime > 1) {
                sendMessage(cart);
            }
        } else {
            sendMessage(cart);
            this.lastCart = new WeakReference<>(cart);
        }
        this.lastTime = time;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        data.setString("Message", ITextComponent.Serializer.componentToJson(this.text));
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        loadfrom(data);
    }

    protected boolean loadfrom(NBTTagCompound data) {
        try {
            this.text = ITextComponent.Serializer.fromJsonLenient(data.getString("Message"));
        } catch (JsonParseException ex) {
            return false;
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        if (stack.hasTagCompound()) {
            if (loadfrom(stack.getTagCompound()) && placer != null) {
                notifySuccessfulSet(placer);
            }
        }
    }

    protected void sendMessage(EntityMinecart cart) {
        cart.addChatMessage(this.text);
        cart.getRecursivePassengers().forEach(e -> e.addChatMessage(this.text));
    }

    protected void notifySuccessfulSet(EntityLivingBase setter) {
        setter.addChatMessage(SUCCESS_MESSAGE);
        setter.addChatMessage(this.text);
    }
}
