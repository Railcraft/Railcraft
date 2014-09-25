/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import mods.railcraft.common.carts.CartExplosiveBase;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;

public class GuiCartTNTFuse extends GuiBasic {

    private final String FUSE = LocalizationPlugin.translate("railcraft.gui.cart.tnt.fuse") + " = ";
    private final String TICKS = " " + LocalizationPlugin.translate("railcraft.gui.ticks");
    protected int fuse = 80;
    CartExplosiveBase cart;

    public GuiCartTNTFuse(CartExplosiveBase c) {
        super(c.getCommandSenderName());
        cart = c;
        if (cart != null)
            fuse = cart.getFuse();
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        buttonList.add(new GuiButton(0, w + 13, h + 50, 30, 20, "-10"));
        buttonList.add(new GuiButton(1, w + 53, h + 50, 30, 20, "-1"));
        buttonList.add(new GuiButton(2, w + 93, h + 50, 30, 20, "+1"));
        buttonList.add(new GuiButton(3, w + 133, h + 50, 30, 20, "+10"));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int f = fuse;
        if (guibutton.id == 0)
            f += -10;
        if (guibutton.id == 1)
            f += -1;
        if (guibutton.id == 2)
            f += 1;
        if (guibutton.id == 3)
            f += 10;
        if (f < CartExplosiveBase.MIN_FUSE)
            f = CartExplosiveBase.MIN_FUSE;
        if (f > CartExplosiveBase.MAX_FUSE)
            f = CartExplosiveBase.MAX_FUSE;
        fuse = f;
    }

    @Override
    public void drawExtras(int x, int y, float f) {
        if (cart != null)
            GuiTools.drawCenteredString(fontRendererObj, FUSE + fuse + TICKS, 25);
    }

    @Override
    public void onGuiClosed() {
        cart.setFuse(fuse);
        if (Game.isNotHost(cart.getWorld()))
            PacketBuilder.instance().sendGuiReturnPacket(cart);
    }

}
