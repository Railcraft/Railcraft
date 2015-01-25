/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiMultiButton;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import net.minecraft.entity.player.InventoryPlayer;
import mods.railcraft.common.gui.containers.ContainerLocomotiveElectric;
import net.minecraft.entity.player.EntityPlayer;

public class GuiLocomotiveElectric extends GuiLocomotive {

    private final EntityLocomotiveElectric loco;
    private final EntityPlayer player;

    public GuiLocomotiveElectric(InventoryPlayer inv, EntityLocomotiveElectric loco) {
        super(inv, loco, new ContainerLocomotiveElectric(inv, loco), "electric", "gui_locomotive_electric.png", 161, false);
        this.loco = loco;
        this.player = inv.player;
    }
    
    @Override
    protected GuiMultiButton getLockButton(int w, int h) {
    	return new GuiMultiButton(8, w + 152, h + 10, 16, loco.getLockController());
    }

}
