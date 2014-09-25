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
import mods.railcraft.common.blocks.tracks.TrackRouting;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.buttons.LockButtonState;
import mods.railcraft.common.gui.containers.ContainerTrackRouting;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayer;

public class GuiTrackRouting extends TileGui {

    private GuiMultiButton lockButton;
    private final EntityPlayer player;
    private final TrackRouting track;
    private ToolTip lockedToolTips;
    private ToolTip unlockedToolTips;
    private ToolTip notownedToolTips;
    private String ownerName = "[Unknown]";

    public GuiTrackRouting(InventoryPlayer inv, TrackRouting track) {
        super((RailcraftTileEntity) track.getTile(), new ContainerTrackRouting(inv, track), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_track_routing.png");
        ySize = 140;
        this.track = track;
        this.player = inv.player;
        lockedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.locked", "{owner}=" + ownerName);
        unlockedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.unlocked", "{owner}=" + ownerName);
        notownedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.notowner", "{owner}=" + ownerName);
    }

    @Override
    public void initGui() {
        super.initGui();
        if (track == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(lockButton = new GuiMultiButton(8, w + 152, h + 8, 16, track.getLockController()));
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
            lockedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.locked", "{owner}=" + username);
            unlockedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.unlocked", "{owner}=" + username);
            notownedToolTips = ToolTip.buildToolTip("railcraft.gui.tip.button.lock.notowner", "{owner}=" + username);
        }
        lockButton.setToolTip(track.getLockController().getButtonState() == LockButtonState.LOCKED ? lockedToolTips : lockButton.enabled ? unlockedToolTips : notownedToolTips);
    }

    @Override
    public void onGuiClosed() {
        sendUpdatePacket();
    }

    private void sendUpdatePacket() {
        PacketBuilder.instance().sendGuiReturnPacket((IGuiReturnHandler) track.getTile());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, track.getName(), 6);
        fontRendererObj.drawString(LocalizationPlugin.translate("railcraft.gui.routing.track.slot.label"), 64, 29, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

}
