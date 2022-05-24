/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.blocks.structures.TileBoilerFirebox;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * Created by CovertJaguar on 9/5/2021 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ContainerBoiler extends ContainerLogic {
    protected final BoilerLogic boilerLogic;
    protected final InventoryLogic inv;
    protected final TankManager tankManager;

    public ContainerBoiler(InventoryPlayer inventoryplayer, ILogicContainer logicContainer) {
        super(logicContainer);
        boilerLogic = Logic.get(BoilerLogic.class, logicContainer);
        inv = Logic.get(InventoryLogic.class, logicContainer);
        tankManager = Logic.get(FluidLogic.class, logicContainer).getTankManager();

        addWidget(new FluidGaugeWidget(tankManager.get(0), 116, 23, 176, 0, 16, 47));
        addWidget(new FluidGaugeWidget(tankManager.get(1), 17, 23, 176, 0, 16, 47));

        addWidget(new IndicatorWidget(boilerLogic.heatIndicator, 40, 25, 176, 61, 6, 43));

        addSlot(new SlotRailcraft(inv, TileBoilerFirebox.SLOT_INPUT_FLUID, 143, 21)); // Water
        addSlot(new SlotRailcraft(inv, TileBoilerFirebox.SLOT_PROCESS_FLUID, 143, 38));
        addSlot(new SlotOutput(inv, TileBoilerFirebox.SLOT_OUTPUT_FLUID, 143, 56));

        addPlayerSlots(inventoryplayer);
    }
}
