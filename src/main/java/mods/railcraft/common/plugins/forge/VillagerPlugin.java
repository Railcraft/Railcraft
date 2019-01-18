/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.forge;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.lang.reflect.Field;
import java.util.List;

/**
 *
 */
public final class VillagerPlugin {

    static final Field UPGRADE_DELAY = ObfuscationReflectionHelper.findField(EntityVillager.class, "field_70961_j");
    static final Field CAREER = ObfuscationReflectionHelper.findField(EntityVillager.class, "field_175563_bv");
    static final Field ID = ObfuscationReflectionHelper.findField(VillagerRegistry.VillagerCareer.class, "id");
    static final Field CAREERS = ObfuscationReflectionHelper.findField(VillagerRegistry.VillagerProfession.class, "careers");

    static {
        UPGRADE_DELAY.setAccessible(true);
    }

    public static int getUpgradeDelay(EntityVillager villager) {
        try {
            return UPGRADE_DELAY.getInt(villager);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    public static void setCareer(EntityVillager villager, VillagerRegistry.VillagerCareer career) {
        try {
            CAREER.setInt(villager, ID.getInt(career) + 1);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    public static VillagerRegistry.VillagerCareer getCareer(EntityVillager villager) {
        try {
            return villager.getProfessionForge().getCareer(CAREER.getInt(villager) - 1);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    // Forge career id starts with 0; vanilla starts with 1; we use forge id
    public static int getCareerId(VillagerRegistry.VillagerCareer career) {
        try {
            return ID.getInt(career);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<VillagerRegistry.VillagerCareer> getCareers(VillagerRegistry.VillagerProfession profession) {
        try {
            return (List<VillagerRegistry.VillagerCareer>) CAREERS.get(profession);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    private VillagerPlugin() {}
}
