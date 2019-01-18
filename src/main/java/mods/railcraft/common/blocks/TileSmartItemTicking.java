/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.util.ITickable;

/**
 *
 */
public abstract class TileSmartItemTicking extends TileSmartItem implements ITickable {

    protected TileSmartItemTicking() {
    }

    protected TileSmartItemTicking(int invSize) {
        super(invSize);
    }

    protected int clock = MiscTools.RANDOM.nextInt();
    private boolean sendClientUpdate;

    @Override
    public void update() {
        clock++;

        if (sendClientUpdate) {
            sendClientUpdate = false;
            PacketBuilder.instance().sendTileEntityPacket(this);
        }
    }

    @Override
    public void sendUpdateToClient() {
        sendClientUpdate = true;
    }
}
