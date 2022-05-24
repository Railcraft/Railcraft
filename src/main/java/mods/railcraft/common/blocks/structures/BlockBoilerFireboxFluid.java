/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@BlockMeta.Tile(TileBoilerFireboxFluid.class)
public final class BlockBoilerFireboxFluid extends BlockBoilerFirebox<TileBoilerFireboxFluid> {

    public BlockBoilerFireboxFluid() {
        setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.METAL);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        CraftingPlugin.addShapedRecipe(stack,
                "PUP",
                "BCB",
                "PFP",
                'P', RailcraftItems.PLATE, Metal.INVAR,
                'U', Items.BUCKET,
                'B', Blocks.IRON_BARS,
                'C', Items.FIRE_CHARGE,
                'F', Blocks.FURNACE);
    }
}
