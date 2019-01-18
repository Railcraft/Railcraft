/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;

import java.lang.ref.WeakReference;

public class TrackKitMessenger extends TrackKitPowered {

    private ITextComponent title = new TextComponentString("");
    private ITextComponent subtitle = ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.subtitle.standard");
    private ITextComponent actionbar = new TextComponentString("");
    private boolean setup;
    private WeakReference<EntityMinecart> lastCart;
    private Timer timer = new Timer();

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.MESSENGER;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (!isPowered())
            return;
        if (lastCart != null && lastCart.get() == cart) {
            if (timer.hasTriggered(cart.world, 100)) {
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
        data.setString("actionbar", ITextComponent.Serializer.componentToJson(actionbar));
        data.setBoolean("setup", setup);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        setup = data.getBoolean("subtitleSet") || data.getBoolean("setup");
        try {
            title = ITextComponent.Serializer.jsonToComponent(data.getString("title"));
            subtitle = ITextComponent.Serializer.jsonToComponent(data.getString("subtitle"));
            actionbar = ITextComponent.Serializer.jsonToComponent(data.getString("actionbar"));
        } catch (JsonParseException ignored) {
        }
    }

    public ITextComponent getTitle() {
        if (setup)
            return title;
        return ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.title.default");
    }

    public ITextComponent getSubtitle() {
        if (setup)
            return subtitle;
        return ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.subtitle.default");
    }

    public ITextComponent getActionbar() {
        if (setup)
            return actionbar;
        return ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.actionbar.default");
    }

    public void setTitle(ITextComponent title) {
        this.title = title;
        setup = true;
    }

    public void setSubtitle(ITextComponent subtitle) {
        this.subtitle = subtitle;
        setup = true;
    }

    public void setActionbar(ITextComponent actionbar) {
        this.actionbar = actionbar;
        setup = true;
    }

    public void setTitle(ICommandSender setter, ITextComponent title) {
        setTitle(title);
        setter.sendMessage(ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.title.set"));
    }

    public void setSubtitle(ICommandSender setter, ITextComponent subtitle) {
        setSubtitle(subtitle);
        setter.sendMessage(ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.subtitle.set"));
    }

    public void setActionbar(ICommandSender setter, ITextComponent actionbar) {
        setActionbar(actionbar);
        setter.sendMessage(ChatPlugin.translateMessage("gui.railcraft.track_kit.messenger.actionbar.set"));
    }

    protected void sendMessage(EntityMinecart cart) {
        cart.getRecursivePassengersByType(EntityPlayerMP.class).forEach(e -> {
            try {
                SPacketTitle pkt = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, TextComponentUtils.processComponent(cart, getActionbar(), e));
                e.connection.sendPacket(pkt);
                pkt = new SPacketTitle(SPacketTitle.Type.SUBTITLE, TextComponentUtils.processComponent(cart, getSubtitle(), e));
                e.connection.sendPacket(pkt);
                pkt = new SPacketTitle(SPacketTitle.Type.TITLE, TextComponentUtils.processComponent(cart, getTitle(), e));
                e.connection.sendPacket(pkt);
            } catch (CommandException ignored) {
            }
        });

    }
}
