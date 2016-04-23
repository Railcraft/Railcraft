/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.worldgen;

import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 4/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GenTools {
    public static final Predicate<IBlockState> STONE = new Predicate<IBlockState>() {
        @Override
        public boolean apply(@Nullable IBlockState input) {
            return input != null && input.getBlock() == Blocks.stone;
        }
    };
}
