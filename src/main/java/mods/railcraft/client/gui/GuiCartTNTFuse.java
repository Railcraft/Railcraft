/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.CartBaseExplosive;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;

public class GuiCartTNTFuse extends GuiBasic {

    private final String FUSE = LocalizationPlugin.translate("gui.railcraft.cart.tnt.fuse") + " = ";
    private final String TICKS = " " + LocalizationPlugin.translate("gui.railcraft.ticks");
    protected int fuse = 80;
    CartBaseExplosive cart;

    public GuiCartTNTFuse(CartBaseExplosive c) {
        super(c.getName());
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
        if (f < CartBaseExplosive.MIN_FUSE)
            f = CartBaseExplosive.MIN_FUSE;
        if (f > CartBaseExplosive.MAX_FUSE)
            f = CartBaseExplosive.MAX_FUSE;
        fuse = f;
    }

    @Override
    public void drawExtras(int x, int y, float f) {
        if (cart != null)
            GuiTools.drawCenteredString(fontRenderer, FUSE + fuse + TICKS, 25);
    }

    @Override
    public void onGuiClosed() {
        cart.setFuse(fuse);
        if (Game.isClient(cart.theWorld()))
            PacketBuilder.instance().sendGuiReturnPacket(cart);
    }

}
