/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiSimpleButton;
import mods.railcraft.common.blocks.machine.alpha.TileTradeStation;
import mods.railcraft.common.blocks.machine.alpha.TileTradeStation.GuiPacketType;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.containers.ContainerTradeStation;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.util.collections.RevolvingList;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static mods.railcraft.common.blocks.machine.alpha.TileTradeStation.GuiPacketType.NEXT_TRADE;
import static mods.railcraft.common.blocks.machine.alpha.TileTradeStation.GuiPacketType.SET_PROFESSION;

public class GuiTradeStation extends TileGui {

    private final String label;
    private final TileTradeStation tile;
    private final RevolvingList<VillagerRegistry.VillagerProfession> professions = new RevolvingList<VillagerRegistry.VillagerProfession>();
    private final EntityVillager villager;

    public GuiTradeStation(InventoryPlayer playerInv, TileTradeStation tile) {
        super(tile, new ContainerTradeStation(playerInv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_trade_station.png");
        xSize = 176;
        ySize = 214;

        this.tile = tile;

        label = tile.getName();

        villager = new EntityVillager(tile.getWorld());

        professions.addAll(VillagerRegistry.instance().getRegistry().getValues());

        professions.setCurrent(tile.getProfession());
        villager.setProfession(professions.getCurrent());
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(new GuiSimpleButton(0, w + 118, h + 64, 10, StandardButtonTextureSets.LEFT_BUTTON, ""));
        buttonList.add(new GuiSimpleButton(1, w + 156, h + 64, 10, StandardButtonTextureSets.RIGHT_BUTTON, ""));

        GuiSimpleButton[] dice = new GuiSimpleButton[3];

        ToolTip tip = ToolTip.buildToolTip("gui.railcraft.trade.station.dice.tips");
        if (tip != null)
            tip.get(0).format = TextFormatting.YELLOW;

        for (int b = 0; b < 3; b++) {
            dice[b] = new GuiSimpleButton(2 + b, w + 93, h + 24 + 21 * b, 16, StandardButtonTextureSets.DICE_BUTTON, "");
            dice[b].setToolTip(tip);
            buttonList.add(dice[b]);
        }

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        switch (button.id) {
            case 0:
                professions.rotateLeft();
                sendUpdateToTile(SET_PROFESSION, professions.getCurrent());
                break;
            case 1:
                professions.rotateRight();
                sendUpdateToTile(SET_PROFESSION, professions.getCurrent());
                break;
            case 2:
                sendUpdateToTile(NEXT_TRADE, (byte) 0);
                break;
            case 3:
                sendUpdateToTile(NEXT_TRADE, (byte) 1);
                break;
            case 4:
                sendUpdateToTile(NEXT_TRADE, (byte) 2);
                break;
        }

        villager.setProfession(professions.getCurrent());
    }

    public void sendUpdateToTile(GuiPacketType type, Object... args) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        try {
            data.writeByte(type.ordinal());
            for (Object arg : args) {
                if (arg instanceof Integer)
                    data.writeInt((Integer) arg);
                else if (arg instanceof Byte)
                    data.writeByte((Byte) arg);
                else if (arg instanceof VillagerRegistry.VillagerProfession) {
                    data.writeUTF(((VillagerRegistry.VillagerProfession) arg).getRegistryName().toString());
                }
            }
        } catch (IOException ignored) {
        }
        PacketBuilder.instance().sendGuiReturnPacket(tile, bytes.toByteArray());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        GuiTools.drawVillager(villager, 141, 79, 30, (float) (guiLeft + 87) - mouseX, (float) (guiTop + 91 - 50) - mouseY);
    }

}
