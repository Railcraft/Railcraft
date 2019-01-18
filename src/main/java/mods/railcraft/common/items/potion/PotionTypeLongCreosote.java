/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items.potion;

import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.oredict.OreIngredient;

final class PotionTypeLongCreosote extends PotionTypeRailcraft {
    PotionTypeLongCreosote() {
        super("creosote", new PotionEffect(RailcraftPotions.CREOSOTE.get(), 9600, 0));
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        RailcraftPotionTypes.CREOSOTE.getObject().ifPresent(creosote -> PotionHelper.addMix(creosote, new OreIngredient("dustRedstone"), this));
    }
}
