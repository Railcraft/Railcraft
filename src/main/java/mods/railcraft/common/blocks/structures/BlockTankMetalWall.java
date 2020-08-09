/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import net.minecraft.block.material.Material;
import net.minecraft.util.Tuple;

/**
 * Created by CovertJaguar on 12/22/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockTankMetalWall<T extends TileTank> extends BlockTankMetal<T> {
    protected BlockTankMetalWall() {
        super(Material.IRON);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }
}
