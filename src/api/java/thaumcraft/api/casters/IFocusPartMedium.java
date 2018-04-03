package thaumcraft.api.casters;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IFocusPartMedium extends IFocusPart {

	public default EnumFocusCastMethod getCastMethod() { return EnumFocusCastMethod.DEFAULT; }
	
	enum EnumFocusCastMethod {
		DEFAULT, //Spell is cast instantly and as long as you keep casting
		CHARGE, //Spell is cast when player releases the charge. The longer you charge, the more effective & expensive the spell will be. Range of 50% - 200%
		INSTANT; //Instantly takes effect when cast with no cooldown.
	}
	
	/**
	 * Determines the number of ticks that you will need to 'charge' the gauntlet before the spell is cast. 
	 * Only applies to DEFAULT and CHARGE methods 
	 * @return
	 */
	public default int getChargeTime() { return 10; }
	
	
	
	/**
	 * @param world
	 * @param caster
	 * @param casterStack
	 * @param focus
	 * @param charge Acts as a multiplier to focus <i>effectiveness</i>. Default value 1. Used by the <i>CHARGE</i> casting method to modify casting time and cost.
	 * @return
	 */
	public default boolean onMediumTrigger(World world, Entity caster, @Nullable ItemStack casterStack, FocusCore focus, float charge) { 
		return true; 
	}
}
