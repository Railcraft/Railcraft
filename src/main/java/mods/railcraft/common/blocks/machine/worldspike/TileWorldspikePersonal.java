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
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileWorldspikePersonal extends TileWorldspike {

    private static final int MINUTES_BEFORE_DISABLE = 5;
    private long ticksSincePlayerLogged;

    @Override
    public WorldspikeVariant getMachineType() {
        return WorldspikeVariant.PERSONAL;
    }

    @Override
    protected Ticket getTicketFromForge() {
        return ForgeChunkManager.requestPlayerTicket(Railcraft.getMod(), PlayerPlugin.getUsername(world, getOwner()), world, Type.NORMAL);
    }

    @Override
    protected boolean meetsTicketRequirements() {
        return PlayerPlugin.isPlayerConnected(getOwner()) && super.meetsTicketRequirements();
    }

    @Override
    public void update() {
        if (Game.isHost(world) && hasActiveTicket()) {
            if (PlayerPlugin.isPlayerConnected(getOwner()))
                ticksSincePlayerLogged = 0;
            else
                ticksSincePlayerLogged++;
            if (ticksSincePlayerLogged > RailcraftConstants.TICKS_PER_MIN * MINUTES_BEFORE_DISABLE)
                releaseTicket();
        }
        super.update();
    }
}
