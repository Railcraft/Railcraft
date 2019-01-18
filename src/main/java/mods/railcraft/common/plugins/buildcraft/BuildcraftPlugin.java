/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.buildcraft;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BuildcraftPlugin {

    public static void init() {
        if (Loader.isModLoaded("BuildCraftAPI|statements")) {
            BCAPIWrapper.init();
        }
    }

    public static void addFacade(Block block, int meta) {
        if (block == null) return;
        FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", String.format("%s@%d", Block.REGISTRY.getNameForObject(block), meta));
    }
}
