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
import mods.railcraft.common.blocks.signals.IRouter;
import mods.railcraft.common.blocks.signals.RoutingLogic;
import mods.railcraft.common.gui.widgets.Widget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.gui.slots.*;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.ItemRoutingTable;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerRouting extends RailcraftContainer {

    private final IRouter router;
    private final InventoryPlayer playerInv;
    private int lastLockState;
    private int lastRoutingState;
    public String ownerName;
    public boolean canLock = false;
    private final SlotSecure slotTicket;
    public RoutingLogic logic;
    public Widget errorElement;

    public ContainerRouting(InventoryPlayer playerInv, IRouter route) {
        super(route.getInventory());
        this.router = route;
        this.playerInv = playerInv;

        errorElement = new Widget(16, 24, 176, 0, 16, 16) {
            @Override
            public ToolTip getToolTip() {
                if (router.getLogic() != null && router.getLogic().getError() != null)
                    return router.getLogic().getError().getToolTip();
                return null;
            }

        };
        errorElement.hidden = true;
        addWidget(errorElement);

        slotTicket = new SlotSecure(ItemRoutingTable.FILTER, route.getInventory(), 0, 35, 24) {
            @Override
            public void onSlotChanged() {
                super.onSlotChanged();
                router.resetLogic();
            }

        };
        slotTicket.setToolTips(ToolTip.buildToolTip("routing.tip.slot"));
        addSlot(slotTicket);

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(playerInv, k + i * 9 + 9, 8 + k * 18, 78 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(playerInv, j, 8 + j * 18, 136));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);

        icrafting.sendProgressBarUpdate(this, 0, router.getLockController().getCurrentState());
        icrafting.sendProgressBarUpdate(this, 1, router.getRoutingController().getCurrentState());

        canLock = PlayerPlugin.isOwnerOrOp(router.getOwner(), playerInv.player);
        slotTicket.locked = router.isSecure() && !canLock;
        icrafting.sendProgressBarUpdate(this, 2, canLock ? 1 : 0);

        String username = router.getOwner().getName();
        if (username != null)
            PacketBuilder.instance().sendGuiStringPacket((EntityPlayerMP) icrafting, windowId, 0, username);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
            ICrafting var2 = (ICrafting) this.crafters.get(var1);

            int lock = router.getLockController().getCurrentState();
            if (this.lastLockState != lock)
                var2.sendProgressBarUpdate(this, 0, lock);

            int railwayType = router.getRoutingController().getCurrentState();
            if (this.lastRoutingState != railwayType)
                var2.sendProgressBarUpdate(this, 1, railwayType);
        }

        this.lastLockState = router.getLockController().getCurrentState();
        this.lastRoutingState = router.getRoutingController().getCurrentState();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {

        switch (id) {
            case 0:
                router.getLockController().setCurrentState(value);
                break;
            case 1:
                router.getRoutingController().setCurrentState(value);
                break;
            case 2:
                canLock = value == 1;
                break;
        }
        slotTicket.locked = router.isSecure() && !canLock;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateString(byte id, String data) {
        switch (id) {
            case 0:
                ownerName = data;
                break;
        }
    }

}
