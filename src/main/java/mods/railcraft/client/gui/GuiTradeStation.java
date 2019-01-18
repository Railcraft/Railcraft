/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiSimpleButton;
import mods.railcraft.common.blocks.logic.TradeStationLogic;
import mods.railcraft.common.blocks.logic.TradeStationLogic.GuiPacketType;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.containers.ContainerTradeStation;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.VillagerPlugin;
import mods.railcraft.common.util.collections.RevolvingList;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

import static mods.railcraft.common.blocks.logic.TradeStationLogic.GuiPacketType.*;

public class GuiTradeStation extends GuiContainerRailcraft {

    private final IWorldNameable owner;
    private final RevolvingList<VillagerRegistry.VillagerProfession> professions = new RevolvingList<>();
    private final RevolvingList<VillagerRegistry.VillagerCareer> careers = new RevolvingList<>();
    private final EntityVillager villager;
    private final IWorldNameable namer;

    public GuiTradeStation(InventoryPlayer playerInv, TradeStationLogic logic, IWorldNameable namer) {
        super(new ContainerTradeStation(playerInv, logic), "gui_trade_station.png");
        this.owner = namer;
        this.namer = namer;
        xSize = 176;
        ySize = 214;

        villager = new EntityVillager(logic.theWorldAsserted());

        professions.addAll(ForgeRegistries.VILLAGER_PROFESSIONS.getValuesCollection());
        VillagerRegistry.VillagerProfession profession = logic.getProfession();
        professions.setCurrent(profession);

        VillagerRegistry.VillagerCareer career = logic.getCareer();
        setupCareer(profession, career);

        villager.setProfession(profession);
        VillagerPlugin.setCareer(villager, career);
    }

    private void setupCareer(VillagerRegistry.VillagerProfession profession) {
        setupCareer(profession, profession.getCareer(0));
    }

    private void setupCareer(VillagerRegistry.VillagerProfession profession, VillagerRegistry.VillagerCareer career) {
        careers.clear();
        careers.addAll(VillagerPlugin.getCareers(profession));
        careers.setCurrent(career);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        buttonList.add(new GuiSimpleButton(0, w + 118, h + 48, 10, StandardButtonTextureSets.LEFT_BUTTON, ""));
        buttonList.add(new GuiSimpleButton(1, w + 156, h + 48, 10, StandardButtonTextureSets.RIGHT_BUTTON, ""));
        buttonList.add(new GuiSimpleButton(5, w + 118, h + 64, 10, StandardButtonTextureSets.LEFT_BUTTON, ""));
        buttonList.add(new GuiSimpleButton(6, w + 156, h + 64, 10, StandardButtonTextureSets.RIGHT_BUTTON, ""));

        GuiSimpleButton[] dice = new GuiSimpleButton[3];

        ToolTip tip = ToolTip.buildToolTip("gui.railcraft.trade.station.dice.tips");
        if (!tip.isEmpty())
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

        VillagerRegistry.VillagerProfession profession;
        switch (button.id) {
            case 0:
                professions.rotateLeft();
                profession = professions.getCurrent();
                setupCareer(profession);
                sendUpdate(SET_PROFESSION, profession);
                break;
            case 1:
                professions.rotateRight();
                profession = professions.getCurrent();
                setupCareer(profession);
                sendUpdate(SET_PROFESSION, profession);
                break;
            case 2:
                sendUpdate(NEXT_TRADE, (byte) 0);
                break;
            case 3:
                sendUpdate(NEXT_TRADE, (byte) 1);
                break;
            case 4:
                sendUpdate(NEXT_TRADE, (byte) 2);
                break;
            case 5:
                careers.rotateLeft();
                sendUpdate(SET_CAREER, careers.getCurrent());
                break;
            case 6:
                careers.rotateRight();
                sendUpdate(SET_CAREER, careers.getCurrent());
                break;
        }

        villager.setProfession(professions.getCurrent());
        VillagerPlugin.setCareer(villager, careers.getCurrent());
    }

    public void sendUpdate(GuiPacketType type, Object... args) {
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
                    data.writeUTF(Objects.requireNonNull(((VillagerRegistry.VillagerProfession) arg).getRegistryName()).toString());
                } else if (arg instanceof VillagerRegistry.VillagerCareer) {
                    data.writeInt(VillagerPlugin.getCareerId((VillagerRegistry.VillagerCareer) arg));
                }
            }
        } catch (IOException ignored) {
        }
        PacketBuilder.instance().sendGuiReturnPacket((IGuiReturnHandler) owner, bytes.toByteArray());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GuiTools.drawStringCenteredAtPos(fontRenderer, namer.getDisplayName().getFormattedText(), 55, 6);
        GuiTools.drawStringCenteredAtPos(fontRenderer, villager.getDisplayName().getFormattedText(), 142, 14, 0xFFFFFF, true);
        GuiTools.drawVillager(villager, 141, 79, 27, (float) (guiLeft + 87) - mouseX, (float) (guiTop + 91 - 50) - mouseY);
    }

}
