/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.util.collections.CollectionTools;

import static mods.railcraft.common.items.Metal.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemIngot extends ItemMetal {

    public ItemIngot() {
        super(Form.INGOT, true, true, CollectionTools.createIndexedLookupTable(STEEL, COPPER, TIN, LEAD, SILVER));
        setSmeltingExperience(1);
    }

    @Override
    public void initializeDefinintion() {
        super.initializeDefinintion();
        for (Metal m : getMetalBiMap().values()) {
            LootPlugin.addLootUnique(RailcraftItems.INGOT, m, 5, 9, LootPlugin.Type.TOOL);
        }
    }

    @Override
    public void finalizeDefinition() {
        ItemMaterials.STEEL_TOOL.setRepairItem(Metal.STEEL.getStack(Form.INGOT));
    }
}
