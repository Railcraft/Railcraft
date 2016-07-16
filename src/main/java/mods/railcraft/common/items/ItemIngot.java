/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.util.collections.CollectionTools;

import javax.annotation.Nullable;

import static mods.railcraft.common.items.Metal.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemIngot extends ItemMetal {

    public ItemIngot() {
        super(Form.INGOT, "item.railcraft.ingot.", true, true, CollectionTools.createIndexedLookupTable(STEEL, COPPER, TIN, LEAD));
        setSmeltingExperience(1);
    }

    @Nullable
    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
        return Metal.class;
    }

    @Override
    public void initializeDefinintion() {
        for (Metal m : variants().values()) {
            LootPlugin.addLootUnique(RailcraftItems.ingot, m, 5, 9, LootPlugin.Type.TOOL);
        }
    }
}
