package mods.railcraft.common.items;

import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.item.ItemStack;

public class ItemPantograph extends ItemRailcraft {

	private static final String ITEM_TAG = "railcraft.pantograph";
	public final static int DURABILITY = 20000;
	private static ItemPantograph instance;
	
	public static final IStackFilter FILTER = new IStackFilter() {
        @Override
        public boolean matches(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ItemPantograph;
        }

    };
	
	public ItemPantograph getItem() {
		return instance;
	}
	
	public static void register() {
		if(instance == null && RailcraftConfig.isItemEnabled(ITEM_TAG)) {
			instance = new ItemPantograph();
			instance.setUnlocalizedName(ITEM_TAG);
			instance.setMaxStackSize(1);
			instance.setMaxDamage(DURABILITY);
			RailcraftRegistry.register(instance);
			
			CraftingPlugin.addShapedRecipe(new ItemStack(instance),
					"SSS",
					"R R",
					"R R",
					'S', "ingotSteel",
					'R', RailcraftItem.rebar.getRecipeObject());
		}
	}

}
