package mods.railcraft.common.blocks.machine.epsilon;

import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TileAdminSteamProducer extends TileMachineBase<EnumMachineEpsilon> implements IFluidHandler {

    private boolean powered;

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        checkRedstone();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        checkRedstone();
    }

    private void checkRedstone() {
        if (Game.isNotHost(getWorld()))
            return;
        boolean p = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
        if (powered != p) {
            powered = p;
            sendUpdateToClient();
        }
    }

    @Override
    public void update() {
        super.update();
        if (Game.isNotHost(worldObj))
            return;

        if (!powered)
            return;

        for (EnumFacing side : EnumFacing.VALUES) {
            TileEntity tile = tileCache.getTileOnSide(side);
            if (tile instanceof IFluidHandler) {
                IFluidHandler fluidHandler = (IFluidHandler) tile;
                if (fluidHandler.canFill(side.getOpposite(), Fluids.STEAM.get())) {
                    FluidStack fluidStack = Fluids.STEAM.get(FluidHelper.BUCKET_VOLUME);
                    fluidHandler.fill(side.getOpposite(), fluidStack, true);
                }
            }
        }

    }

    @Override
    public EnumMachineEpsilon getMachineType() {
        return EnumMachineEpsilon.ADMIN_STEAM_PRODUCER;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (!powered)
            return null;
        return Fluids.STEAM.get(resource.amount);
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        if (!powered)
            return null;
        return Fluids.STEAM.get(FluidHelper.BUCKET_VOLUME);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        FluidStack fluidStack = Fluids.STEAM.get(FluidHelper.BUCKET_VOLUME);
        return new FluidTankInfo[]{
                new FluidTankInfo(fluidStack, FluidHelper.BUCKET_VOLUME)
        };
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        boolean p = data.readBoolean();
        if (powered != p) {
            powered = p;
            markBlockForUpdate();
        }
    }

}
