/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.stairs;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.BlockFactory;
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.brick.EnumBrick;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.item.ItemStack;

import static mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial.*;
import static mods.railcraft.common.blocks.aesthetics.stairs.BlockRailcraftStairs.getItem;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockFactoryStairs extends BlockFactory {
    public BlockFactoryStairs() {
        super("stair");
    }

    @Override
    protected void doBlockInit() {
        int renderId = Railcraft.getProxy().getRenderId();
        BlockRailcraftStairs.block = new BlockRailcraftStairs(renderId);
        BlockRailcraftStairs.block.setBlockName("railcraft.stair");
        RailcraftRegistry.register(BlockRailcraftStairs.block, ItemStair.class);
        GameRegistry.registerTileEntity(TileStair.class, "RCStairTile");

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
            if (BlockRailcraftStairs.isEnabled(mat) && mat.getSourceItem() != null) {
                CraftingPlugin.addShapedRecipe(BlockRailcraftStairs.getItem(mat, 4), "S  ", "SS ", "SSS", 'S', mat.getSourceItem());
            }
        }
    }
}
