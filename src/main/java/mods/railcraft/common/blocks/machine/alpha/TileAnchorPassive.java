/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.collections.ItemMap;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeChunkManager;

/**
 * Created by Terpo on 2/24/2015.
 */
public class TileAnchorPassive extends TileAnchorWorld {
    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.PASSIVE_ANCHOR;
    }

    @Override
    public ItemMap<Float> getFuelMap() {
        return RailcraftConfig.anchorFuelPassive;
    }

    @Override
    protected ForgeChunkManager.Ticket getTicketFromForge() {
        return ForgeChunkManager.requestPlayerTicket(Railcraft.getMod(), PlayerPlugin.getUsername(worldObj, getOwner()), worldObj, ForgeChunkManager.Type.NORMAL);
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
