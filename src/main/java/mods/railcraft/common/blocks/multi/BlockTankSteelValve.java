/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.init.Blocks;

@BlockMeta.Tile(TileTankSteelValve.class)
public class BlockTankSteelValve extends BlockTankMetalValve<TileTankSteelValve> {

    @Override
    public TankDefinition getTankType() {
        return TankDefinition.STEEL;
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        addRecipe("BPB",
                "PLP",
                "BPB",
                'B', Blocks.IRON_BARS,
                'P', RailcraftItems.PLATE, Metal.STEEL,
                'L', Blocks.LEVER);
    }

}
