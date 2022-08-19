/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.EntityLocomotiveDiesel;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
//import mods.railcraft.common.gui.slots.SlotWaterLimited;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;

//import mods.railcraft.common.util.misc.Game;
//import org.apache.logging.log4j.Level;

public class ContainerLocomotiveDiesel extends ContainerLocomotive {

    private final EntityLocomotiveDiesel loco;

    private ContainerLocomotiveDiesel(InventoryPlayer playerInv, EntityLocomotiveDiesel loco) {
        super(playerInv, loco, 205);
        this.loco = loco;
    }

    public static ContainerLocomotiveDiesel make(InventoryPlayer playerInv, EntityLocomotiveDiesel loco) {
        ContainerLocomotiveDiesel con = new ContainerLocomotiveDiesel(playerInv, loco);
        con.init();
        return con;
    }

    @Override
    public void defineSlotsAndWidgets() {
        addWidget(new FluidGaugeWidget(loco.getTankManager().get(0), 30, 23, 176, 0, 16, 47));

        addWidget(new IndicatorWidget(loco.engine.heatIndicator, 17, 25, 176, 61, 6, 43));

        addSlot(new SlotRailcraft(loco, 0, 152, 20));
        addSlot(new SlotOutput(loco, 1, 152, 56));
        addSlot(new SlotOutput(loco, 2, 116, 56));
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 22, (int) Math.round(loco.engine.getTemp()));
    }

}
