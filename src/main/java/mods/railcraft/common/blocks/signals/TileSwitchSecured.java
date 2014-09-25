/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import com.mojang.authlib.GameProfile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.common.gui.buttons.LockButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileSwitchSecured extends TileSwitchBase implements IGuiReturnHandler, ISecure<LockButtonState> {

    private final MultiButtonController<LockButtonState> lockController = new MultiButtonController(0, LockButtonState.VALUES);

    @Override
    public MultiButtonController<LockButtonState> getLockController() {
        return lockController;
    }

    @Override
    public float getHardness() {
        if (isSecure()) {
            return -1;
        }
        return super.getHardness();
    }

    @Override
    public boolean isSecure() {
        return lockController.getButtonState() == LockButtonState.LOCKED;
    }

    protected boolean canAccess(GameProfile player) {
        return !isSecure() || PlayerPlugin.isOwnerOrOp(getOwner(), player);
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
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeByte(lockController.getCurrentState());
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        byte lock = data.readByte();
        if (sender == null || canAccess(sender.getGameProfile())) {
            lockController.setCurrentState(lock);
        }
    }

}
