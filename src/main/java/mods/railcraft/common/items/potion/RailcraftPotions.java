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
public enum RailcraftPotions implements IRailcraftObjectContainer<PotionRailcraft> {
    CREOSOTE(PotionCreosote::new),;

    private final Definition def;
    private final Supplier<PotionRailcraft> supplier;
    private PotionRailcraft potion;
    public static final List<RailcraftPotions> VALUES = ImmutableList.copyOf(values());

    RailcraftPotions(Supplier<PotionRailcraft> supplier) {
        this.def = new Definition(this, "potion." + name().toLowerCase(), null);
        this.supplier = supplier;
    }

    @Override
    public Definition getDef() {
        return this.def;
    }

    @Override
    public void register() {
        this.potion = checkNotNull(supplier.get());
        this.potion.setPotionName(getBaseTag());
        this.potion.setRegistryName(RailcraftConstantsAPI.locationOf(name().toLowerCase()));
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
