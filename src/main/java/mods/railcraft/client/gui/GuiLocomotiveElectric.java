/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityLocomotiveElectric;
import mods.railcraft.common.gui.containers.ContainerLocomotiveElectric;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLocomotiveElectric extends GuiLocomotive {

    public GuiLocomotiveElectric(InventoryPlayer inv, EntityLocomotiveElectric loco) {
        super(inv, loco, ContainerLocomotiveElectric.make(inv, loco), "electric", "gui_locomotive_electric.png", 161, false);
    }

}
