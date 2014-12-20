/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.lantern;

import mods.railcraft.common.blocks.BlockFactory;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;


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
        RailcraftRegistry.register(BlockLantern.stone, ItemLantern.class);

        for (EnumLanternStone lamp : EnumLanternStone.VALUES) {
            RailcraftRegistry.register(lamp.getItem());

            ForestryPlugin.addBackpackItem("builder", lamp.getItem());
        }

        BlockLantern.metal = new BlockLantern(renderId, new LanternProxyMetal());
        BlockLantern.metal.setBlockName("railcraft.lantern.metal");
        RailcraftRegistry.register(BlockLantern.metal, ItemLantern.class);

        for (EnumLanternMetal lamp : EnumLanternMetal.VALUES) {
            RailcraftRegistry.register(lamp.getItem());

            ForestryPlugin.addBackpackItem("builder", lamp.getItem());
        }
    }

    @Override
    protected void doRecipeInit(ModuleManager.Module module) {
        EnumLanternStone.initialize();
        EnumLanternMetal.initialize();
    }

}
