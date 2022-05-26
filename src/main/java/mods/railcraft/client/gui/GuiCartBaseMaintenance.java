/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiMultiButton;
import mods.railcraft.common.carts.CartBaseMaintenance.CartMode;
import mods.railcraft.common.carts.CartBaseMaintenancePattern;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.world.IWorldNameable;

import java.io.IOException;

public abstract class GuiCartBaseMaintenance extends GuiTitled {
    private final CartBaseMaintenancePattern cart;

    protected GuiCartBaseMaintenance(IWorldNameable nameable, RailcraftContainer container, String texture, CartBaseMaintenancePattern cart) {
        super(nameable, container, texture);
        this.cart = cart;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (cart == null) return;
        int h = (height - ySize) / 2;
        GuiMultiButton<CartMode> modeButton = GuiMultiButton.create(1, 220, h + ySize - 100, 60, cart.getModeController());
        modeButton.setClickConsumer(n -> cart.setMode(cart.nextMode()));
        buttonList.add(modeButton);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) throws IOException {
        if (cart == null)
            return;
        super.actionPerformed(guibutton);
        sendUpdatePacket();
    }

    @Override
    public void onGuiClosed() {
        sendUpdatePacket();
    }

    private void sendUpdatePacket() {
        PacketBuilder.instance().sendGuiReturnPacket(cart);
    }
}
