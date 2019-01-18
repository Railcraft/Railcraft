/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityLocomotiveCreative;
import mods.railcraft.common.gui.containers.ContainerLocomotive;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class GuiLocomotiveCreative extends GuiLocomotive {

    private static final int GUI_HEIGHT = 161;

    GuiLocomotiveCreative(InventoryPlayer inv, EntityLocomotiveCreative loco) {
        super(inv, loco, ContainerLocomotive.make(inv, loco), "creative", "gui_locomotive_creative.png", GUI_HEIGHT, false);
    }

}
