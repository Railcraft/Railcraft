/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.misc;

import mods.railcraft.common.plugins.multipart.MultiPartPlugin;
import net.minecraft.block.Block;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class MicroBlockPlugin {

    public static void addMicroBlockCandidate(Block block, int meta) {
        if (block == null) return;
//        BuildcraftPlugin.addFacade(block, meta);
        MultiPartPlugin.addMicroMaterial(block, meta);
    }

}
