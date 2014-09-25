/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import java.util.HashSet;
import net.minecraft.block.Block;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockSet extends HashSet<BlockKey> {

    public boolean add(Block block, int meta) {
        return add(new BlockKey(block, meta));
    }

    public boolean add(Block block) {
        return add(new BlockKey(block));
    }

    public boolean contains(Block block, int meta) {
        if (contains(new BlockKey(block)))
            return true;
        return contains(new BlockKey(block, meta));
    }

}
