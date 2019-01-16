/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.gui.containers.ContainerCartRF;

public class GuiCartForgeEnergy extends GuiTitled {
    public GuiCartForgeEnergy(EntityCartRF cart) {
        super(cart, new ContainerCartRF(cart), "gui_rf_device.png");
        ySize = 88;
        drawInvTitle = false;
    }

}
