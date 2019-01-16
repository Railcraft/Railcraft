/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items.potion;

import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;

final class PotionTypeCreosote extends PotionTypeRailcraft {
    public PotionTypeCreosote() {
        super("creosote", new PotionEffect(RailcraftPotions.CREOSOTE.get(), 3600, 0));
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        PotionHelper.addMix(PotionTypes.AWKWARD, RailcraftItems.BOTTLE_CREOSOTE.getIngredient(), this);
    }
}
