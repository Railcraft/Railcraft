/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import mods.railcraft.common.util.network.RailcraftDataInputStream;
import mods.railcraft.common.util.network.RailcraftDataOutputStream;
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
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class TileSteamTrap extends TileMachineBase implements ISteamUser {

    private static final byte JET_TIME = 40;
    private static final byte DAMAGE = 8;
    private static final double RANGE = 3.5;
    protected EnumFacing direction = EnumFacing.NORTH;
    protected boolean powered;
    private byte jet;
    private final TankManager tankManager = new TankManager();
    private final FilteredTank tank = new FilteredTank(FluidHelper.BUCKET_VOLUME * 32, Fluids.STEAM.get());

    protected TileSteamTrap() {
        tankManager.add(tank);
    }

    @Override
    public void update() {
        super.update();
        if (jet > 0) {
            jet--;
            if (jet == 0)
                sendUpdateToClient();
        }
        if (Game.isNotHost(worldObj)) {
            if (isJetting()) {
                double speedFactor = 0.2;
                for (int i = 0; i < 10; i++) {
                    EffectManager.instance.steamJetEffect(worldObj, this, new Vec3d(direction.getDirectionVec()).scale(speedFactor));
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

    //TODO: test
    public List<EntityLivingBase> getEntitiesInSteamArea() {
        Vec3d jetVector = new Vec3d(direction.getDirectionVec()).scale(RANGE).addVector(0.5D, 0.5D, 0.5D);
        AxisAlignedBB area = AABBFactory.start().box().expandToCoordinate(jetVector).offset(getPos()).build();
        return worldObj.getEntitiesWithinAABB(EntityLivingBase.class, area);
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        return tankManager.fill(0, resource, doFill);
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluid == null || Fluids.STEAM.is(fluid);
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing direction) {
        return tankManager.getTankInfo(direction);
    }

    protected abstract void triggerCheck();

    protected void jet() {
        if (!canJet()) return;
        jet = JET_TIME;
        tank.setFluid(null);
        SoundHelper.playSound(worldObj, null, getPos(), RailcraftSoundEvents.MECHANICAL_STEAM_HISS, SoundCategory.BLOCKS, 1, (float) (1 + MiscTools.RANDOM.nextGaussian() * 0.1));
        sendUpdateToClient();
    }

    public boolean isJetting() {
        return jet > 0;
    }

    public void onStopJetting() {
    }

    public boolean canJet() {
        return !isJetting() && tank.isFull();
    }

    @Override
    public void onBlockPlacedBy(@Nonnull IBlockState state, @Nonnull EntityLivingBase entityLiving, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        direction = MiscTools.getSideFacingPlayer(getPos(), entityLiving);
    }

    @Override
    public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull Block block) {
        super.onNeighborBlockChange(state, block);
        powered = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
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

    @Nonnull
    @Override
    public void writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("direction", (byte) direction.ordinal());
        data.setBoolean("powered", powered);
        tankManager.writeTanksToNBT(data);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);
        direction = EnumFacing.getFront(data.getByte("direction"));
        powered = data.getBoolean("powered");
        tankManager.readTanksFromNBT(data);
    }

    @Override
    public void writePacketData(@Nonnull RailcraftDataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(jet);
        data.writeByte(direction.ordinal());
    }

    @Override
    public void readPacketData(@Nonnull RailcraftDataInputStream data) throws IOException {
        super.readPacketData(data);
        jet = data.readByte();
        direction = EnumFacing.getFront(data.readByte());
        markBlockForUpdate();
    }
}
