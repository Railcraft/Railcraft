/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items.enchantment;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RailcraftEnchantments {

    public static Enchantment wrecking;
    public static Enchantment implosion;
    public static Enchantment destruction;
    public static Enchantment smack;

    public static void registerEnchantment() {
        if (RailcraftConfig.wreckingEnabled()) {
            wrecking = new EnchantmentDamageRailcraft("wrecking", Enchantment.Rarity.RARE, 1, 11, 20, null, 0.75f);
            GameRegistry.register(wrecking, new ResourceLocation(RailcraftConstantsAPI.MOD_ID, "wrecking"));
        }
        if (RailcraftConfig.implosionEnabled()) {
            implosion = new EnchantmentDamageRailcraft("implosion", Enchantment.Rarity.RARE, 5, 8, 20, EntityCreeper.class, 2.5f);
            MinecraftForge.EVENT_BUS.register(implosion);
            GameRegistry.register(implosion, new ResourceLocation(RailcraftConstantsAPI.MOD_ID, "implosion"));
        }
        if (RailcraftConfig.destructionEnabled()) {
            destruction = new EnchantmentDestruction(Enchantment.Rarity.VERY_RARE);
            GameRegistry.register(destruction, new ResourceLocation(RailcraftConstantsAPI.MOD_ID, "destruction"));
        }
        if (RailcraftConfig.smackEnabled()) {
            smack = new EnchantmentSmack(Enchantment.Rarity.RARE).setRegistryName(Railcraft.MOD_ID, "smack");
            GameRegistry.register(smack);
        }
    }

}
