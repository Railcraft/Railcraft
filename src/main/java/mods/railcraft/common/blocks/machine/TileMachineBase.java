/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.BlockContainerRailcraftSubtyped;
import mods.railcraft.common.blocks.ISmartTile;
import mods.railcraft.common.blocks.RailcraftTickingTileEntity;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public abstract class TileMachineBase extends RailcraftTickingTileEntity implements ISmartTile {

    private boolean checkedBlock;

    public abstract IEnumMachine<?> getMachineType();

    @Override
    public String getLocalizationTag() {
        return getMachineType().getLocalizationTag() + ".name";
    }

    @Override
    public final short getId() {
        return (short) getMachineType().ordinal();
    }

    public List<ItemStack> getDrops(int fortune) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        items.add(getMachineType().getStack());
        return items;
    }

    public List<ItemStack> getBlockDroppedSilkTouch(int fortune) {
        return getDrops(fortune);
    }

    public boolean canSilkHarvest(EntityPlayer player) {
        return false;
    }

    public void initFromItem(ItemStack stack) {
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(world))
            return;

        // Check and fix invalid block ids
        if (!checkedBlock) {
            checkedBlock = true;

            if (!getMachineType().isAvailable()) {
                world.setBlockToAir(getPos());
                return;
            }

            if (getBlockType() != getMachineType().block()) {
                Game.log(Level.INFO, "Updating Machine Block: {0} {1}->{2}, [{3}]", getClass().getSimpleName(), getBlockType(), getMachineType().block(), getPos());
                world.setBlockState(getPos(), getMachineType().getDefaultState(), 3);
                validate();
                world.setTileEntity(getPos(), this);
                updateContainingBlockInfo();
            }

            IBlockState oldState = world.getBlockState(getPos());
            IEnumMachine variant = (IEnumMachine) ((BlockMachine) oldState.getBlock()).getVariant(oldState);
            if (getMachineType() != variant) {
                IBlockState newState = getMachineType().getDefaultState();
                if (newState != null) {
                    world.setBlockState(getPos(), newState, 3);
                    validate();
                    world.setTileEntity(getPos(), this);
                    Game.log(Level.INFO, "Updating Machine State: {0} {1}->{2}, [{3}]", getClass().getSimpleName(), oldState, newState, getPos());
                    updateContainingBlockInfo();
                }
            }
        }
    }

    public boolean isPoweringTo(EnumFacing side) {
        return false;
    }

//    public int colorMultiplier() {
//        return 16777215;
//    }
//
//    public boolean recolourBlock(EnumDyeColor color) {
//        return false;
//    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return !(oldState.getBlock() == getBlockType() && newSate.getBlock() == getBlockType()
                && ((BlockContainerRailcraftSubtyped) getBlockType()).getVariant(oldState) == ((BlockContainerRailcraftSubtyped) getBlockType()).getVariant(newSate));
    }
}
