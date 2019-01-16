/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.forge;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by CovertJaguar on 6/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class PotionPlugin {
    private static final List<ItemStack> POTIONS = new ArrayList<>();
    private static final List<ItemStack> POTIONS_SPLASH = new ArrayList<>();
    private static final List<ItemStack> POTIONS_LINGERING = new ArrayList<>();
    private static boolean hasInit;

    private static void init() {
        POTIONS.clear();
        POTIONS_SPLASH.clear();
        POTIONS_LINGERING.clear();
        for (PotionType potiontype : ForgeRegistries.POTION_TYPES) {
            POTIONS.add(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potiontype));
            POTIONS_SPLASH.add(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype));
            POTIONS_LINGERING.add(PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), potiontype));
        }
        hasInit = true;
    }

    public static List<ItemStack> getPotions() {
        if (!hasInit)
            init();
        return Collections.unmodifiableList(POTIONS);
    }

    public static List<ItemStack> getPotionsSplash() {
        if (!hasInit)
            init();
        return Collections.unmodifiableList(POTIONS_SPLASH);
    }

    public static List<ItemStack> getPotionsLingering() {
        if (!hasInit)
            init();
        return Collections.unmodifiableList(POTIONS_LINGERING);
    }

    private PotionPlugin() {}
}
