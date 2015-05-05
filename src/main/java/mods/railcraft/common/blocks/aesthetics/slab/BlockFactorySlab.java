/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.slab;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.railcraft.common.blocks.BlockFactory;
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;

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
        int renderId = Railcraft.getProxy().getRenderId();
        BlockRailcraftSlab.block = new BlockRailcraftSlab(renderId);
        BlockRailcraftSlab.block.setBlockName("railcraft.slab");
        RailcraftRegistry.register(BlockRailcraftSlab.block, ItemSlab.class);
        GameRegistry.registerTileEntity(TileSlab.class, "RCSlabTile");

        for (EnumBlockMaterial mat : EnumBlockMaterial.VALUES) {
            RailcraftRegistry.register(getItem(mat));

            switch (mat) {
                case SNOW:
                case ICE:
                    break;
                default:
                    ForestryPlugin.addBackpackItem("builder", getItem(mat));
            }
        }
    }

    @Override
    protected void doRecipeInit(ModuleManager.Module module) {
        EnumBlockMaterial.initialize();
        for (EnumBlockMaterial mat : EnumBlockMaterial.VALUES) {
            if (BlockRailcraftSlab.isEnabled(mat) && mat.getSourceItem() != null) {
                switch (mat) {
                    case SNOW:
                        CraftingPlugin.addShapedRecipe(BlockRailcraftSlab.getItem(mat, 3), "SSS", 'S', Blocks.snow_layer);
                        break;
                    default:
                        CraftingPlugin.addShapedRecipe(BlockRailcraftSlab.getItem(mat, 6), "SSS", 'S', mat.getSourceItem());
                        CraftingPlugin.addShapedRecipe(mat.getSourceItem(), "S", "S", 'S', BlockRailcraftSlab.getItem(mat));
                }
            }
        }
    }
}
