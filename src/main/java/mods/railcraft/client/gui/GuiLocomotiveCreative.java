/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityLocomotiveCreative;
import mods.railcraft.common.gui.containers.ContainerLocomotiveCreative;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiLocomotiveCreative extends GuiLocomotive {

    private final EntityLocomotiveCreative loco;
    private final EntityPlayer player;

    public GuiLocomotiveCreative(InventoryPlayer inv, EntityLocomotiveCreative loco) {
        super(inv, loco, ContainerLocomotiveCreative.make(inv, loco), "creative", "gui_locomotive_creative.png", 161, false);
        this.loco = loco;
        this.player = inv.player;
    }

}
