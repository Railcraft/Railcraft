package thaumcraft.api.casters;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

public interface IFocusPart {	
	
	/**
	 * A unique key for this part. It is probably a good idea to include your modid in this.
	 * @return
	 */
	public String getKey();
	
	/**
	 * The research required to unlock this part. 
	 * @return
	 */
	public String getResearch();
	
	/**
	 * Get the aspect associated with this part. Used to determine the crystal required in crafting.
	 * @return
	 */
	public Aspect getAspect();
	
	public default String getName() {
		return I18n.translateToLocal("focuspart."+getKey()+".name");
	}
	
	public default String getText() {
		return I18n.translateToLocal("focuspart."+getKey()+".text");
	}
	
	public default float getCostMultiplier() { return 1; }
	
	/**
	 * By how much effects linked to this part will be modified. Used to modify things like damage, duration, etc.
	 * @return
	 */
	public default float getEffectMultiplier() { return 1; }
	
	public EnumFocusPartType getType();
	
	/**
	 * Location of the icon image file for this part.
	 * @return
	 */
	public ResourceLocation getIcon();
	
	/**
	 * Default color the part gem will display as.
	 * @return
	 */
	public int getGemColor();
	
	/**
	 * Default color the part icon will display as.
	 * @return
	 */
	public int getIconColor();
		
	enum EnumFocusPartType {
		MEDIUM, EFFECT, MODIFIER;
	}

	/**
	 * Special coding to see if one part can connect to each other.
	 * @param part
	 * @return
	 */
	public default boolean canConnectTo(IFocusPart part) { return true; }
	
	
	
	public default boolean hasCustomParticle() { return false; }
	public default void drawCustomParticle(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {  }
	
	
}
