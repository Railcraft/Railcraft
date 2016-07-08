/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.slab;

import mods.railcraft.common.blocks.BlockFactory;
import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static mods.railcraft.common.blocks.aesthetics.slab.BlockRailcraftSlab.getItem;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockFactorySlab extends BlockFactory {
    public BlockFactorySlab() {
        super("slab");
    }

    @Override
    protected void doBlockInit() {
        BlockRailcraftSlab.block = new BlockRailcraftSlab();
        BlockRailcraftSlab.block.setUnlocalizedName("railcraft.slab");
        RailcraftRegistry.register(BlockRailcraftSlab.block, ItemSlab.class);
        GameRegistry.registerTileEntity(TileSlab.class, "RCSlabTile");

        for (BlockMaterial mat : BlockMaterial.VALUES) {
            RailcraftRegistry.register(getItem(mat));

            switch (mat) {
                case SNOW:
                case ICE:
                case PACKED_ICE:
                    break;
                default:
                    ForestryPlugin.addBackpackItem("forestry.builder", getItem(mat));
            }
        }
    }

    @Override
    protected void doRecipeInit() {
        BlockMaterial.initialize();
        for (BlockMaterial mat : BlockMaterial.VALUES) {
            if (BlockRailcraftSlab.isEnabled(mat) && mat.getSourceItem() != null) {
                switch (mat) {
                    case SNOW:
                        CraftingPlugin.addRecipe(BlockRailcraftSlab.getItem(mat, 3), "SSS", 'S', Blocks.SNOW_LAYER);
                        break;
                    default:
                        CraftingPlugin.addRecipe(BlockRailcraftSlab.getItem(mat, 6), "SSS", 'S', mat.getSourceItem());
                        CraftingPlugin.addRecipe(mat.getSourceItem(), "S", "S", 'S', BlockRailcraftSlab.getItem(mat));
                }
            }
        }
    }
}
