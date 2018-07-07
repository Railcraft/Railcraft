package mods.railcraft.common.items.potion;

import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.oredict.OreIngredient;

final class PotionTypeStrongCreosote extends PotionTypeRailcraft {
    PotionTypeStrongCreosote() {
        super("creosote", new PotionEffect(RailcraftPotions.CREOSOTE.get(), 1800, 1));
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        RailcraftPotionTypes.CREOSOTE.getObject().ifPresent(creosote -> PotionHelper.addMix(creosote, new OreIngredient("dustGlowstone"), this));
    }
}
