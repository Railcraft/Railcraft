/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import mods.railcraft.common.gui.containers.ContainerLocomotive;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SideOnly(Side.CLIENT)
public abstract class GuiLocomotive extends GuiTitled {

    private final EntityLocomotive loco;
    private final String typeTag;
    private final Map<LocoMode, GuiToggleButtonSmall> modeButtons = new LinkedHashMap<>();
    private final List<GuiToggleButtonSmall> speedButtons = new ArrayList<>();
    private GuiMultiButton lockButton;
    private ToolTip lockedToolTips;
    private ToolTip unlockedToolTips;
    private ToolTip privateToolTips;
    private String locoOwner;

    protected GuiLocomotive(InventoryPlayer inv, EntityLocomotive loco, ContainerLocomotive container, String typeTag, String guiName, int guiHeight, boolean hasIdleMode) {
        super(loco, container, guiName);
        ySize = guiHeight;
        this.loco = loco;
        this.typeTag = typeTag;
        loco.clientMode = loco.getMode();
        loco.clientSpeed = loco.getSpeed();
        lockedToolTips = ToolTip.buildToolTip("gui.railcraft.locomotive.tips.button.locked", "{owner}=[Unknown]");
        unlockedToolTips = ToolTip.buildToolTip("gui.railcraft.locomotive.tips.button.unlocked", "{owner}=[Unknown]");
        privateToolTips = ToolTip.buildToolTip("gui.railcraft.locomotive.tips.button.private", "{owner}=[Unknown]");
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
            GuiToggleButtonSmall button = new GuiToggleButtonSmall(id++, 0, h + ySize - 129, 55, LocalizationPlugin.translate("gui.railcraft.locomotive.mode." + mode.getName()), loco.clientMode == mode);
            button.setClickConsumer(b -> loco.clientMode = mode);
            button.setStatusUpdater(b -> b.active = loco.clientMode == mode);
            button.setToolTip(ToolTip.buildToolTip("gui.railcraft.locomotive." + typeTag + ".tips.button.mode." + mode.getName()));
            modeButtons.put(mode, button);
        }
        GuiTools.newButtonRowAuto(buttonList, w + 3, 171, modeButtons.values());

        GuiToggleButtonSmall reverseButton = new GuiToggleButtonSmall(id++, 0, h + ySize - 112, 12, "R", loco.isReverse());
        reverseButton.setClickConsumer(b -> loco.setReverse(!loco.isReverse()));
        reverseButton.setStatusUpdater(b -> b.active = loco.isReverse());
        speedButtons.add(reverseButton);
        for (LocoSpeed speed : LocoSpeed.VALUES) {
            String label = IntStream.range(0, speed.getLevel()).mapToObj(i -> ">").collect(Collectors.joining());
            GuiToggleButtonSmall button = new GuiToggleButtonSmall(id++, 0, h + ySize - 112, 7 + speed.getLevel() * 5, label, loco.clientSpeed == speed);
            button.setClickConsumer(b -> loco.clientSpeed = speed);
            button.setStatusUpdater(b -> b.active = loco.clientSpeed == speed);
            speedButtons.add(button);
        }
        GuiTools.newButtonRow(buttonList, w + 8, 3, speedButtons);

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
            lockedToolTips = ToolTip.buildToolTip("gui.railcraft.locomotive.tips.button.locked", "{owner}=" + ownerName);
            unlockedToolTips = ToolTip.buildToolTip("gui.railcraft.locomotive.tips.button.unlocked", "{owner}=" + ownerName);
            privateToolTips = ToolTip.buildToolTip("gui.railcraft.locomotive.tips.button.private", "{owner}=" + ownerName);
        }
    }

    @Override
    public void onGuiClosed() {
        sendUpdatePacket();
    }

    private void sendUpdatePacket() {
        PacketBuilder.instance().sendGuiReturnPacket(loco);
    }
}
