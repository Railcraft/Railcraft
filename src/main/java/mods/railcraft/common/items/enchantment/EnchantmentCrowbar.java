package mods.railcraft.common.items.enchantment;

import mods.railcraft.common.items.ItemCrowbar;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentCrowbar extends Enchantment {

    public EnchantmentCrowbar(String tag, Rarity rarity, EntityEquipmentSlot... slots) {
        super(rarity, EnumEnchantmentType.DIGGER, slots);
        setName("railcraft.crowbar." + tag);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemCrowbar;
    }

}
