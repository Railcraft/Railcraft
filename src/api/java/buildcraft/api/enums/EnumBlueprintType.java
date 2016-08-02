package buildcraft.api.enums;

import buildcraft.api.items.IBlueprintItem;
import net.minecraft.item.ItemStack;

public enum EnumBlueprintType {
    NONE,
    BLUEPRINT,
    TEMPLATE;

    public static EnumBlueprintType valueOf(int index) {
        if (index <= 0 || index >= values().length) {
            return NONE;
        } else {
            return values()[index];
        }
    }

    public static EnumBlueprintType getType(ItemStack item) {
        if (item == null) {
            return NONE;
        } else if (item.getItem() instanceof IBlueprintItem) {
            return ((IBlueprintItem) item.getItem()).getType(item);
        } else {
            return NONE;
        }
    }
}
