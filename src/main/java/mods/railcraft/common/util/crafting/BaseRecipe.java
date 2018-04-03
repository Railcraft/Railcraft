package mods.railcraft.common.util.crafting;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 *
 */
abstract class BaseRecipe implements IRecipe {

    final ResourceLocation registryName;

    BaseRecipe(String name) {
        this.registryName = RailcraftConstantsAPI.locationOf(name);
    }

    @Override
    public final IRecipe setRegistryName(ResourceLocation name) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Nonnull
    @Override
    public final ResourceLocation getRegistryName() {
        return this.registryName;
    }

    @Override
    public final Class<IRecipe> getRegistryType() {
        return IRecipe.class;
    }
}
