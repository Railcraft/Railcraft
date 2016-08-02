package buildcraft.api.items;

import buildcraft.api.enums.EnumBlueprintType;
import net.minecraft.item.ItemStack;

public interface IBlueprintItem extends INamedItem {
    EnumBlueprintType getType(ItemStack stack);
}
