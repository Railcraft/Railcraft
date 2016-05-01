/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockKey {

    public final IBlockState blockState;

    public BlockKey(IBlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + blockState.hashCode();
        hash = 73 * hash + blockState.getProperties().hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockKey) {
            final BlockKey other = (BlockKey) obj;
            if (blockState != other.blockState)
                return false;
            if (blockState instanceof IExtendedBlockState) {
                ((IExtendedBlockState) blockState).getUnlistedProperties().equals(((ExtendedBlockState) other.blockState).getUnlistedProperties());
            }
            return true;
        }
        return false;
    }

}
