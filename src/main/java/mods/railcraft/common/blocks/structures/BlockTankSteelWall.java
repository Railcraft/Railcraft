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
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;

@BlockMeta.Tile(TileTankSteel.class)
public class BlockTankSteelWall extends BlockTankMetalWall<TileTankSteel> {

    @Override
    public TankDefinition getTankType() {
        return TankDefinition.STEEL;
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        addRecipe("PP",
                "PP",
                'P', RailcraftItems.PLATE, Metal.STEEL);
    }
}
