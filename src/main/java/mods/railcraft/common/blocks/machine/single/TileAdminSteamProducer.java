/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.single;

import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.io.IOException;

public class TileAdminSteamProducer extends TileMachineBase {

    private final TankManager tankManager = new TankManager();
    private boolean powered;

    public TileAdminSteamProducer() {
        StandardTank tankSteam = new StandardTank(FluidTools.BUCKET_VOLUME * 4) {
            @Override
            public int fillInternal(FluidStack resource, boolean doFill) {
                return 0;
            }

            @Nullable
            @Override
            public FluidStack drainInternal(int maxDrain, boolean doDrain) {
                if (!powered)
                    return null;
                return Fluids.STEAM.get(maxDrain);
            }

            @Nullable
            @Override
            public FluidStack drainInternal(FluidStack resource, boolean doDrain) {
                if (!powered)
                    return null;
                if (Fluids.STEAM.is(resource))
                    return resource.copy();
                return null;
            }
        };
        tankSteam.setCanFill(false);
        tankManager.add(tankSteam);
    }

    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) getTankManager();
        return super.getCapability(capability, facing);
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        checkRedstone();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        checkRedstone();
    }

    private void checkRedstone() {
        if (Game.isClient(getWorld()))
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
        if (Game.isClient(worldObj))
            return;

        if (powered)
            tankManager.push(tileCache, Predicates.alwaysTrue(), EnumFacing.VALUES, 0, FluidTools.BUCKET_VOLUME);
    }

    @Override
    public EnumMachineEpsilon getMachineType() {
        return EnumMachineEpsilon.ADMIN_STEAM_PRODUCER;
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

}
