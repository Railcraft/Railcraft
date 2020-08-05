/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;

@BlockMeta.Tile(TileBoilerFireboxSolid.class)
public final class BlockBoilerFireboxSolid extends BlockBoilerFirebox<TileBoilerFireboxSolid> {

    public BlockBoilerFireboxSolid() {
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(3, 1);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        CraftingPlugin.addShapedRecipe(stack,
                "BBB",
                "BCB",
                "BFB",
                'B', Items.NETHERBRICK,
                'C', Items.FIRE_CHARGE,
                'F', Blocks.FURNACE
        );
    }
}
