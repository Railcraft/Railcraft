/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.client.gui.buttons.GuiMultiButton;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.gui.buttons.LockButtonState;
import mods.railcraft.common.gui.containers.ContainerRouting;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.routing.IRouter;
import mods.railcraft.common.util.routing.RoutingLogic;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiRouting extends GuiTitled {

    private final IRouter router;
    private GuiMultiButton lockButton;
    private GuiMultiButton routingButton;
    private final TileRailcraft tile;
    private ToolTip lockedToolTips;
    private ToolTip unlockedToolTips;
    private ToolTip notOwnedToolTips;
    private ToolTip privateToolTips;
    private final ToolTip publicToolTips;
    private String ownerName = RailcraftConstantsAPI.UNKNOWN_PLAYER;

    public GuiRouting(InventoryPlayer inv, TileRailcraft tile, IRouter router) {
        super(tile, new ContainerRouting(inv, router), "gui_routing.png");
        ySize = 160;
        this.tile = tile;
        this.router = router;
        lockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.locked", "{owner}=" + ownerName);
        unlockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.unlocked", "{owner}=" + ownerName);
        notOwnedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.notowner", "{owner}=" + ownerName);
        privateToolTips = ToolTip.buildToolTip("gui.railcraft.routing.type.private.tips", "{owner}=" + ownerName);
        publicToolTips = ToolTip.buildToolTip("gui.railcraft.routing.type.public.tips");
    }

    @Override
    public void initGui() {
        super.initGui();
        if (router == null) {
            return;
        }
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(lockButton = GuiMultiButton.create(8, w + 152, h + 16, 16, router.getLockController()));
        lockButton.enabled = ((ContainerRouting) container).canLock;

        buttonList.add(routingButton = GuiMultiButton.create(8, w + 68, h + 50, 100, router.getRoutingController()));
        routingButton.canChange = false;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (router == null) {
            return;
        }
        updateButtons();
//        sendUpdatePacket();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        updateButtons();
        ContainerRouting con = (ContainerRouting) container;
        con.errorElement.hidden = router.getLogic().map(RoutingLogic::isValid).orElse(true);
    }

    private void updateButtons() {
        lockButton.enabled = ((ContainerRouting) container).canLock;
        routingButton.canChange = !router.isSecure() || ((ContainerRouting) container).canLock;
        String username = ((ContainerRouting) container).ownerName;
        if (username != null && !username.equals(ownerName)) {
            ownerName = username;
            lockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.locked", "{owner}=" + username);
            unlockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.unlocked", "{owner}=" + username);
            notOwnedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.notowner", "{owner}=" + username);
            privateToolTips = ToolTip.buildToolTip("gui.railcraft.routing.type.private.tips", "{owner}=" + username);
        }
        lockButton.setToolTip(router.getLockController().getButtonState() == LockButtonState.LOCKED ? lockedToolTips : lockButton.enabled ? unlockedToolTips : notOwnedToolTips);
        routingButton.setToolTip(router.getRoutingController().getButtonState() == IRouter.RoutingButtonState.PRIVATE ? privateToolTips : publicToolTips);
    }

    @Override
    public void onGuiClosed() {
        sendUpdatePacket();
    }

    private void sendUpdatePacket() {
        PacketBuilder.instance().sendGuiReturnPacket((IGuiReturnHandler) tile);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.routing.slot.label"), 64, 29, 0x404040);
    }

}
