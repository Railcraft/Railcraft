/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.enchantment;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RailcraftEnchantments {

    public static Enchantment wrecking;
    public static Enchantment implosion;
    public static Enchantment destruction;

    public static void registerEnchantment() {
        wrecking = new EnchantmentDamageRailcraft("wrecking", Enchantment.Rarity.RARE, 1, 11, 20, null, 0.75f);
        implosion = new EnchantmentDamageRailcraft("implosion", Enchantment.Rarity.RARE, 5, 8, 20, EntityCreeper.class, 2.5f);
        MinecraftForge.EVENT_BUS.register(implosion);
        destruction = new EnchantmentDestruction(Enchantment.Rarity.VERY_RARE);

        GameRegistry.register(wrecking, new ResourceLocation(RailcraftConstantsAPI.MOD_ID, "wrecking"));
        GameRegistry.register(implosion, new ResourceLocation(RailcraftConstantsAPI.MOD_ID, "implosion"));
        GameRegistry.register(destruction, new ResourceLocation(RailcraftConstantsAPI.MOD_ID, "destruction"));
    }

}
