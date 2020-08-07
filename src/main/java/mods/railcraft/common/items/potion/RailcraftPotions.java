/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

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
        private final SimpleDef def;
        private final String name;
        private final Supplier<PotionRailcraft> supplier;
        private PotionRailcraft potion;

        PotionContainer(String name, Supplier<PotionRailcraft> supplier) {
            this.name = name;
            this.def = new SimpleDef(this, name);
            this.supplier = supplier;
        }

        @Override
        public SimpleDef getDef() {
            return def;
        }

        @Override
        public void register() {
            this.potion = checkNotNull(supplier.get());
            potion.setPotionName("potion." + Railcraft.MOD_ID + '.' + getBaseTag());
            potion.setRegistryName(RailcraftConstantsAPI.locationOf(name));
            potion.initializeDefinition();
            ForgeRegistries.POTIONS.register(potion);
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
