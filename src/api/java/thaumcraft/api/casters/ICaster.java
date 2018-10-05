package thaumcraft.api.casters;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ICaster {
	
	float getConsumptionModifier(ItemStack is, EntityPlayer player, boolean crafting);

	boolean consumeVis(ItemStack is, EntityPlayer player, float amount, boolean crafting, boolean simulate);

	Item getFocus(ItemStack stack);

	ItemStack getFocusStack(ItemStack stack);

	void setFocus(ItemStack stack, ItemStack focus);

	ItemStack getPickedBlock(ItemStack stack);

}