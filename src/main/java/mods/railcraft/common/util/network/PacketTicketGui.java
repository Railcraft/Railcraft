/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.client.gui.GuiTicket;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

class PacketTicketGui extends RailcraftPacket {
    private EnumHand hand;

    PacketTicketGui() {
    }

    PacketTicketGui(EnumHand hand) {
        this.hand = hand;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeEnum(hand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(RailcraftInputStream data) {
        try {
            EntityPlayer player = Minecraft.getMinecraft().player;
            ItemStack heldItem = player.getHeldItem(data.readEnum(EnumHand.values()));
            if (!InvTools.isEmpty(heldItem) && RailcraftItems.TICKET_GOLD.isEqual(heldItem))
                Minecraft.getMinecraft().displayGuiScreen(new GuiTicket(player, heldItem));
        } catch (Exception exception) {
            Game.log().throwable("Error reading Golden Ticket Gui Packet", exception);
        }
    }

    @Override
    public int getID() {
        return PacketType.GOLDEN_TICKET_GUI.ordinal();
    }

}
