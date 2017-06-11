/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import com.google.gson.JsonParseException;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.Timer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;

import java.lang.ref.WeakReference;

public class TrackKitMessenger extends TrackKitPowered {

    private ITextComponent title;
    private ITextComponent subtitle;
    private boolean subtitleSet;
    private WeakReference<EntityMinecart> lastCart;
    private Timer timer = new Timer();

    public TrackKitMessenger() {
        this.title = ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.title.default");
        this.subtitle = ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.subtitle.default");
    }

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.MESSENGER;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (!isPowered())
            return;
        if (lastCart != null && lastCart.get() == cart) {
            if (timer.hasTriggered(cart.worldObj, 100)) {
                sendMessage(cart);
            }
        } else {
            sendMessage(cart);
            lastCart = new WeakReference<>(cart);
            timer.reset();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setString("title", ITextComponent.Serializer.componentToJson(title));
        data.setString("subtitle", ITextComponent.Serializer.componentToJson(subtitle));
        data.setBoolean("subtitleSet", subtitleSet);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        subtitleSet = data.getBoolean("subtitleSet");
        try {
            title = ITextComponent.Serializer.jsonToComponent(data.getString("title"));
            subtitle = ITextComponent.Serializer.jsonToComponent(data.getString("subtitle"));
        } catch (JsonParseException ignored) {
        }
    }

    public ITextComponent getTitle() {
        return title;
    }

    public ITextComponent getSubtitle() {
        return subtitle;
    }

    public void setTitle(ITextComponent title) {
        this.title = title;
        if (!subtitleSet)
            this.subtitle = ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.subtitle.standard");
    }

    public void setSubtitle(ITextComponent subtitle) {
        this.subtitle = subtitle;
        subtitleSet = true;
    }

    public void setTitle(ICommandSender setter, ITextComponent title) {
        setTitle(title);
        setter.addChatMessage(ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.title.set"));
    }

    public void setSubtitle(ICommandSender setter, ITextComponent subtitle) {
        setSubtitle(subtitle);
        setter.addChatMessage(ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.subtitle.set"));
    }

    protected void sendMessage(EntityMinecart cart) {
        cart.getRecursivePassengersByType(EntityPlayerMP.class).forEach(e -> {
            try {
                SPacketTitle pkt = new SPacketTitle(SPacketTitle.Type.SUBTITLE, TextComponentUtils.processComponent(cart, subtitle, e));
                e.connection.sendPacket(pkt);
                pkt = new SPacketTitle(SPacketTitle.Type.TITLE, TextComponentUtils.processComponent(cart, title, e));
                e.connection.sendPacket(pkt);
            } catch (CommandException ignored) {
            }
        });

    }
}
