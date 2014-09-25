/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import mods.railcraft.common.util.collections.BlockKey;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Register an item here to designate it as a possible ballast that can be used
 * in the Bore.
 *
 * It is expected that ballast is affected by gravity.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BallastRegistry {

    private static final Set<BlockKey> ballastRegistry = new HashSet<BlockKey>();

    static {
        registerBallast(Blocks.gravel, 0);
    }

    public static void registerBallast(Block block, int metadata) {
        ballastRegistry.add(new BlockKey(block, metadata));
    }

    public static boolean isItemBallast(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock)
            return ballastRegistry.contains(new BlockKey(((ItemBlock) stack.getItem()).field_150939_a, stack.getItemDamage()));
        return false;
    }

    public static Set<BlockKey> getRegisteredBallasts() {
        return ballastRegistry;
    }

}
