/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.lamp;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.railcraft.common.blocks.BlockFactory;


import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.ItemRegistry;


/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockFactoryLantern extends BlockFactory {

    public BlockFactoryLantern() {
        super("stonelamp");
    }

    @Override
    protected void doBlockInit() {
        int renderId = Railcraft.getProxy().getRenderId();
        BlockStoneLantern.block = new BlockStoneLantern(renderId);
        BlockStoneLantern.block.setBlockName("railcraft.stonelamp");
        GameRegistry.registerBlock(BlockStoneLantern.block, ItemStoneLantern.class, BlockStoneLantern.block.getUnlocalizedName());

        for (EnumStoneLantern lamp : EnumStoneLantern.VALUES) {
            ItemRegistry.registerItemStack(lamp.getTag(), lamp.getItem());

            ForestryPlugin.addBackpackItem("builder", lamp.getItem());
        }
    }

    @Override
    protected void doRecipeInit(ModuleManager.Module module) {
        EnumStoneLantern.initialize();
    }

}
