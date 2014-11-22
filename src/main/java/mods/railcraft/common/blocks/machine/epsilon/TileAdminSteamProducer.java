package mods.railcraft.common.blocks.machine.epsilon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileAdminSteamProducer extends TileMachineBase implements IFluidHandler {

    private boolean powered;

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        checkRedstone();
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        checkRedstone();
    }

    private void checkRedstone() {
        if (Game.isNotHost(getWorld()))
            return;
        boolean p = PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord, zCoord);
        if (powered != p) {
            powered = p;
            sendUpdateToClient();
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(worldObj))
            return;

        if (!powered)
            return;

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
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
    public IEnumMachine getMachineType() {
        return EnumMachineEpsilon.ADMIN_STEAM_PRODUCER;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (!powered)
            return null;
        return Fluids.STEAM.get(resource.amount);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (!powered)
            return null;
        return Fluids.STEAM.get(FluidHelper.BUCKET_VOLUME);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        FluidStack fluidStack = Fluids.STEAM.get(FluidHelper.BUCKET_VOLUME);
        return new FluidTankInfo[]{
            new FluidTankInfo(fluidStack, FluidHelper.BUCKET_VOLUME)
        };
    }

    @Override
    public IIcon getIcon(int side) {
        return getMachineType().getTexture(powered ? 0 : 6);
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
