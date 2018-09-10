package mods.railcraft.api.charge;

import net.minecraft.world.World;

import java.util.function.Function;

/**
 *
 */
public final class ChargeApiAccess {

    public static void setDimensionHook(Function<World, IChargeDimension> instance) {
        ChargeToolsApi.accessor = instance;
    }

    private ChargeApiAccess() {
    }
}
