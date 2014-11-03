package mods.railcraft.common.enchantment;

import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.enchantment.Enchantment;

public class RailcraftEnchantments {

    public static Enchantment wrecking;
    public static Enchantment implosion;
    public static Enchantment destruction;

    public static void registerEnchantment() {
        wrecking = new EnchantmentWrecking(RailcraftConfig.wreckingID(), 10);
        implosion = new EnchantmentImplosion(RailcraftConfig.implosionID(), 5);
        destruction = new EnchantmentDestruction(RailcraftConfig.destructionID(), 5);
    }
}
