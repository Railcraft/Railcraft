/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import net.minecraft.block.state.IBlockState;

import java.util.HashSet;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockSet extends HashSet<BlockKey> {

    public boolean add(IBlockState blockState) {
        return add(new BlockKey(blockState));
    }

    public boolean contains(IBlockState blockState) {
        return contains(new BlockKey(blockState));
    }

}
