/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import mods.railcraft.common.carts.EntityLocomotiveSteamSolid;
import mods.railcraft.common.gui.containers.ContainerLocomotiveSteamSolid;
import net.minecraft.entity.player.EntityPlayer;

public class GuiLocomotiveSteamSolid extends GuiLocomotive {

    private final EntityLocomotiveSteamSolid loco;
    private final EntityPlayer player;

    public GuiLocomotiveSteamSolid(InventoryPlayer inv, EntityLocomotiveSteamSolid loco) {
        super(inv, loco, ContainerLocomotiveSteamSolid.make(inv, loco), "steam", "gui_locomotive_steam.png", 205, true);
        this.loco = loco;
        this.player = inv.player;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par3, par3);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        if (this.loco.boiler.hasFuel()) {
            int scale = this.loco.boiler.getBurnProgressScaled(12);
            this.drawTexturedModalRect(x + 62, y + 34 - scale, 176, 59 - scale, 14, scale + 2);
        }
    }

}
