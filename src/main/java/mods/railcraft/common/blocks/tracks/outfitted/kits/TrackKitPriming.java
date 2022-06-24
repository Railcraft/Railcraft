/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.carts.IExplosiveCart;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitPriming extends TrackKitPowered implements IGuiReturnHandler {

    public static final short MAX_FUSE = 500;
    public static final short MIN_FUSE = 0;
    private short fuse = 80;

    @Override
    public @Nullable World theWorld() {
        return super.theWorld();
    }

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.PRIMING;
    }

    @Override
    protected EnumGui getGUI() {
        return EnumGui.TRACK_PRIMING;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered()) {
            if (cart instanceof IExplosiveCart) {
                IExplosiveCart tnt = (IExplosiveCart) cart;
                tnt.setFuse(fuse);
                tnt.setPrimed(true);
            }
        }
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeShort(fuse);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        fuse = data.readShort();

        markBlockNeedsUpdate();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setShort("delay", getFuse());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        setFuse(data.getShort("delay"));
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeShort(fuse);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        fuse = data.readShort();
    }

    public short getFuse() {
        return fuse;
    }

    public void setFuse(short f) {
        f = (short) Math.max(f, MIN_FUSE);
        f = (short) Math.min(f, MAX_FUSE);
        fuse = f;
    }
}
