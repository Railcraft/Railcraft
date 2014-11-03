package mods.railcraft.common.enchantment;

import mods.railcraft.common.items.ItemCrowbar;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;

public class EnchantmentCrowbar extends Enchantment {

    public EnchantmentCrowbar(int id, int weight) {
        super(id, weight, EnumEnchantmentType.weapon);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (stack.getItem() instanceof ItemCrowbar) {
            return true;
        } else {
            return false;
        }
    }
}
