/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Register an item here to designate it as a possible ballast that can be used
 * in the Bore.
 * <p/>
 * It is expected that ballast is affected by gravity.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BallastRegistry {

    private static final Set<IBlockState> ballastRegistry = new HashSet<>();

    static {
        registerBallast(Blocks.GRAVEL, 0);
    }

    public static void registerBallast(Block block, int metadata) {
        ballastRegistry.add(block.getStateFromMeta(metadata));
    }

    public static boolean isItemBallast(ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return false;
        IBlockState state = InvTools.getBlockStateFromStack(stack);
        return state != null && ballastRegistry.contains(state);
    }

    public static Set<IBlockState> getRegisteredBallasts() {
        return ballastRegistry;
    }

}
