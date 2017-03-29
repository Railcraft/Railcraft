/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.collections.CollectionTools;

import static mods.railcraft.common.items.Metal.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemNugget extends ItemMetal {

    public ItemNugget() {
        super(Form.NUGGET, true, true, CollectionTools.createIndexedLookupTable(IRON, STEEL, COPPER, TIN, LEAD, SILVER, BRONZE));
    }

    @Override
    public void defineRecipes() {
        for (Metal m : getMetalBiMap().values()) {
            CraftingPlugin.addShapelessRecipe(m.getStack(Metal.Form.NUGGET, 9), m.getStack(Metal.Form.INGOT));
            CraftingPlugin.addRecipe(m.getStack(Metal.Form.INGOT),
                    "NNN",
                    "NNN",
                    "NNN",
                    'N', m.getOreTag(Metal.Form.NUGGET));
        }
    }
}
