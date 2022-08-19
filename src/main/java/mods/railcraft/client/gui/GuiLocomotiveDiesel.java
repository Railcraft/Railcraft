/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityLocomotiveDiesel;
import mods.railcraft.common.gui.containers.ContainerLocomotiveDiesel;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLocomotiveDiesel extends GuiLocomotive {

    public GuiLocomotiveDiesel(InventoryPlayer inv, EntityLocomotiveDiesel loco) {
        super(inv, loco, ContainerLocomotiveDiesel.make(inv, loco), "diesel", "gui_locomotive_diesel.png", 205, false);
    }

}
