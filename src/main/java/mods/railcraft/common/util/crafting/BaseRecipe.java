/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

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
