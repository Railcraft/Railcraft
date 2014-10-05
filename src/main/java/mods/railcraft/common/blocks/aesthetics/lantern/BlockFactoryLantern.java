/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.lantern;

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
        super("lamp");
    }

    @Override
    protected void doBlockInit() {
        int renderId = Railcraft.getProxy().getRenderId();
        BlockLantern.stone = new BlockLantern(renderId, new LanternProxyStone());
        BlockLantern.stone.setBlockName("railcraft.lantern.stone");
        GameRegistry.registerBlock(BlockLantern.stone, ItemLantern.class, BlockLantern.stone.getUnlocalizedName());

        for (EnumLanternStone lamp : EnumLanternStone.VALUES) {
            ItemRegistry.registerItemStack(lamp.getTag(), lamp.getItem());

            ForestryPlugin.addBackpackItem("builder", lamp.getItem());
        }

        BlockLantern.metal = new BlockLantern(renderId, new LanternProxyMetal());
        BlockLantern.metal.setBlockName("railcraft.lantern.metal");
        GameRegistry.registerBlock(BlockLantern.metal, ItemLantern.class, BlockLantern.metal.getUnlocalizedName());

        for (EnumLanternMetal lamp : EnumLanternMetal.VALUES) {
            ItemRegistry.registerItemStack(lamp.getTag(), lamp.getItem());

            ForestryPlugin.addBackpackItem("builder", lamp.getItem());
        }
    }

    @Override
    protected void doRecipeInit(ModuleManager.Module module) {
        EnumLanternStone.initialize();
        EnumLanternMetal.initialize();
    }

}
