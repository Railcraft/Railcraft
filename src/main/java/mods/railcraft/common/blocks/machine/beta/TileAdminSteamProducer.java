package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileAdminSteamProducer extends TileMachineBase implements IFluidHandler {

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(worldObj))
            return;

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tile = tileCache.getTileOnSide(side);
            if (tile instanceof IFluidHandler) {
                IFluidHandler fluidHandler = (IFluidHandler) tile;
                if (fluidHandler.canFill(side, Fluids.STEAM.get())) {
                    FluidStack fluidStack = Fluids.STEAM.get(FluidHelper.BUCKET_VOLUME);
                    fluidHandler.fill(side, fluidStack, true);
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
        return Fluids.STEAM.get(resource.amount);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
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
        return getMachineType().getTexture(side);
    }
}
