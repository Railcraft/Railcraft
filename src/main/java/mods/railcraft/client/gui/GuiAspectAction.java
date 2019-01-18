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
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.client.gui.buttons.GuiMultiButton;
import mods.railcraft.client.gui.buttons.GuiToggleButton;
import mods.railcraft.common.blocks.interfaces.ITileAspectResponder;
import mods.railcraft.common.gui.buttons.LockButtonState;
import mods.railcraft.common.gui.containers.ContainerAspectAction;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

public class GuiAspectAction extends GuiContainerRailcraft {

    private final ITileAspectResponder actionManager;
    private final String title;
    private final boolean[] aspects = new boolean[SignalAspect.values().length];
    private GuiMultiButton lockButton;
    private boolean changed;
    private ToolTip lockedToolTips;
    private ToolTip unlockedToolTips;
    private ToolTip notOwnedToolTips;
    public String ownerName = RailcraftConstantsAPI.UNKNOWN_PLAYER;

    public GuiAspectAction(EntityPlayer player, ITileAspectResponder actionManager, String title) {
        this(player, actionManager, title, "gui_basic.png");
        ySize = 88;
    }

    protected GuiAspectAction(EntityPlayer player, ITileAspectResponder actionManager, String title, String texture) {
        super(new ContainerAspectAction(player, actionManager), texture);
        this.actionManager = actionManager;
        this.title = title;
        for (SignalAspect aspect : SignalAspect.values()) {
            aspects[aspect.ordinal()] = actionManager.doesActionOnAspect(aspect);
        }
        lockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.locked", "{owner}=" + ownerName);
        unlockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.unlocked", "{owner}=" + ownerName);
        notOwnedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.notowner", "{owner}=" + ownerName);
    }

    @Override
    public void initGui() {
        super.initGui();
        if (actionManager == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        buttonList.add(new GuiToggleButton(0, w + 7, h + 30, 50, LocalizationPlugin.translate(SignalAspect.GREEN.getLocalizationTag()), aspects[SignalAspect.GREEN.ordinal()]));
        buttonList.add(new GuiToggleButton(1, w + 12, h + 55, 70, LocalizationPlugin.translate(SignalAspect.BLINK_YELLOW.getLocalizationTag()), aspects[SignalAspect.BLINK_YELLOW.ordinal()]));
        buttonList.add(new GuiToggleButton(2, w + 63, h + 30, 50, LocalizationPlugin.translate(SignalAspect.YELLOW.getLocalizationTag()), aspects[SignalAspect.YELLOW.ordinal()]));
        buttonList.add(new GuiToggleButton(3, w + 94, h + 55, 70, LocalizationPlugin.translate(SignalAspect.BLINK_RED.getLocalizationTag()), aspects[SignalAspect.BLINK_RED.ordinal()]));
        buttonList.add(new GuiToggleButton(4, w + 119, h + 30, 50, LocalizationPlugin.translate(SignalAspect.RED.getLocalizationTag()), aspects[SignalAspect.RED.ordinal()]));
        buttonList.add(lockButton = GuiMultiButton.create(5, w + 152, h + 8, 16, actionManager.getLockController()));
        lockButton.enabled = false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GuiTools.drawCenteredString(fontRenderer, title, 15);
    }

    @Override
    protected final void actionPerformed(GuiButton button) {
        if (actionManager == null)
            return;
        if (!button.enabled)
            return;
        if (!canChange())
            return;
        markChanged();
        onButtonPressed(button);
    }

    protected void onButtonPressed(GuiButton button) {
        if (button.id <= 4) {
            SignalAspect aspect = SignalAspect.VALUES[button.id];
            aspects[aspect.ordinal()] = !aspects[aspect.ordinal()];
            ((GuiToggleButton) button).active = aspects[aspect.ordinal()];
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        lockButton.enabled = ((ContainerAspectAction) container).canLock;
        lockButton.setToolTip(actionManager.getLockController().getButtonState() == LockButtonState.LOCKED ? lockedToolTips : lockButton.enabled ? unlockedToolTips : notOwnedToolTips);
        String username = ((ContainerAspectAction) container).ownerName;
        if (username != null && !username.equals(ownerName)) {
            ownerName = username;
            lockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.locked", "{owner}=" + username);
            unlockedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.unlocked", "{owner}=" + username);
            notOwnedToolTips = ToolTip.buildToolTip("gui.railcraft.tips.button.lock.notowner", "{owner}=" + username);
        }
    }

    @Override
    public void onGuiClosed() {
        if (changed && actionManager instanceof IGuiReturnHandler && canChange()) {
            prepareForPacket();
            PacketGuiReturn pkt = new PacketGuiReturn((IGuiReturnHandler) actionManager);
            PacketDispatcher.sendToServer(pkt);
        }
    }

    protected void prepareForPacket() {
        for (SignalAspect aspect : SignalAspect.VALUES) {
            actionManager.doActionOnAspect(aspect, aspects[aspect.ordinal()]);
        }
    }

    public void markChanged() {
        changed = true;
    }

    public boolean canChange() {
        return actionManager.getLockController().getButtonState() == LockButtonState.UNLOCKED || ((ContainerAspectAction) container).canLock;
    }

}
