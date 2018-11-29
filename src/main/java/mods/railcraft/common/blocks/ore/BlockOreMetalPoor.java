/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.BlockMetaVariant;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.plugins.forge.CraftingPlugin;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMetaVariant(EnumOreMetalPoor.class)
public class BlockOreMetalPoor extends BlockOreMetalBase<EnumOreMetalPoor> {
    public BlockOreMetalPoor() {
        setDefaultState(blockState.getBaseState().withProperty(getVariantProperty(), EnumOreMetalPoor.COPPER));
    }

    @Override
    public void defineRecipes() {
        for (EnumOreMetalPoor ore : EnumOreMetalPoor.VALUES) {
            CraftingPlugin.addFurnaceRecipe(ore.getStack(), ore.getMetal().getStack(Metal.Form.NUGGET, 3), 0.3F);
        }
    }
}
