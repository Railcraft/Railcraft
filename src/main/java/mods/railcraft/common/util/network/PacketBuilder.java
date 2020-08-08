/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import mods.railcraft.api.signals.AbstractPair;
import mods.railcraft.api.signals.ISignalPacketBuilder;
import mods.railcraft.common.carts.EntityCartJukebox;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.widgets.Widget;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketKeyPress.EnumKeyBinding;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class PacketBuilder implements ISignalPacketBuilder {

    private static PacketBuilder instance;

    private PacketBuilder() {
    }

    public static PacketBuilder instance() {
        if (instance == null)
            instance = new PacketBuilder();
        return instance;
    }

    public void sendTileEntityPacket(TileEntity tile) {
        if (tile.getWorld() instanceof WorldServer) {
            WorldServer world = (WorldServer) tile.getWorld();
            SPacketUpdateTileEntity packet = tile.getUpdatePacket();
            if (packet != null)
                PacketDispatcher.sendToWatchers(packet, world, tile.getPos().getX(), tile.getPos().getZ());
        }
    }

    public void sendTileEntityPacket(@Nullable TileEntity tile, EntityPlayerMP player) {
        if (tile != null) {
            SPacketUpdateTileEntity packet = tile.getUpdatePacket();
            if (packet != null)
                PacketDispatcher.sendToPlayer(packet, player);
        }
    }

    @Override
    public void sendPairPacketUpdate(AbstractPair pairing) {
        PacketPairUpdate pkt = new PacketPairUpdate(pairing);
        PacketDispatcher.sendToDimension(pkt, pairing.getTile().getWorld().provider.getDimension());
    }

    @Override
    public void sendPairPacketRequest(AbstractPair pairing) {
        PacketPairRequest pkt = new PacketPairRequest(pairing);
        PacketDispatcher.sendToServer(pkt);
    }

    public void sendGuiReturnPacket(IGuiReturnHandler handler) {
        PacketGuiReturn pkt = new PacketGuiReturn(handler);
        PacketDispatcher.sendToServer(pkt);
    }

    public void sendGuiReturnPacket(IGuiReturnHandler handler, byte[] extraData) {
        PacketGuiReturn pkt = new PacketGuiReturn(handler, extraData);
        PacketDispatcher.sendToServer(pkt);
    }

    public void sendKeyPressPacket(EnumKeyBinding keyPress) {
        PacketKeyPress pkt = new PacketKeyPress(keyPress);
        PacketDispatcher.sendToServer(pkt);
    }

    public void sendGuiIntegerPacket(IContainerListener listener, int windowId, int key, int value) {
        if (listener instanceof EntityPlayerMP) {
            PacketGuiInteger pkt = new PacketGuiInteger(windowId, key, value);
            PacketDispatcher.sendToPlayer(pkt, (EntityPlayerMP) listener);
        }
    }

    public void sendGuiStringPacket(IContainerListener listener, int windowId, int key, String value) {
        if (listener instanceof EntityPlayerMP) {
            PacketGuiString pkt = new PacketGuiString(windowId, key, value);
            PacketDispatcher.sendToPlayer(pkt, (EntityPlayerMP) listener);
        }
    }

    public void sendGuiDataPacket(IContainerListener listener, int windowId, int key, byte[] value) {
        if (listener instanceof EntityPlayerMP) {
            PacketGuiData pkt = new PacketGuiData(windowId, key, value);
            PacketDispatcher.sendToPlayer(pkt, (EntityPlayerMP) listener);
        }
    }

    public void sendGuiWidgetPacket(IContainerListener listener, int windowId, Widget widget) {
        if (listener instanceof EntityPlayerMP && widget.hasServerSyncData(listener)) {
            ByteBuf byteBuf = Unpooled.buffer();
            try (ByteBufOutputStream out = new ByteBufOutputStream(byteBuf);
                 RailcraftOutputStream data = new RailcraftOutputStream(out)) {
                widget.writeServerSyncData(listener, data);
                byte[] syncData = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(syncData);
                PacketGuiWidget pkt = new PacketGuiWidget(windowId, widget, syncData);
                PacketDispatcher.sendToPlayer(pkt, (EntityPlayerMP) listener);
            } catch (IOException ex) {
                if (Game.DEVELOPMENT_VERSION)
                    throw new RuntimeException(ex);
            } finally {
                byteBuf.release();
            }
        }
    }

    public void sendGoldenTicketGuiPacket(EntityPlayerMP player, EnumHand hand) {
        PacketTicketGui pkt = new PacketTicketGui(hand);
        PacketDispatcher.sendToPlayer(pkt, player);
    }

    public void sendLogbookGuiPacket(EntityPlayerMP player, Multimap<LocalDate, GameProfile> log) {
        PacketLogbook pkt = new PacketLogbook(log);
        PacketDispatcher.sendToPlayer(pkt, player);
    }

    public void sendMovingSoundPacket(SoundEvent sound, SoundCategory category, EntityMinecart cart, SoundHelper.MovingSoundType type) {
        sendMovingSoundPacket(sound, category, cart, type, new NBTTagCompound());
    }

    public void sendMovingSoundPacket(SoundEvent sound, SoundCategory category, EntityMinecart cart, SoundHelper.MovingSoundType type, NBTTagCompound extraData) {
        if (!RailcraftConfig.playSounds())
            return;
        PacketMovingSound pkt = new PacketMovingSound(sound, category, cart, type, extraData);
        PacketDispatcher.sendToDimension(pkt, cart.world.provider.getDimension());
    }

    public void stopRecord(EntityCartJukebox cart) {
        if (!RailcraftConfig.playSounds())
            return;
        PacketStopRecord pkt = new PacketStopRecord(cart);
        PacketDispatcher.sendToDimension(pkt, cart.world.provider.getDimension());
    }

    public void sendEntitySync(Entity entity) {
        PacketEntitySync pkt = new PacketEntitySync(entity);
        PacketDispatcher.sendToDimension(pkt, entity.world.provider.getDimension());
    }
}
