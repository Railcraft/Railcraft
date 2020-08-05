/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.BlockEntityDelegate;
import mods.railcraft.common.blocks.ISmartTile;
import mods.railcraft.common.blocks.TileRailcraft;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

/**
 * Created by CovertJaguar on 1/28/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockStructure<T extends TileRailcraft & ISmartTile> extends BlockEntityDelegate<T> {
    protected BlockStructure(Material materialIn) {super(materialIn);}

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
}
