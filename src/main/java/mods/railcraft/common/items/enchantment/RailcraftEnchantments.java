/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items.enchantment;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Locale;
import java.util.function.Supplier;

public enum RailcraftEnchantments {
    WRECKING(() -> new EnchantmentDamageRailcraft("wrecking", Enchantment.Rarity.RARE, 1, 11, 20, null, 0.75f)),
    IMPLOSION(() -> new EnchantmentDamageRailcraft("implosion", Enchantment.Rarity.RARE, 5, 8, 20, EntityCreeper.class::isInstance, 2.5f)),
    DESTRUCTION(() -> new EnchantmentDestruction(Enchantment.Rarity.VERY_RARE)),
    SMACK(() -> new EnchantmentSmack(Enchantment.Rarity.VERY_RARE));
    public static RailcraftEnchantments[] VALUES = values();
    private final Supplier<Enchantment> factory;
    private Enchantment enchantment;

    RailcraftEnchantments(Supplier<Enchantment> factory) {
        this.factory = factory;
    }

    public Enchantment get() {
        return enchantment;
    }

    public String getTag() {
        return name().toLowerCase(Locale.ROOT);
    }

    public int getLevel(ItemStack stack) {
        if (enchantment == null)
            return 0;
        return EnchantmentHelper.getEnchantmentLevel(enchantment, stack);
    }

    public static void registerEnchantments() {
        for (RailcraftEnchantments enchantment : VALUES) {
            if (RailcraftConfig.isEnchantmentEnabled(enchantment.getTag())) {
                enchantment.enchantment = enchantment.factory.get();
                enchantment.enchantment.setRegistryName(RailcraftConstantsAPI.locationOf(enchantment.getTag()));
                ForgeRegistries.ENCHANTMENTS.register(enchantment.enchantment);
            }
        }
    }

}
