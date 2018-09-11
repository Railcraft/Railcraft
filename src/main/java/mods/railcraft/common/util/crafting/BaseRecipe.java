package mods.railcraft.common.util.crafting;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;

// Keep it public; emblems use it!
public abstract class BaseRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    protected BaseRecipe(String name) {
        setRegistryName(RailcraftConstantsAPI.locationOf(name));
    }
}
