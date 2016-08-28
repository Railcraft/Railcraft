/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiMultiButton;
import mods.railcraft.client.gui.buttons.GuiToggleButtonSmall;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityLocomotive.LocoMode;
import mods.railcraft.common.carts.EntityLocomotive.LocoSpeed;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerLocomotive;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class GuiLocomotive extends EntityGui {

    private final EntityLocomotive loco;
    private final EntityPlayer player;
    private final String typeTag;
    private Map<LocoMode, GuiToggleButtonSmall> modeButtons = new LinkedHashMap<>();
    private Map<LocoSpeed, GuiToggleButtonSmall> speedButtons = new LinkedHashMap<>();
    private GuiMultiButton lockButton;
    private ToolTip lockedToolTips;
    private ToolTip unlockedToolTips;
    private ToolTip privateToolTips;
    private String locoOwner;

    protected GuiLocomotive(InventoryPlayer inv, EntityLocomotive loco, ContainerLocomotive container, String typeTag, String guiName, int guiHeight, boolean hasIdleMode) {
        super(loco, container, RailcraftConstants.GUI_TEXTURE_FOLDER + guiName);
        ySize = guiHeight;
        this.loco = loco;
        this.player = inv.player;
        this.typeTag = typeTag;
        loco.clientMode = loco.getMode();
        loco.clientSpeed = loco.getSpeed();
        lockedToolTips = ToolTip.buildToolTip("railcraft.gui.locomotive.tip.button.locked", "{owner}=[Unknown]");
        unlockedToolTips = ToolTip.buildToolTip("railcraft.gui.locomotive.tip.button.unlocked", "{owner}=[Unknown]");
        privateToolTips = ToolTip.buildToolTip("railcraft.gui.locomotive.tip.button.private", "{owner}=[Unknown]");
    }

    @Override
    public void initGui() {
        super.initGui();
        if (loco == null)
            return;
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        int id = 0;

        for (LocoMode mode : loco.getAllowedModes()) {
            GuiToggleButtonSmall button = new GuiToggleButtonSmall(id++, 0, h + ySize - 129, 55, LocalizationPlugin.translate("railcraft.gui.locomotive.mode." + mode.getName()), loco.clientMode == mode);
            button.setClickConsumer(b -> loco.clientMode = mode);
            button.setStatusUpdater(b -> b.active = loco.clientMode == mode);
            button.setToolTip(ToolTip.buildToolTip("railcraft.gui.locomotive." + typeTag + ".tip.button.mode." + mode.getName()));
            modeButtons.put(mode, button);
        }
        GuiTools.newButtonRowAuto(buttonList, w + 3, 171, modeButtons.values());

        for (LocoSpeed speed : loco.getAllowedSpeeds()) {
            String label = "";
            for (int i = 0; i < speed.getLevelAbs(); i++) {
                label += speed.getLevel() > 0 ? ">" : "<";
            }
            GuiToggleButtonSmall button = new GuiToggleButtonSmall(id++, 0, h + ySize - 112, 7 + speed.getLevelAbs() * 5, label, loco.clientSpeed == speed);
            button.setClickConsumer(b -> loco.clientSpeed = speed);
            button.setStatusUpdater(b -> b.active = loco.clientSpeed == speed);
            speedButtons.put(speed, button);
        }
        GuiTools.newButtonRow(buttonList, w + 8, 3, speedButtons.values());

        buttonList.add(lockButton = GuiMultiButton.create(id, w + 152, h + ySize - 111, 16, loco.getLockController()));
        lockButton.enabled = loco.clientCanLock;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) throws IOException {
        if (loco == null)
            return;
        super.actionPerformed(guibutton);
        updateButtons();
        sendUpdatePacket();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        updateButtons();
    }

    private void updateButtons() {
        lockButton.enabled = loco.clientCanLock;
        lockButton.setToolTip(loco.isPrivate() ? privateToolTips : loco.isSecure() ? lockedToolTips : lockButton.enabled ? unlockedToolTips : null);
        String ownerName = ((ContainerLocomotive) container).ownerName;
        if (ownerName != null && !ownerName.equals(locoOwner)) {
            locoOwner = ownerName;
            lockedToolTips = ToolTip.buildToolTip("railcraft.gui.locomotive.tip.button.locked", "{owner}=" + ownerName);
            unlockedToolTips = ToolTip.buildToolTip("railcraft.gui.locomotive.tip.button.unlocked", "{owner}=" + ownerName);
            privateToolTips = ToolTip.buildToolTip("railcraft.gui.locomotive.tip.button.private", "{owner}=" + ownerName);
        }
    }

    @Override
    public void onGuiClosed() {
        sendUpdatePacket();
    }

    private void sendUpdatePacket() {
        PacketBuilder.instance().sendGuiReturnPacket(loco);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = loco.getName();
        GuiTools.drawCenteredString(fontRendererObj, name, 6);
        fontRendererObj.drawString(I18n.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

}
