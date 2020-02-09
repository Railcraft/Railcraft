/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
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
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.world.IWorldNameable;

import java.io.IOException;

public abstract class GuiCartBaseMaintenance extends GuiTitled {
    private final CartBaseMaintenancePattern cart;
    private GuiMultiButton modeButton;
    private ToolTip serviceToolTip;
    private ToolTip transportToolTip;

    protected GuiCartBaseMaintenance(IWorldNameable nameable, RailcraftContainer container, String texture, CartBaseMaintenancePattern cart) {
        super(nameable, container, texture);
        this.cart = cart;
        serviceToolTip = ToolTip.buildToolTip("gui.railcraft.cart.maintenance.tips.mode.service");
        transportToolTip = ToolTip.buildToolTip("gui.railcraft.cart.maintenance.tips.mode.transport");
    }

    @Override
    public void initGui() {
        super.initGui();
        if(cart == null) return;
        int h = (height - ySize) / 2;
        modeButton = GuiMultiButton.create(1, 200, h + ySize - 100, 90, cart.getModeController());
        modeButton.setToolTip(ToolTip.buildToolTip("gui.railcraft.cart.maintenance.tips.mode."+cart.getMode().getName()));
        modeButton.setClickConsumer(n -> {
            cart.clientMode = cart.getOtherMode();
        });
        buttonList.add(modeButton);
    }
    @Override
    public void updateScreen() {
        super.updateScreen();
        updateTooltip();
    }

    private void updateTooltip() {
        modeButton.setToolTip((cart.getMode() == CartMode.SERVICE) ? serviceToolTip : (cart.getMode() == CartMode.TRANSPORT) ? transportToolTip : null);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) throws IOException {
        if (cart == null)
            return;
        super.actionPerformed(guibutton);
        updateTooltip();
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
