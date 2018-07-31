package thaumcraft.api.golems.seals;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ISealConfigFilter {
	
	NonNullList<ItemStack> getInv();
	
	int getFilterSize();
	
	ItemStack getFilterSlot(int i);
	
	void setFilterSlot(int i, ItemStack stack);
	
	boolean isBlacklist();
	
	void setBlacklist(boolean black);
	
	boolean hasStacksizeLimiters();
	
}
