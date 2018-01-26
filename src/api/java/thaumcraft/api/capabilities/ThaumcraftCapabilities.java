package thaumcraft.api.capabilities;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * 
 * @author Azanor
 *
 */
public class ThaumcraftCapabilities {

	//PLAYER RESEARCH/////////////////////////////////////////
	
	/**
	 * The capability object for IPlayerKnowledge
	 */
	@CapabilityInject(IPlayerKnowledge.class)
	public static final Capability<IPlayerKnowledge> KNOWLEDGE = null;

	/**
	 * Retrieves the knowledge capability handler for the supplied player
	 */
	public static IPlayerKnowledge getKnowledge(@Nonnull EntityPlayer player)
	{
		return player.getCapability(KNOWLEDGE, null);
	}
	
	/**
	 * Shortcut method to check if player knows the passed researchs. All must be true
	 * Research does not need to be complete, just 'in progress' 
	 * @param player
	 * @param research 
	 * @return
	 */
	public static boolean knowsResearch(@Nonnull EntityPlayer player, @Nonnull String... research) {
		for (String r : research)
			if (!getKnowledge(player).isResearchKnown(r)) return false;
		return true;
	}
	
	/**
	 * Shortcut method to check if player knows all the passed research entries. 
	 * Research needs to be complete and 'in progress' research will only count if a stage is passed in the research paramater (using @, eg. "FOCUSFIRE@2")
	 * @param player
	 * @param research
	 * @return
	 */
	public static boolean knowsResearchStrict(@Nonnull EntityPlayer player, @Nonnull String... research) {
		for (String r : research) {
			if (r.contains("@")) {
				if (!getKnowledge(player).isResearchKnown(r)) return false;
			} else {
				if (!getKnowledge(player).isResearchComplete(r)) return false; 
			}
		}
		return true;
	}
	
	
	//PLAYER WARP/////////////////////////////////////////

	/**
	 * The capability object for IPlayerWarp
	 */
	@CapabilityInject(IPlayerWarp.class)
	public static final Capability<IPlayerWarp> WARP = null;

	/**
	 * Retrieves the warp capability handler for the supplied player
	 */
	public static IPlayerWarp getWarp(@Nonnull EntityPlayer player)
	{
		return player.getCapability(WARP, null);
	}

	
	
	
}
