/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.stairs;

import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.BlockFactory;
import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static mods.railcraft.common.blocks.aesthetics.BlockMaterial.*;
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
        BlockRailcraftStairs.block.setRegistryName("railcraft.stair");
        RailcraftRegistry.register(BlockRailcraftStairs.block, ItemStair.class);
        GameRegistry.registerTileEntity(TileStair.class, "RCStairTile");

        for (BlockMaterial mat : BlockMaterial.STAIR_MATS) {
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
        BlockMaterial.initialize();
        for (BlockMaterial mat : BlockMaterial.STAIR_MATS) {
            if (BlockRailcraftStairs.isEnabled(mat) && mat.getSourceItem() != null) {
                CraftingPlugin.addShapedRecipe(BlockRailcraftStairs.getItem(mat, 4), "S  ", "SS ", "SSS", 'S', mat.getSourceItem());
                IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(BlockRailcraftStairs.getItem(mat), true, false);
                recipe.addOutput(mat.getSourceItem(), 1.0f);
            }
        }

        addRockCrusherRecipe(BrickTheme.ABYSSAL, ABYSSAL_BLOCK, ABYSSAL_BRICK, ABYSSAL_COBBLE, ABYSSAL_FITTED);
        addRockCrusherRecipe(BrickTheme.BLEACHEDBONE, BLEACHEDBONE_BLOCK, BLEACHEDBONE_BRICK, BLEACHEDBONE_COBBLE, BLEACHEDBONE_FITTED);
        addRockCrusherRecipe(BrickTheme.BLOODSTAINED, BLOODSTAINED_BLOCK, BLOODSTAINED_BRICK, BLOODSTAINED_COBBLE, BLOODSTAINED_FITTED);
        addRockCrusherRecipe(BrickTheme.FROSTBOUND, FROSTBOUND_BLOCK, FROSTBOUND_BRICK, FROSTBOUND_COBBLE, FROSTBOUND_FITTED);
        addRockCrusherRecipe(BrickTheme.INFERNAL, INFERNAL_BLOCK, INFERNAL_BRICK, INFERNAL_COBBLE, INFERNAL_FITTED);
        addRockCrusherRecipe(BrickTheme.NETHER, NETHER_BLOCK, NETHER_COBBLE, NETHER_FITTED);
        addRockCrusherRecipe(BrickTheme.QUARRIED, QUARRIED_BLOCK, QUARRIED_BRICK, QUARRIED_COBBLE, QUARRIED_FITTED);
        addRockCrusherRecipe(BrickTheme.SANDY, SANDY_BLOCK, SANDY_BRICK, SANDY_COBBLE, SANDY_FITTED);
    }

    private void addRockCrusherRecipe(BrickTheme brickTheme, BlockMaterial... types) {
        if (brickTheme.getBlock() == null)
            return;
        ItemStack output = brickTheme.get(BrickVariant.COBBLE, 1);
        for (BlockMaterial mat : types) {
            if (!BlockRailcraftStairs.isEnabled(mat))
                continue;
            IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(getItem(mat), true, false);
            recipe.addOutput(output, 1.0F);
        }
    }
}
