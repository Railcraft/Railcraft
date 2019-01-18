/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartTrackRelayer;
import mods.railcraft.common.gui.containers.ContainerCartTrackRelayer;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiCartTrackRelayer extends GuiCartTrackLayer {

    public GuiCartTrackRelayer(InventoryPlayer inv, EntityCartTrackRelayer cart) {
        super(cart, new ContainerCartTrackRelayer(inv, cart), "gui_cart_track_relayer.png");
    }
}
