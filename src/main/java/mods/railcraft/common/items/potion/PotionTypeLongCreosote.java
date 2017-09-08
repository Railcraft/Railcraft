package mods.railcraft.common.items.potion;

import net.minecraft.init.Items;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;

public class PotionTypeLongCreosote extends PotionTypeRailcraft {
    public PotionTypeLongCreosote() {
        super("long_creosote", new PotionEffect(RailcraftPotions.CREOSOTE.get(), 9600, 0));
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        RailcraftPotionTypes.CREOSOTE.getObject().ifPresent(creosote -> PotionHelper.registerPotionTypeConversion(creosote, new PotionHelper.ItemPredicateInstance(Items.REDSTONE), this));
    }
}
