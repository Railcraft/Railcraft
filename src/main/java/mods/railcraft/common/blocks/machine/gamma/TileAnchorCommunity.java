package mods.railcraft.common.blocks.machine.gamma;

import org.apache.logging.log4j.Level;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.misc.Game;

public class TileAnchorCommunity extends TileAnchorWorld  {
    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineGamma.COMMUNITY_ANCHOR;
    }
    
    @Override
    public boolean needsFuel() {
        return false;
    }
    
    @Override
    protected Ticket getTicketFromForge() {
        return ForgeChunkManager.requestPlayerTicket(Railcraft.getMod(), PlayerPlugin.getUsername(worldObj, getOwner()), worldObj, Type.NORMAL);
    }
    
    @Override
    protected boolean meetsTicketRequirements() {
        return nearbyPlayer() && super.meetsTicketRequirements();
    }
    
    private boolean nearbyPlayer()
    {
    	return MinecraftServer.getServer().getCurrentPlayerCount() > 0 ? true : false;     	
    }
    
}
