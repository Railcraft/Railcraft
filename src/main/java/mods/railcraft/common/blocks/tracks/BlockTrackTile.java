/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks;

import mods.railcraft.common.blocks.IRailcraftBlockTile;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class BlockTrackTile<T extends TileRailcraft> extends BlockTrack implements IRailcraftBlockTile<T> {

    private final ThreadLocal<Boolean> lastClearResult = new ThreadLocal<>();

    protected BlockTrackTile() {
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
        return false;
    }

    //TODO: Move drop code here? We have a reference to the TileEntity now.
    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        if (Game.isHost(worldIn))
            lastClearResult.set(clearBlock(state, worldIn, pos, harvesters.get()));
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (Game.isHost(world)) {
            if (player.capabilities.isCreativeMode) {
                return clearBlock(state, world, pos, player);
            } else {
                dropBlockAsItem(world, pos, state, 0);
                return lastClearResult.get() == null || lastClearResult.get();
            }
        }
        return true;
    }

    public boolean clearBlock(IBlockState state, World world, BlockPos pos, @Nullable EntityPlayer player) {
        return world.setBlockToAir(pos);
    }

}
