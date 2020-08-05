/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.logic.SteamTurbineLogic;
import mods.railcraft.common.blocks.structures.TileSteamTurbine;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.widgets.AnalogWidget;
import mods.railcraft.common.gui.widgets.ChargeNetworkIndicator;
import mods.railcraft.common.gui.widgets.IndicatorController;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

public class ContainerTurbine extends RailcraftContainer {

    public static final int GUI_HEIGHT = 140;
    public final TileSteamTurbine tile;
    private final SteamTurbineLogic logic;

    public ContainerTurbine(InventoryPlayer inventoryplayer, TileSteamTurbine tile) {
        this.tile = tile;
        this.logic = tile.getLogic(SteamTurbineLogic.class).orElseThrow(() -> new IllegalArgumentException("Expected Logic Object"));
        IInventory inv = logic.getInventory();

        addWidget(new AnalogWidget(new IndicatorController() {
            double clientValue;

            @Override
            protected void refreshToolTip() {
                ToolTip newTip = ToolTip.buildToolTip("gui.railcraft.steam.turbine.gauge", "{percent}=" + String.format("%.0f%%", clientValue * 100.0));
                getToolTip().set(newTip);
            }

            @Override
            public double getServerValue() {
                return logic.operatingRatio;
            }

            @Override
            public double getClientValue() {
                return clientValue;
            }

            @Override
            public void setClientValue(double value) {
                this.clientValue = value;
            }
        }, 137, 19, 28, 14, 99, 65));
        addWidget(new AnalogWidget(new ChargeNetworkIndicator(tile.getWorld(), tile.getPos()), 137, 38, 28, 14, 99, 65));

        addSlot(new SlotStackFilter(StackFilters.of(RailcraftItems.TURBINE_ROTOR), inv, 0, 60, 24)
                .setStackLimit(1));

        addPlayerSlots(inventoryplayer, GUI_HEIGHT);
    }
}
