package mods.railcraft.common.items.enchantment;

import mods.railcraft.common.items.ItemCrowbar;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;

public class EnchantmentCrowbar extends Enchantment {

    public EnchantmentCrowbar(String tag, int id, int weight) {
        super(id, weight, EnumEnchantmentType.digger);
        setName("railcraft.crowbar." + tag);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemCrowbar;
    }

}
