/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 *
 */
public abstract class BlockMultiBlockInventory<T extends TileMultiBlockInventory> extends BlockMultiBlock<T> {
    protected BlockMultiBlockInventory(Material materialIn) {
        super(materialIn);
    }

    protected BlockMultiBlockInventory(Material material, MapColor mapColor) {
        super(material, mapColor);
    }
}
