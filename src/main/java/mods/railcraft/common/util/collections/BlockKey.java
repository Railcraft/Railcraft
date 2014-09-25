/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import net.minecraft.block.Block;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockKey {

    public final Block block;
    public final int metadata;

    public BlockKey(Block block) {
        this.block = block;
        this.metadata = -1;
    }

    public BlockKey(Block block, int metadata) {
        this.block = block;
        this.metadata = metadata;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.block.hashCode();
        hash = 73 * hash + this.metadata;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final BlockKey other = (BlockKey) obj;
        if (this.block != other.block) return false;
        return this.metadata == other.metadata;
    }

}
