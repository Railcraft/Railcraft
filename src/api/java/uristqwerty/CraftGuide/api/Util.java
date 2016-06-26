package uristqwerty.CraftGuide.api;

import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * Contains a number of methods that implement common functionality
 * that would otherwise need to be implemented by everyone who uses
 * the API, or that relies on parts of CraftGuide not included in
 * the API.
 */
public abstract class Util
{
	/**
	 * An instance of Util, containing the method implementations.
	 * <br><br>
	 * It is set during CraftGuide's {@literal @PreInit}.
	 */
	public static Util instance;

	/**
	 * Causes CraftGuide to clear its list of recipes, and reload them with
	 * exactly the same process that was originally used to build the list.
	 */
	public abstract void reloadRecipes();

	/**
	 * Converts the passed ItemStack's name and information into a List
	 * of Strings for display, similar to how GuiContainer does it.
	 * Additionally, contains logic to try an alternative if given a stack
	 * with a damage of -1 which produces an unusable name and information,
	 * if CraftGuide is set to always show item IDs it will insert the item's
	 * ID and damage value as the second line. If an exception is thrown
	 * at any time during the process, it will log it to CraftGuide.log and
	 * generate an error text to display that at least shows the item ID and
	 * damage.
	 * @param stack
	 * @return
	 */
	public abstract List<String> getItemStackText(ItemStack stack);

	/**
	 * Gets a standard {@link ItemFilter} for any of the common types:
	 * <li>ItemStack
	 * <li>List of ItemStacks
	 * <li>String
	 *
	 * @param item
	 * @return
	 */
	public abstract ItemFilter getCommonFilter(Object item);

	/**
	 * Gets a texture usable with {@link Renderer#renderRect}
	 * from a String identifier.
	 * If the texture does not exist in the current theme, nothing will be drawn.
	 * <br><br>
	 * In addition to anything defined by the
	 * currently-active theme, converts the following aliases to the equivalent
	 * name used in theme files for backwards compatibility:
	 *
	 * <li>"ItemStack-Any": ItemStack overlay indicating that any metadata value
	 * matches</li>
	 * <li>"ItemStack-OreDict": ItemStack overlay indicating that any one of a
	 * list of ItemStacks are acceptable.</li>
	 * <li>"ItemStack-OreDict-Single": Used in place of ItemStack-OreDict when
	 * the list of ItemStacks is exactly one element long.</li>
	 * <li>"ItemStack-Background": Image used as background for  GUI slots</li>
	 * <li>"TextFilter": Icon used in place of an ItemStack when searching for
	 * a String</li>
	 * <li>"Error": Displayed in place of an ItemStack when an error prevents
	 * that stack from being rendered.</li>
	 *
	 * @param identifier
	 * @return
	 */
	public abstract NamedTexture getTexture(String identifier);

	/**
	 * Returns the number of partial ticks for this frame. I don't know
	 * quite what they do, but it's the third parameter to
	 * {@link net.minecraft.src.GuiScreen#drawScreen}, so I'm assuming
	 * that at least something needs it. Rather than pass it as an
	 * extra argument to every drawing method, it is stored at the
	 * start of rendering the GUI, and can be retrieved with this method.
	 * @return
	 */
	public abstract float getPartialTicks();

	/**
	 * Returns a new List<ItemStack>, containing one of each unique item present in either a or b
	 */
	public abstract List<ItemStack> subtractItemLists(List<ItemStack> a, List<ItemStack> b);

	/**
	 * Returns a new List<ItemStack>, containing one of each unique item present in a but not in b
	 */
	public abstract List<ItemStack> addItemLists(List<ItemStack> a, List<ItemStack> b);
}
