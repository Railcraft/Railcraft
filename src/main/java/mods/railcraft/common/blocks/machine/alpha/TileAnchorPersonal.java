/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.collections.ItemMap;
import mods.railcraft.common.util.misc.Game;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileAnchorPersonal extends TileAnchorWorld {

    private static final int MINUTES_BEFORE_DISABLE = 5;
    private long ticksSincePlayerLogged;

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.PERSONAL_ANCHOR;
    }

    @Override
    protected Ticket getTicketFromForge() {
        return ForgeChunkManager.requestPlayerTicket(Railcraft.getMod(), PlayerPlugin.getUsername(worldObj, getOwner()), worldObj, Type.NORMAL);
    }

    @Override
    public ItemMap<Float> getFuelMap() {
        return RailcraftConfig.anchorFuelPersonal;
    }

    @Override
    protected boolean meetsTicketRequirements() {
        return PlayerPlugin.isPlayerConnected(getOwner()) && super.meetsTicketRequirements();
    }

    @Override
    public void updateEntity() {
        if (Game.isHost(worldObj) && hasActiveTicket()) {
            if (PlayerPlugin.isPlayerConnected(getOwner()))
                ticksSincePlayerLogged = 0;
            else
                ticksSincePlayerLogged++;
            if (ticksSincePlayerLogged > RailcraftConstants.TICKS_PER_MIN * MINUTES_BEFORE_DISABLE)
                releaseTicket();
        }
        super.updateEntity();
    }

}
