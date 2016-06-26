package uristqwerty.CraftGuide.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list of objects polled by CraftGuide every time the
 *  tooltip text of an item is needed, allowing additional lines
 *  of information about the stack to be displayed.
 *
 *  @see StackInfoSource
 */
public class StackInfo
{
	public static List<StackInfoSource> sources = new ArrayList<StackInfoSource>();

	public static void addSource(StackInfoSource source)
	{
		sources.add(source);
	}
}
