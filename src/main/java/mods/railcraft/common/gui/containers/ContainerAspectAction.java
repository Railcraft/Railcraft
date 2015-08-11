/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.UUID;
import mods.railcraft.common.blocks.signals.IAspectActionManager;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import net.minecraft.inventory.ICrafting;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerAspectAction extends RailcraftContainer {

    private final IAspectActionManager actionManager;
    private final EntityPlayer player;
    private int lastLockState;
    public boolean canLock;
    public String ownerName;

    public ContainerAspectAction(EntityPlayer player, IAspectActionManager actionManager) {
        super();
        this.actionManager = actionManager;
        this.player = player;
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);

        icrafting.sendProgressBarUpdate(this, 0, actionManager.getLockController().getCurrentState());
        icrafting.sendProgressBarUpdate(this, 1, PlayerPlugin.isOwnerOrOp(actionManager.getOwner(), player) ? 1 : 0);

        String username = actionManager.getOwner().getName();
        if (username != null)
            PacketBuilder.instance().sendGuiStringPacket((EntityPlayerMP) icrafting, windowId, 0, username);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
            ICrafting var2 = (ICrafting) this.crafters.get(var1);

            int lock = actionManager.getLockController().getCurrentState();
            if (this.lastLockState != lock)
                var2.sendProgressBarUpdate(this, 0, lock);
        }

        this.lastLockState = actionManager.getLockController().getCurrentState();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 0:
                actionManager.getLockController().setCurrentState(value);
                break;
            case 1:
                canLock = value == 1;
                break;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateString(byte id, String data) {
        switch (id) {
            case 0:
                try {
                    ownerName = data;
                } catch (IllegalArgumentException ex) {
                }
                break;
        }
    }

}
