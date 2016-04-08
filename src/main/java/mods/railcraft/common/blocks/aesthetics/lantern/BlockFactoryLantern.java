/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.lantern;

import com.google.common.collect.BiMap;
import mods.railcraft.common.blocks.BlockFactory;
import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.blocks.aesthetics.brick.BlockBrick;
import mods.railcraft.common.blocks.aesthetics.slab.BlockRailcraftSlab;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockFactoryLantern extends BlockFactory {

    public BlockFactoryLantern() {
        super("lamp");
    }

    @Override
    protected void doBlockInit() {
        BlockLantern.stone = createLantern(BlockLantern.STONE_LANTERN);
        BlockLantern.metal = createLantern(BlockLantern.METAL_LANTERN);
    }

    private BlockLantern createLantern(BiMap<BlockMaterial, Integer> variants) {
        BlockLantern block = new BlockLantern(variants);
        block.setUnlocalizedName("railcraft.lantern.metal");
        RailcraftRegistry.register(block, ItemLantern.class);

        for (BlockMaterial mat : variants.keySet()) {
            RailcraftRegistry.register(block.getItem(mat));

            ForestryPlugin.addBackpackItem("builder", block.getItem(mat));
        }
        return block;
    }

    @Override
    protected void doRecipeInit() {
        BlockMaterial.initialize();
        if (BlockLantern.stone != null) {
            for (BlockMaterial mat : BlockLantern.STONE_LANTERN.keySet()) {
                if (BlockLantern.stone.isAvailable(mat) && mat.getState() != null) {
                    Object slab = null;
                    if (mat.getState().getBlock() instanceof BlockBrick)
                        slab = BlockRailcraftSlab.getItem(mat);
                    else if (mat == BlockMaterial.SANDSTONE)
                        slab = new ItemStack(Blocks.stone_slab, 1, 1);
                    else if (mat == BlockMaterial.STONE_BRICK)
                        slab = new ItemStack(Blocks.stone_slab, 1, 0);
                    if (slab == null)
                        slab = mat.getCraftingEquivalent();
                    CraftingPlugin.addRecipe(BlockLantern.stone.getItem(mat), " S ", " T ", " S ", 'S', slab, 'T', new ItemStack(Blocks.torch));
                }
            }
        }
        if (BlockLantern.metal != null) {
            for (BlockMaterial mat : BlockLantern.METAL_LANTERN.keySet()) {
                if (BlockLantern.metal.isAvailable(mat)) {
                    Object slab = BlockRailcraftSlab.getItem(mat);
                    if (slab == null)
                        slab = mat.getCraftingEquivalent();
                    CraftingPlugin.addRecipe(BlockLantern.stone.getItem(mat), " S ", " T ", " S ", 'S', slab, 'T', new ItemStack(Blocks.torch));
                }
            }
        }
    }

}
