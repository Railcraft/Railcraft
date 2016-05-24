/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.core.IPostConnection.ConnectStyle;
import mods.railcraft.common.blocks.aesthetics.lantern.BlockLantern;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class PostConnectionHelper {

    private static final Set<Block> canConnect = new HashSet<Block>();
    private static final Set<Block> noConnect = new HashSet<Block>();

    static {
        canConnect.add(Blocks.glowstone);
        canConnect.add(Blocks.redstone_lamp);
        canConnect.add(Blocks.lit_redstone_lamp);
        canConnect.add(Blocks.glass);
        canConnect.add(Blocks.mob_spawner);
        canConnect.add(Blocks.piston);

        noConnect.add(Blocks.dirt);
        noConnect.add(Blocks.stone);
        noConnect.add(Blocks.gravel);
        noConnect.add(Blocks.grass);
        noConnect.add(Blocks.sand);
        noConnect.add(Blocks.bedrock);
        noConnect.add(Blocks.coal_ore);
        noConnect.add(Blocks.diamond_ore);
        noConnect.add(Blocks.emerald_ore);
        noConnect.add(Blocks.gold_ore);
        noConnect.add(Blocks.iron_ore);
        noConnect.add(Blocks.lapis_ore);
        noConnect.add(Blocks.redstone_ore);
        noConnect.add(Blocks.lit_redstone_ore);
        noConnect.add(Blocks.clay);
        noConnect.add(Blocks.snow);
        noConnect.add(Blocks.snow_layer);
        noConnect.add(Blocks.melon_block);
        noConnect.add(Blocks.pumpkin);
        noConnect.add(Blocks.tnt);
        noConnect.add(Blocks.soul_sand);
        noConnect.add(Blocks.netherrack);
        noConnect.add(Blocks.sponge);
    }

    public static ConnectStyle connect(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        try {
            Block block = state.getBlock();
            if (block instanceof IPostConnection && ((IPostConnection) block).connectsToPost(world, pos, state, side) == ConnectStyle.NONE)
                return ConnectStyle.NONE;
        } catch (Error error) {
            Game.logErrorAPI("Railcraft", error, IPostConnection.class);
            return ConnectStyle.NONE;
        }

        BlockPos otherPos = pos.offset(side);

        if (world.isAirBlock(otherPos))
            return ConnectStyle.NONE;

        IBlockState otherState = WorldPlugin.getBlockState(world, otherPos);
        Block otherBlock = otherState.getBlock();

        EnumFacing oppositeSide = side.getOpposite();

        try {
            if (otherBlock instanceof IPostConnection)
                return ((IPostConnection) otherBlock).connectsToPost(world, otherPos, otherState, oppositeSide);
        } catch (Error error) {
            Game.logErrorAPI("Railcraft", error, IPostConnection.class);
        }

        if (otherBlock instanceof BlockPostBase)
            return ConnectStyle.TWO_THIN;

        if (noConnect.contains(otherBlock))
            return ConnectStyle.NONE;

        if (canConnect.contains(otherBlock))
            return ConnectStyle.TWO_THIN;

        if (otherBlock instanceof BlockWallSign) {
            return otherState.getValue(BlockWallSign.FACING) == side ? ConnectStyle.SINGLE_THICK : ConnectStyle.NONE;
        }

        if (otherBlock instanceof BlockLantern)
            return ConnectStyle.SINGLE_THICK;

        if (world.isSideSolid(pos, oppositeSide, false))
            return ConnectStyle.TWO_THIN;

        // RedPower 2 compatibility
//        if (Blocks.blocksList[id] != null && Blocks.blocksList[id].getClass().getSimpleName().equals("BlockShapedLamp")) {
//            return true;
//        }

        return ConnectStyle.NONE;
    }

}
