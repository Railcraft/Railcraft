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
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitRouting;
import mods.railcraft.common.gui.buttons.LockButtonState;
import mods.railcraft.common.gui.containers.ContainerTrackRouting;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiTrackRouting extends GuiTitled {

    private GuiMultiButton lockButton;
    private final TrackKitRouting track;
    private ToolTip lockedToolTips;
    private ToolTip unlockedToolTips;
    private ToolTip notOwnedToolTips;
    private String ownerName = RailcraftConstantsAPI.UNKNOWN_PLAYER;

    public GuiTrackRouting(InventoryPlayer inv, TrackKitRouting track) {
        super(track.getTile(), new ContainerTrackRouting(inv, track), "gui_track_routing.png");
        ySize = 140;
        this.track = track;
        lockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.locked", "{owner}=" + ownerName);
        unlockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.unlocked", "{owner}=" + ownerName);
        notOwnedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.notowner", "{owner}=" + ownerName);
    }

    @Override
    public void initGui() {
        super.initGui();
        if (track == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(lockButton = GuiMultiButton.create(8, w + 152, h + 8, 16, track.getLockController()));
        lockButton.enabled = ((ContainerTrackRouting) container).canLock;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (track == null)
            return;
        updateButtons();
//        sendUpdatePacket();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        updateButtons();
    }

    private void updateButtons() {
        lockButton.enabled = ((ContainerTrackRouting) container).canLock;
        String username = ((ContainerTrackRouting) container).ownerName;
        if (username != null && !username.equals(ownerName)) {
            ownerName = username;
            lockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.locked", "{owner}=" + username);
            unlockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.unlocked", "{owner}=" + username);
            notOwnedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.notowner", "{owner}=" + username);
        }
        lockButton.setToolTip(track.getLockController().getButtonState() == LockButtonState.LOCKED ? lockedToolTips : lockButton.enabled ? unlockedToolTips : notOwnedToolTips);
    }

    @Override
    public void onGuiClosed() {
        sendUpdatePacket();
    }

    private void sendUpdatePacket() {
        PacketBuilder.instance().sendGuiReturnPacket(track.getTile());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.routing.track.slot.label"), 64, 29, 0x404040);
    }

}
