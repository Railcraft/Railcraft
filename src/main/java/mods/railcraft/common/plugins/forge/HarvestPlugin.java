/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.core.IContainerBlock;
import mods.railcraft.common.core.IContainerState;
import mods.railcraft.common.util.misc.Code;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class HarvestPlugin {

    public enum ToolClass {
        PICKAXE("pickaxe"),
        AXE("axe"),
        SHOVEL("shovel");
        public final String name;

        ToolClass(String name) {
            this.name = name;
        }

        public String getToolString(int level) {
            return name + ":" + level;
        }
    }

    private HarvestPlugin() {
    }

    public static void setToolClass(Item item, String toolClass, int level) {
        item.setHarvestLevel(toolClass, level);
    }

    public static void setBlockHarvestLevel(String toolClass, int level, IContainerBlock blockContainer) {
        Block block = blockContainer.block();
        if (block != null)
            setBlockHarvestLevel(toolClass, level, block);
    }

    public static void setBlockHarvestLevel(String toolClass, int level, Block block) {
        block.setHarvestLevel(toolClass, level);
    }

    public static void setStateHarvestLevel(String toolClassLevel, IContainerState stateContainer) {
        String[] tokens = toolClassLevel.split(":");
        if (tokens.length != 2)
            throw new IllegalArgumentException("Tool class string must be of the format: <toolClass>:<level>");
        String toolClass = tokens[0];
        int level = Integer.parseInt(tokens[1]);
        setStateHarvestLevel(toolClass, level, stateContainer);
    }

    public static void setStateHarvestLevel(String toolClass, int level, IContainerState stateContainer) {
        IBlockState state = stateContainer.getDefaultState();
        if (state != null)
            setStateHarvestLevel(toolClass, level, state);
    }

    public static void setStateHarvestLevel(String toolClass, int level, @Nullable IBlockState blockState) {
        if (blockState != null)
            blockState.getBlock().setHarvestLevel(toolClass, level, blockState);
    }

    public static int getHarvestLevel(IBlockState state, String toolClass) {
        Block block = state.getBlock();
        return block.isToolEffective(toolClass, state) ? block.getHarvestLevel(state) : -1;
    }

    public static ItemStack getSilkTouchDrop(IBlockState state) {
        // Block#getSilkTouchDrop
        return Code.findMethod(Block.class, "func_180643_i", ItemStack.class, IBlockState.class)
                .invoke(state.getBlock(), state).orElse(emptyStack());
    }
}
