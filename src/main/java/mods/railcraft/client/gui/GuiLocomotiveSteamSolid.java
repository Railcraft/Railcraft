/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityLocomotiveSteamSolid;
import mods.railcraft.common.gui.containers.ContainerLocomotiveSteamSolid;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLocomotiveSteamSolid extends GuiLocomotive {

    private final EntityLocomotiveSteamSolid loco;

    public GuiLocomotiveSteamSolid(InventoryPlayer inv, EntityLocomotiveSteamSolid loco) {
        super(inv, loco, ContainerLocomotiveSteamSolid.make(inv, loco), "steam", "gui_locomotive_steam.png", 205, true);
        this.loco = loco;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par3, par3);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        if (loco.boiler.hasFuel()) {
            int scale = loco.boiler.getBurnProgressScaled(12);
            drawTexturedModalRect(x + 99, y + 33 - scale, 176, 59 - scale, 14, scale + 2);
        }
    }

}
