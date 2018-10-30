/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.core.IContainerBlock;
import mods.railcraft.common.core.IContainerState;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.List;

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

    private static final MethodHandle GET_SILK_TOUCH_DROP_METHOD;
    private static final MethodHandle CAPTURE_DROPS_METHOD;

    static {
        MethodHandle handle = null;
        try {
            Method method = Block.class.getDeclaredMethod(Game.DEVELOPMENT_ENVIRONMENT ? "getSilkTouchDrop" : "func_180643_i", IBlockState.class);
            boolean oldAccessible = method.isAccessible();
            method.setAccessible(true);
            handle = MethodHandles.lookup().unreflect(method);
            method.setAccessible(oldAccessible);
        } catch (Throwable throwable) {
            Game.logThrowable("Cannot initialize silk touch drops", throwable);
        }
        GET_SILK_TOUCH_DROP_METHOD = handle;

        handle = null;
        try {
            Method method = Block.class.getDeclaredMethod("captureDrops", boolean.class);
            boolean oldAccessible = method.isAccessible();
            method.setAccessible(true);
            handle = MethodHandles.lookup().unreflect(method);
            method.setAccessible(oldAccessible);
        } catch (Throwable throwable) {
            Game.logThrowable("Cannot initialize silk touch drops", throwable);
        }
        CAPTURE_DROPS_METHOD = handle;
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
        if (state.getBlock() != Blocks.AIR)
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
        if (GET_SILK_TOUCH_DROP_METHOD == null)
            return emptyStack();
        try {
            return (ItemStack) GET_SILK_TOUCH_DROP_METHOD.invoke(state.getBlock(), state);
        } catch (Throwable throwable) {
            Game.logThrowable("Cannot get silk touch drops", throwable);
        }
        return emptyStack();
    }

    public static void dropRecursively(List<ItemStack> drops, World world, BlockPos pos, IBlockState state) {
        IBlockState oldState;
        do {
            oldState = state;
            drops.addAll(HarvestPlugin.getDropBlockAsItem(state, world, pos, 0));
            state = WorldPlugin.getBlockState(world, pos);
        } while (state != oldState);
        WorldPlugin.setBlockToAir(world, pos);
    }

    public static NonNullList<ItemStack> getDropBlockAsItem(IBlockState state, World world, BlockPos pos, int fortune) {
        final Block block = state.getBlock();
        startCaptureDrops(block);
        block.dropBlockAsItem(world, pos, state, fortune);
        return endCaptureDrops(block);
    }

    public static void startCaptureDrops(Block block) {
        if (CAPTURE_DROPS_METHOD == null)
            return;
        try {
            CAPTURE_DROPS_METHOD.invoke(block, true);
        } catch (Throwable throwable) {
            Game.logThrowable("Cannot start drop capture", throwable);
        }
    }

    @SuppressWarnings("unchecked")
    public static NonNullList<ItemStack> endCaptureDrops(Block block) {
        if (CAPTURE_DROPS_METHOD == null)
            return NonNullList.create();
        try {
            return (NonNullList<ItemStack>) CAPTURE_DROPS_METHOD.invoke(block, false);
        } catch (Throwable throwable) {
            Game.logThrowable("Cannot end drop capture", throwable);
        }

        return NonNullList.create();
    }
}
