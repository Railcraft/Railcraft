/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.core.IPostConnection.ConnectStyle;
import mods.railcraft.common.blocks.aesthetics.materials.BlockLantern;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class PostConnectionHelper {

    private static final Set<Block> canConnect = new HashSet<>();
    private static final Set<Block> noConnect = new HashSet<>();

    static {
        canConnect.add(Blocks.GLOWSTONE);
        canConnect.add(Blocks.REDSTONE_LAMP);
        canConnect.add(Blocks.LIT_REDSTONE_LAMP);
        canConnect.add(Blocks.GLASS);
        canConnect.add(Blocks.MOB_SPAWNER);
        canConnect.add(Blocks.PISTON);

        noConnect.add(Blocks.DIRT);
        noConnect.add(Blocks.STONE);
        noConnect.add(Blocks.GRAVEL);
        noConnect.add(Blocks.GRASS);
        noConnect.add(Blocks.SAND);
        noConnect.add(Blocks.BEDROCK);
        noConnect.add(Blocks.COAL_ORE);
        noConnect.add(Blocks.DIAMOND_ORE);
        noConnect.add(Blocks.EMERALD_ORE);
        noConnect.add(Blocks.GOLD_ORE);
        noConnect.add(Blocks.IRON_ORE);
        noConnect.add(Blocks.LAPIS_ORE);
        noConnect.add(Blocks.REDSTONE_ORE);
        noConnect.add(Blocks.LIT_REDSTONE_ORE);
        noConnect.add(Blocks.CLAY);
        noConnect.add(Blocks.SNOW);
        noConnect.add(Blocks.SNOW_LAYER);
        noConnect.add(Blocks.MELON_BLOCK);
        noConnect.add(Blocks.PUMPKIN);
        noConnect.add(Blocks.TNT);
        noConnect.add(Blocks.SOUL_SAND);
        noConnect.add(Blocks.NETHERRACK);
        noConnect.add(Blocks.SPONGE);
    }

    public static ConnectStyle connect(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        try {
            Block block = state.getBlock();
            if (block instanceof IPostConnection && ((IPostConnection) block).connectsToPost(world, pos, state, side) == ConnectStyle.NONE)
                return ConnectStyle.NONE;
        } catch (Error error) {
            Game.log().api(Railcraft.NAME, error, IPostConnection.class);
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
            Game.log().api(Railcraft.NAME, error, IPostConnection.class);
        }

        if (otherBlock instanceof BlockPostBase)
            return ConnectStyle.TWO_THIN;

        if (noConnect.contains(otherBlock))
            return ConnectStyle.NONE;

        if (canConnect.contains(otherBlock))
            return ConnectStyle.TWO_THIN;

//        if (otherBlock instanceof BlockFence) {
//            return  ConnectStyle.TWO_THIN ;
//        }

        if (otherBlock instanceof BlockFenceGate) {
            return otherState.getValue(BlockFenceGate.FACING).getAxis() != side.getAxis() ? ConnectStyle.TWO_THIN : ConnectStyle.NONE;
        }

        if (otherBlock instanceof BlockWallSign) {
            return otherState.getValue(BlockWallSign.FACING) == side ? ConnectStyle.SINGLE_THICK : ConnectStyle.NONE;
        }

        if (otherBlock instanceof BlockLantern)
            return ConnectStyle.SINGLE_THICK;

        if (world.isSideSolid(pos, oppositeSide, false))
            return ConnectStyle.TWO_THIN;

        // RedPower 2 compatibility
//        if (Blocks.BLOCKSLIST[id] != null && Blocks.BLOCKSLIST[id].getClass().getSimpleName().equals("BlockShapedLamp")) {
//            return true;
//        }

        return ConnectStyle.NONE;
    }

}
