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
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Tuple;

@BlockMeta.Tile(TileTankWater.class)
public class BlockTankWater extends BlockEntityDelegate<TileTankWater> {

    public BlockTankWater() {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setHarvestLevel("axe", 0);
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(6),
                "WWW",
                "BSB",
                "WWW",
                'B', "plateBronze",
                'S', "slimeball",
                'W', "plankWood");
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
}
