package mods.railcraft.common.items.potion;

import net.minecraft.init.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;

public class PotionTypeStrongCreosote extends PotionTypeRailcraft {
    public PotionTypeStrongCreosote() {
        super("strong_creosote", new PotionEffect(RailcraftPotions.CREOSOTE.get(), 1800, 1));
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        RailcraftPotionTypes.CREOSOTE.getObject().ifPresent(creosote -> PotionHelper.addMix(creosote, Items.GLOWSTONE_DUST, this));
    }
}
