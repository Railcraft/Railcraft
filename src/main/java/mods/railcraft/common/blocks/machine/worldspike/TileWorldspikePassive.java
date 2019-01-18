/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.worldspike;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import net.minecraftforge.common.ForgeChunkManager;

/**
 * Created by Terpo on 2/24/2015.
 */
public class TileWorldspikePassive extends TileWorldspike {

    @Override
    public WorldspikeVariant getMachineType() {
        return WorldspikeVariant.PASSIVE;
    }

    @Override
    protected ForgeChunkManager.Ticket getTicketFromForge() {
        return ForgeChunkManager.requestPlayerTicket(Railcraft.getMod(), PlayerPlugin.getUsername(world, getOwner()), world, ForgeChunkManager.Type.NORMAL);
    }

//    @Override
//    protected boolean meetsTicketRequirements() {
//        return isPlayerPresent() && super.meetsTicketRequirements();
//    }
//
//    private boolean isPlayerPresent() {
//        return MinecraftServer.getServer().getCurrentPlayerCount() > 0;
//    }
}
