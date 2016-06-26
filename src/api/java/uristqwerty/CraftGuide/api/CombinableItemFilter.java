package uristqwerty.CraftGuide.api;

import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * An extra set of operations for ItemFilter, allowing some ItemFilters to be combined.
 */
public interface CombinableItemFilter extends ItemFilter
{
	/**
	 * Returns an <code>ItemFilter</code> that represents the union of <code>this</code> and <code>other</code>.
	 * <br><br>
	 * May return <code>null</code>, if the operation does not make sense for the type of <code>other</code>.
	 */
	public ItemFilter addItemFilter(ItemFilter other);

	/**
	 * Returns an <code>ItemFilter</code> that represents the contents of <code>this</code> that are not also present in <code>other</code>.
	 * <br><br>
	 * May return <code>null</code>, if the operation does not make sense for the type of <code>other</code>.
	 */
	public ItemFilter subtractItemFilter(ItemFilter other);

	/**
	 * Returns a list of <code>ItemStack</code>s that represent this <code>ItemFilter</code>.
	 * <br><br>
	 * May return <code>null</code>, if the operation does not make sense for this CombinableItemFilter.
	 */
	public List<ItemStack> getRepresentativeItems();
}
