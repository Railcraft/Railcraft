package uristqwerty.CraftGuide.api;

import net.minecraft.item.ItemStack;

/**
 * When CraftGuide gets the tooltip of an item, every StackInfoSource
 *  listed by {@link StackInfo} is polled, so that it may provide
 *  additional lines of information about the stack.
 */
public interface StackInfoSource
{
	/**
	 * May return null to indicate that no additional information
	 *  about the stack is provided by this StackInfoSource.
	 */
	public String getInfo(ItemStack itemStack);
}
