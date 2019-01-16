/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import com.mojang.authlib.GameProfile;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.gui.buttons.LockButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.misc.ISecureObject;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class TrackKitSecured extends TrackKitRailcraft implements IGuiReturnHandler, ISecureObject<LockButtonState> {
    private final MultiButtonController<LockButtonState> lockController = MultiButtonController.create(0, LockButtonState.VALUES);

    @Override
    public @Nullable World theWorld() {
        return getTile().getWorld();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        lockController.writeToNBT(data, "lock");
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        lockController.readFromNBT(data, "lock");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(lockController.getCurrentState());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        lockController.setCurrentState(data.readByte());
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeByte(lockController.getCurrentState());
    }

    @Override
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        byte lock = data.readByte();
        if (sender == null || canAccess(sender.getGameProfile()))
            lockController.setCurrentState(lock);
    }

    private boolean canAccess(GameProfile player) {
        return !isSecure() || PlayerPlugin.isOwnerOrOp(getOwner(), player);
    }

    @Override
    public MultiButtonController<LockButtonState> getLockController() {
        return lockController;
    }

    @Override
    public boolean isSecure() {
        return lockController.getButtonState() == LockButtonState.LOCKED;
    }

    @Override
    public boolean isProtected() {
        return isSecure();
    }

    @Override
    public String getName() {
        return ((TileTrackOutfitted) getTile()).getName();
    }

    @Override
    public boolean hasCustomName() {
        return ((TileTrackOutfitted) getTile()).hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return getTile().getDisplayName();
    }

    @Override
    public GameProfile getOwner() {
        return ((TileTrackOutfitted) getTile()).getOwner();
    }
}
