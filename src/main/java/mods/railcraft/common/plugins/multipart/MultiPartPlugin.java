/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.multipart;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class MultiPartPlugin {

    public static void addMicroMaterial(Block block, int meta) {
        if (block == null) return;
        FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(block, 1, meta));
    }

}
