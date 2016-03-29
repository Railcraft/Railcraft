package mods.railcraft.common.plugins.forge;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class BlockPlugin {
    private static final Set<Block> gates = Sets.newIdentityHashSet();

    static {
        gates.add(Blocks.acacia_fence_gate);
        gates.add(Blocks.birch_fence_gate);
        gates.add(Blocks.dark_oak_fence_gate);
        gates.add(Blocks.jungle_fence_gate);
        gates.add(Blocks.oak_fence_gate);
        gates.add(Blocks.spruce_fence_gate);
    }

    public static boolean isGate(Block block) {
        return gates.contains(block);
    }
}
