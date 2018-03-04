package thaumcraft.api.casters;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusCore.FocusEffect;

public interface IFocusPartEffect extends IFocusPart {
	
	public default float getBaseCost() { return 0; }

	public default boolean onEffectTrigger(World world, RayTraceResult ray, Entity caster, @Nullable ItemStack casterStack, Entity mediumEntity, FocusEffect effect, float charge) { return true; }
	
		
}
