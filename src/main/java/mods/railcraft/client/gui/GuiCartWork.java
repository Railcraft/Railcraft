/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import mods.railcraft.common.carts.EntityCartWork;
import mods.railcraft.common.gui.containers.ContainerCartWork;

public class GuiCartWork extends EntityGui
{

    private EntityMinecart cart;

    public GuiCartWork(InventoryPlayer inv, EntityCartWork cart)
    {
        super(cart, new ContainerCartWork(inv, cart), "textures/gui/container/crafting_table.png");
        this.cart = cart;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.crafting"), 28, 6, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }
}
