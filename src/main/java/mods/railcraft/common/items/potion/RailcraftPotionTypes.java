package mods.railcraft.common.items.potion;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public final class RailcraftPotionTypes {

    private static final class PotionTypeContainer implements IRailcraftObjectContainer<PotionTypeRailcraft> {
        private final Definition def;
        private final Supplier<PotionTypeRailcraft> supplier;
        private final String name;
        private PotionTypeRailcraft potion;

        PotionTypeContainer(String name, Supplier<PotionTypeRailcraft> supplier) {
            this.supplier = supplier;
            this.name = name;
            this.def = new Definition(this, name, null);
        }

        @Override
        public Definition getDef() {
            return this.def;
        }

        @Override
        public void register() {
            this.potion = checkNotNull(supplier.get());
            this.potion.setRegistryName(RailcraftConstantsAPI.locationOf(name));
            this.potion.initializeDefinition();
            ForgeRegistries.POTION_TYPES.register(this.potion);
        }

        @Override
        public Optional<PotionTypeRailcraft> getObject() {
            return Optional.ofNullable(potion);
        }

        public PotionTypeRailcraft get() {
            return potion;
        }
    }

//    public static final List<RailcraftPotionTypes> VALUES = ImmutableList.copyOf(values());

    public static final IRailcraftObjectContainer<PotionTypeRailcraft> CREOSOTE = new PotionTypeContainer("creosote", PotionTypeCreosote::new);
    public static final IRailcraftObjectContainer<PotionTypeRailcraft> LONG_CREOSOTE = new PotionTypeContainer("long_creosote", PotionTypeLongCreosote::new);
    public static final IRailcraftObjectContainer<PotionTypeRailcraft> STRONG_CREOSOTE = new PotionTypeContainer("strong_creosote", PotionTypeStrongCreosote::new);

    static {
        CREOSOTE.conditions().add(RailcraftPotions.CREOSOTE);
        LONG_CREOSOTE.conditions().add(RailcraftPotions.CREOSOTE);
        STRONG_CREOSOTE.conditions().add(RailcraftPotions.CREOSOTE);
    }

    private RailcraftPotionTypes() {
    }
}


