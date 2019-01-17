/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.blocks.interfaces.ITileTank;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.entity.RailcraftDamageSource;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.RailcraftSoundEvents;
import mods.railcraft.common.util.sounds.SoundHelper;
import mods.railcraft.common.util.steam.ISteamUser;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class TileSteamTrap extends TileMachineBase implements ISteamUser, ITileRotate, ITileTank {

    private static final byte JET_TIME = 40;
    private static final byte DAMAGE = 8;
    private static final double RANGE = 3.5;
    protected EnumFacing direction = EnumFacing.NORTH;
    protected boolean powered;
    private byte jet;
    private final TankManager tankManager = new TankManager();
    private final FilteredTank tank = new FilteredTank(FluidTools.BUCKET_VOLUME * 32, this).setFilterFluid(Fluids.STEAM);

    protected TileSteamTrap() {
        tankManager.add(tank);
    }

    //TODO: Fix
    @Override
    public EquipmentVariant getMachineType() {
        return null;
    }

    @Override
    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getTankManager());
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        super.update();
        if (jet > 0) {
            jet--;
            if (jet == 0)
                sendUpdateToClient();
        }
        if (Game.isClient(world)) {
            if (isJetting()) {
                double speedFactor = 0.2;
                for (int i = 0; i < 10; i++) {
                    ClientEffects.INSTANCE.steamJetEffect(world, this, new Vec3d(direction.getDirectionVec()).scale(speedFactor));
                }
            }
            return;
        }
        triggerCheck();
        if (isJetting())
            for (EntityLivingBase entity : getEntitiesInSteamArea()) {
                entity.attackEntityFrom(RailcraftDamageSource.STEAM, DAMAGE);
            }
    }

    //TODO: test, can we draw this?
    public List<EntityLivingBase> getEntitiesInSteamArea() {
        Vec3d jetVector = new Vec3d(direction.getDirectionVec()).scale(RANGE).add(0.5D, 0.5D, 0.5D);
        AxisAlignedBB area = AABBFactory.start().box().expandToCoordinate(jetVector).offset(getPos()).build();
        return world.getEntitiesWithinAABB(EntityLivingBase.class, area);
    }

    protected abstract void triggerCheck();

    protected void jet() {
        if (!canJet()) return;
        jet = JET_TIME;
        tank.setFluid(null);
        SoundHelper.playSound(world, null, getPos(), RailcraftSoundEvents.MECHANICAL_STEAM_HISS, SoundCategory.BLOCKS, 1, (float) (1 + MiscTools.RANDOM.nextGaussian() * 0.1));
        sendUpdateToClient();
    }

    public boolean isJetting() {
        return jet > 0;
    }

    public boolean canJet() {
        return !isJetting() && tank.isFull();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        if (entityLiving != null)
            direction = MiscTools.getSideFacingPlayer(getPos(), entityLiving);
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block, BlockPos pos) {
        super.onNeighborBlockChange(state, block, pos);
        powered = PowerPlugin.isBlockBeingPowered(world, getPos());
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (direction == axis)
            direction = axis.getOpposite();
        else
            direction = axis;
        markBlockForUpdate();
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("direction", (byte) direction.ordinal());
        data.setBoolean("powered", powered);
        tankManager.writeTanksToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        direction = EnumFacing.byIndex(data.getByte("direction"));
        powered = data.getBoolean("powered");
        tankManager.readTanksFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(jet);
        data.writeByte(direction.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        jet = data.readByte();
        direction = EnumFacing.byIndex(data.readByte());
        markBlockForUpdate();
    }

    @Override
    public EnumFacing getFacing() {
        return direction;
    }

    @Override
    public void setFacing(EnumFacing facing) {
        this.direction = facing;
    }
}
