/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.ISmartTile;
import mods.railcraft.common.blocks.TileRailcraftTicking;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.Predicates;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class TileAdminSteamProducer extends TileRailcraftTicking implements ISmartTile {

    private static final IFluidHandler HANDLER = new IFluidHandler() {
        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new IFluidTankProperties[] {new IFluidTankProperties() {
                final FluidStack steam = Fluids.STEAM.getB(4);

                @Override
                public @Nullable FluidStack getContents() {
                    return steam;
                }

                @Override
                public int getCapacity() {
                    return 4 * FluidTools.BUCKET_VOLUME;
                }

                @Override
                public boolean canFill() {
                    return false;
                }

                @Override
                public boolean canDrain() {
                    return true;
                }

                @Override
                public boolean canFillFluidType(FluidStack fluidStack) {
                    return false;
                }

                @Override
                public boolean canDrainFluidType(FluidStack fluidStack) {
                    return Fluids.STEAM.is(fluidStack);
                }
            }};
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return 0;
        }

        @Override
        public @Nullable FluidStack drain(FluidStack resource, boolean doDrain) {
            return Fluids.STEAM.is(resource) ? resource : null;
        }

        @Override
        public @Nullable FluidStack drain(int maxDrain, boolean doDrain) {
            return Fluids.STEAM.get(maxDrain);
        }
    };
    boolean powered;

    public TileAdminSteamProducer() {
        powered = false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (powered && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (powered && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(HANDLER);
        return super.getCapability(capability, facing);
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block, BlockPos pos) {
        super.onNeighborBlockChange(state, block, pos);
        checkRedstone();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        checkRedstone();
    }

    private void checkRedstone() {
        if (Game.isClient(world))
            return;
        boolean p = PowerPlugin.isBlockBeingPowered(world, getPos());
        if (powered != p) {
            powered = p;
            sendUpdateToClient();
        }
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(world))
            return;

        if (powered) {
            for (IFluidHandler handler : FluidTools.findNeighbors(tileCache, Predicates.alwaysTrue(), EnumFacing.VALUES)) {
                FluidUtil.tryFluidTransfer(handler, HANDLER, FluidTools.BUCKET_VOLUME * 4, true);
            }
        }
    }

    @Override
    public @Nullable EnumGui getGui() {
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        return data;
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        boolean p = data.readBoolean();
        if (powered != p) {
            powered = p;
            markBlockForUpdate();
        }
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return base.withProperty(BlockAdminSteamProducer.POWERED, powered);
    }
}
