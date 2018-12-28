/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

@BlockMeta.Tile(TileChestMetals.class)
public class BlockChestMetals extends BlockChestRailcraft<TileChestMetals> {

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "GPG",
                "PAP",
                "GPG",
                'A', new ItemStack(Blocks.ANVIL),
                'P', new ItemStack(Blocks.PISTON),
                'G', "gearSteel");
    }
}
