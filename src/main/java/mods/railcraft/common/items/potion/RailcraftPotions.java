package mods.railcraft.common.items.potion;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.Railcraft;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public final class RailcraftPotions {

    public static final class PotionContainer implements IRailcraftObjectContainer<PotionRailcraft> {
        private final Definition def;
        private final String name;
        private final Supplier<PotionRailcraft> supplier;
        private PotionRailcraft potion;

        PotionContainer(String name, Supplier<PotionRailcraft> supplier) {
            this.name = name;
            this.def = new Definition(this, name, null);
            this.supplier = supplier;
        }

        @Override
        public Definition getDef() {
            return this.def;
        }

        @Override
        public void register() {
            this.potion = checkNotNull(supplier.get());
            this.potion.setPotionName("potion." + Railcraft.MOD_ID + '.' + getBaseTag());
            this.potion.setRegistryName(RailcraftConstantsAPI.locationOf(name));
            this.potion.initializeDefinition();
            ForgeRegistries.POTIONS.register(this.potion);
        }

        @Override
        public Optional<PotionRailcraft> getObject() {
            return Optional.ofNullable(potion);
        }

        public PotionRailcraft get() {
            return potion;
        }
    }

    //    public static final List<RailcraftPotions> VALUES = ImmutableList.copyOf(values());
    public static final PotionContainer CREOSOTE = new PotionContainer("creosote", PotionCreosote::new);

    private RailcraftPotions() {
    }
}
