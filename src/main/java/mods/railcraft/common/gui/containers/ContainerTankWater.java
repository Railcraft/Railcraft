/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.blocks.logic.WaterGeneratorLogic;
import mods.railcraft.common.blocks.logic.WaterGeneratorLogic.GeneratorStatus;
import mods.railcraft.common.blocks.structures.TileTankWater;
import mods.railcraft.common.fluids.IFluidHandlerImplementor;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class ContainerTankWater extends RailcraftContainer {

    public ContainerTankWater(InventoryPlayer inventoryplayer, TileTankWater tile) {


        tile.getLogic(IFluidHandlerImplementor.class).ifPresent(tank -> {
            addWidget(new FluidGaugeWidget(tank.getTankManager().get(0), 35, 20, 176, 0, 48, 47) {
                GeneratorStatus status = new GeneratorStatus();

                ToolTip toolTip = new ToolTip() {
                    @Override
                    public void refresh() {
                        clear();
                        ToolTip tankToolTip = tank.getToolTip();
                        tankToolTip.refresh();
                        addAll(tankToolTip);
                        get(0).format = TextFormatting.BLUE;
                        newline();
                        add(new ToolTipLine(LocalizationPlugin.format("gui.railcraft.tank.sky", status.canSeeSky), TextFormatting.DARK_GRAY));
                        add(new ToolTipLine(LocalizationPlugin.format("gui.railcraft.tank.base", status.baseRate() * status.canSeeSky), TextFormatting.DARK_GRAY));
                        add(new ToolTipLine(LocalizationPlugin.format("gui.railcraft.tank.humidity", status.humidityMultiplier), TextFormatting.DARK_GRAY));
                        add(new ToolTipLine(LocalizationPlugin.format("gui.railcraft.tank.precipitation", status.precipitationMultiplier), TextFormatting.DARK_GRAY));
                        add(new ToolTipLine(LocalizationPlugin.format("gui.railcraft.tank.temp", -status.tempPenalty), TextFormatting.DARK_GRAY));
                        add(new ToolTipLine(LocalizationPlugin.format("gui.railcraft.tank.final",
                                (status.baseRate()
                                        * status.canSeeSky
                                        * status.humidityMultiplier
                                        * status.precipitationMultiplier)
                                        - status.tempPenalty
                        ), TextFormatting.DARK_GRAY));
                    }
                };

                @Override
                public void writeServerSyncData(IContainerListener listener, RailcraftOutputStream data) throws IOException {
                    super.writeServerSyncData(listener, data);
                    GeneratorStatus temp = new GeneratorStatus();
                    temp.precipitationMultiplier = 0.0;
                    temp.humidityMultiplier = 0.0;
                    tile.getLogic(StructureLogic.class).ifPresent(struct -> {
                        struct.getComponents().stream().flatMap(Streams.toType(TileLogic.class))
                                .map(t -> t.getLogic(WaterGeneratorLogic.class))
                                .flatMap(Streams.unwrap())
                                .map(l -> l.status)
                                .forEach(s -> {
                                    temp.canSeeSky += s.canSeeSky;
                                    if (s.canSeeSky > 0) {
                                        temp.humidityMultiplier += s.humidityMultiplier;
                                        temp.precipitationMultiplier += s.precipitationMultiplier;
                                        temp.tempPenalty += s.tempPenalty;
                                    }
                                });
                    });
                    if (temp.canSeeSky > 0) {
                        temp.humidityMultiplier /= temp.canSeeSky;
                        temp.precipitationMultiplier /= temp.canSeeSky;
                    }
                    temp.writeData(data);
                }

                @Override
                public void readServerSyncData(RailcraftInputStream data) throws IOException {
                    super.readServerSyncData(data);
                    status.readData(data);
                }

                @Override
                public ToolTip getToolTip() {
                    return toolTip;
                }
            });
        });

        tile.getLogic(IInventoryImplementor.class).ifPresent(inv -> {
            addSlot(new SlotRailcraft(inv, 0, 116, 18));
            addSlot(new SlotOutput(inv, 1, 140, 36));
            addSlot(new SlotOutput(inv, 2, 116, 54));
        });

        addPlayerSlots(inventoryplayer);
    }
}
