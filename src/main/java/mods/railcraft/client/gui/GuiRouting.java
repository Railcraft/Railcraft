/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import mods.railcraft.client.gui.buttons.GuiMultiButton;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.signals.IRouter;
import mods.railcraft.common.blocks.signals.RoutingLogic;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.buttons.LockButtonState;
import mods.railcraft.common.gui.containers.ContainerRouting;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayer;

public class GuiRouting extends TileGui {

    private final IRouter router;
    private GuiMultiButton lockButton;
    private GuiMultiButton routingButton;
    private final EntityPlayer player;
    private final RailcraftTileEntity tile;
    private ToolTip lockedToolTips;
    private ToolTip unlockedToolTips;
    private ToolTip notownedToolTips;
    private ToolTip privateToolTips;
    private final ToolTip publicToolTips;
    private String ownerName = "[Unknown]";

    public GuiRouting(InventoryPlayer inv, RailcraftTileEntity tile, IRouter router) {
        super(tile, new ContainerRouting(inv, router), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_routing.png");
        ySize = 160;
        this.tile = tile;
        this.router = router;
        this.player = inv.player;
        lockedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.locked", "{owner}=" + ownerName);
        unlockedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.unlocked", "{owner}=" + ownerName);
        notownedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.notowner", "{owner}=" + ownerName);
        privateToolTips = ToolTip.buildToolTip("railcraft.gui.routing.type.private.tip", "{owner}=" + ownerName);
        publicToolTips = ToolTip.buildToolTip("railcraft.gui.routing.type.public.tip");
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

        buttonList.add(lockButton = new GuiMultiButton(8, w + 152, h + 8, 16, router.getLockController()));
        lockButton.enabled = ((ContainerRouting) container).canLock;

        buttonList.add(routingButton = new GuiMultiButton(8, w + 65, h + 50, 100, router.getRoutingController()));
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
        RoutingLogic logic = router.getLogic();
        con.errorElement.hidden = logic == null || logic.isValid();
    }

    private void updateButtons() {
        lockButton.enabled = ((ContainerRouting) container).canLock;
        routingButton.canChange = !router.isSecure() || ((ContainerRouting) container).canLock;
        String username = ((ContainerRouting) container).ownerName;
        if (username != null && !username.equals(ownerName)) {
            ownerName = username;
            lockedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.locked", "{owner}=" + username);
            unlockedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.unlocked", "{owner}=" + username);
            notownedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.notowner", "{owner}=" + username);
            privateToolTips = ToolTip.buildToolTip("railcraft.gui.routing.type.private.tip", "{owner}=" + username);
        }
        lockButton.setToolTip(router.getLockController().getButtonState() == LockButtonState.LOCKED ? lockedToolTips : lockButton.enabled ? unlockedToolTips : notownedToolTips);
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
        GuiTools.drawCenteredString(fontRendererObj, router.getName(), 6);
        fontRendererObj.drawString(LocalizationPlugin.translate("railcraft.gui.routing.slot.label"), 64, 29, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

}
