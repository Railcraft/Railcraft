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

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Created by CovertJaguar on 7/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileRailcraftTicking extends TileRailcraft implements ITickable {

    protected int clock = MiscTools.RANDOM.nextInt();
    private boolean sendClientUpdate;

    @Override
    @OverridingMethodsMustInvokeSuper
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

    protected boolean clock(int interval) {
        return clock % interval == 0;
    }

}
