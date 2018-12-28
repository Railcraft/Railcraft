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
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@BlockMeta.Tile(TileCokeOven.class)
public abstract class BlockCokeOven extends BlockMultiBlockInventory<TileCokeOven> {

    public static final PropertyInteger ICON = PropertyInteger.create("icon", 0, 2);

    protected BlockCokeOven() {
        super(Material.ROCK);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ICON);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(3, 1);
    }

    @Override
    public abstract void defineRecipes();
}
