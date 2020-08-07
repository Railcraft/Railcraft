/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.plugins.forge.CraftingPlugin;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(EnumOreMetal.class)
public class BlockOreMetal extends BlockOreMetalBase<EnumOreMetal> {
    public BlockOreMetal() {
        setDefaultState(blockState.getBaseState().withProperty(getVariantEnumProperty(), EnumOreMetal.COPPER));
    }

    @Override
    public void defineRecipes() {
        for (EnumOreMetal ore : EnumOreMetal.VALUES) {
            CraftingPlugin.addFurnaceRecipe(ore.getStack(), ore.getMetal().getStack(Metal.Form.INGOT), 0.7F);
        }
    }
}
