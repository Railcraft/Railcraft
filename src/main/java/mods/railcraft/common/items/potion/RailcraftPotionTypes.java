package mods.railcraft.common.items.potion;

import com.google.common.collect.ImmutableList;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public enum RailcraftPotionTypes implements IRailcraftObjectContainer<PotionTypeRailcraft> {
    CREOSOTE(PotionTypeCreosote::new) {
        {
            conditions().add(RailcraftPotions.CREOSOTE);
        }
    },
    LONG_CREOSOTE(PotionTypeLongCreosote::new) {
        {
            conditions().add(RailcraftPotions.CREOSOTE);
        }
    },
    STRONG_CREOSOTE(PotionTypeStrongCreosote::new) {
        {
            conditions().add(RailcraftPotions.CREOSOTE);
        }
    },;

    private final Definition def;
    private final Supplier<PotionTypeRailcraft> supplier;
    private PotionTypeRailcraft potion;
    public static final List<RailcraftPotionTypes> VALUES = ImmutableList.copyOf(values());

    RailcraftPotionTypes(Supplier<PotionTypeRailcraft> supplier) {
        this.supplier = supplier;
        this.def = new Definition(this, name().toLowerCase(), null);
    }

    @Override
    public Definition getDef() {
        return this.def;
    }

    @Override
    public void register() {
        this.potion = checkNotNull(supplier.get());
        this.potion.setRegistryName(RailcraftConstantsAPI.locationOf(name().toLowerCase()));
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
