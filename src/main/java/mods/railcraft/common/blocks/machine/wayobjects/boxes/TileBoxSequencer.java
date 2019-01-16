/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.interfaces.ITileRedstoneEmitter;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

public class TileBoxSequencer extends TileBoxBase implements ITileRedstoneEmitter {

    private static final int MAX_ITERATIONS = 64;
    private EnumFacing sideOutput = EnumFacing.NORTH;
    private boolean powerState;
    private boolean neighborState;


    @Override
    public IEnumMachine<?> getMachineType() {
        return SignalBoxVariant.SEQUENCER;
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block neighborBlock, BlockPos pos) {
        super.onNeighborBlockChange(state, neighborBlock, pos);
        if (world.isRemote)
            return;
        boolean p = PowerPlugin.isBlockBeingPoweredByRepeater(world, getPos());
        if (!powerState && p) {
            powerState = true;
            incrementSequencer(true, new HashSet<>(), 0);
        } else
            powerState = p;
    }

    @Override
    public void onNeighborStateChange(TileBoxBase neighbor, EnumFacing side) {
        if (world.isRemote)
            return;
        if (neighbor instanceof TileBoxSequencer)
            return;
        if (neighbor instanceof TileBoxCapacitor)
            return;
        boolean p = neighbor.isEmittingRedstone(side);
        if (!neighborState && p) {
            neighborState = true;
            incrementSequencer(true, new HashSet<>(), 0);
        } else
            neighborState = p;
    }

    private void incrementSequencer(boolean firstPass, Set<TileEntity> visitedFirstPass, int numberOfIterations) {
        if (firstPass) {
            visitedFirstPass.add(this);
            TileEntity tile = tileCache.getTileOnSide(sideOutput);
            if (tile instanceof TileBoxSequencer && !visitedFirstPass.contains(tile)) {
                TileBoxSequencer box = (TileBoxSequencer) tile;
                box.incrementSequencer(true, visitedFirstPass, numberOfIterations);
                return;
            }
        }

        EnumFacing newSide = sideOutput.rotateAround(EnumFacing.Axis.Y);
        while (newSide != sideOutput && !canOutputToSide(newSide)) {
            newSide = newSide.rotateAround(EnumFacing.Axis.Y);
        }
        sideOutput = newSide;
        updateNeighbors();

        if (numberOfIterations >= MAX_ITERATIONS)
            return;

        TileEntity tile = tileCache.getTileOnSide(sideOutput);
        if (tile instanceof TileBoxSequencer) {
            TileBoxSequencer box = (TileBoxSequencer) tile;
            box.incrementSequencer(false, visitedFirstPass, numberOfIterations + 1);
        }
    }

    private boolean canOutputToSide(EnumFacing side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        if (tile instanceof TileBoxSequencer)
            return true;
        if (tile instanceof TileBoxBase)
            return ((TileBoxBase) tile).canReceiveAspect();
        IBlockState state = WorldPlugin.getBlockState(world, getPos().offset(side));
        Block block = state.getBlock();
        if (block == Blocks.REDSTONE_WIRE)
            return true;
        if (block == Blocks.UNPOWERED_REPEATER || block == Blocks.POWERED_REPEATER) {
            EnumFacing inputSide = state.getValue(BlockHorizontal.FACING);
            return side.getOpposite() == inputSide;
        }
        return false;
    }

    private void updateNeighbors() {
        sendUpdateToClient();
        notifyBlocksOfNeighborChange();
        updateNeighborBoxes();
    }

    @Override
    public int getPowerOutput(EnumFacing side) {
        TileEntity tile = tileCache.getTileOnSide(side.getOpposite());
        if (tile instanceof TileBoxBase)
            return NO_POWER;
        return sideOutput.getOpposite() == side ? FULL_POWER : NO_POWER;
    }

    @Override
    public boolean isEmittingRedstone(EnumFacing side) {
        return sideOutput == side;
    }

    @Override
    public SignalAspect getBoxSignalAspect(@Nullable EnumFacing side) {
        return sideOutput == side ? SignalAspect.GREEN : SignalAspect.RED;
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("sideOutput", (byte) sideOutput.ordinal());
        data.setBoolean("powerState", powerState);
        data.setBoolean("neighborState", neighborState);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        sideOutput = EnumFacing.byIndex(data.getByte("sideOutput"));
        powerState = data.getBoolean("powerState");
        neighborState = data.getBoolean("neighborState");
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(sideOutput.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        sideOutput = EnumFacing.byIndex(data.readByte());
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isConnected(EnumFacing side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        if (tile instanceof TileBoxSequencer)
            return true;
        if (tile instanceof TileBoxBase)
            return ((TileBoxBase) tile).canReceiveAspect() || ((TileBoxBase) tile).canTransferAspect();
        return false;
    }

    @Override
    public boolean canTransferAspect() {
        return true;
    }

    @Override
    public boolean canReceiveAspect() {
        return true;
    }
}
