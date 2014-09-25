/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.filters;

import net.minecraft.item.ItemStack;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.plugins.forge.OreDictPlugin;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class OreStackFilter implements IStackFilter {

    private final String oreTag;

    public OreStackFilter(String oreTag) {
        this.oreTag = oreTag;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return OreDictPlugin.isOreType(oreTag, stack);
    }

}
