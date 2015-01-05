/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.util.steam.ISteamUser;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class TileSteamTrap extends TileMachineBase implements IFluidHandler, ISteamUser {

    private static final byte JET_TIME = 40;
    private static final byte DAMAGE = 8;
    private static final double RANGE = 3.5;
    protected ForgeDirection direction = ForgeDirection.NORTH;
    protected boolean powered;
    private byte jet;
    private final TankManager tankManager = new TankManager();
    private final FilteredTank tank = new FilteredTank(FluidHelper.BUCKET_VOLUME * 32, Fluids.STEAM.get());

    public TileSteamTrap() {
        tankManager.add(tank);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (jet > 0) {
            jet--;
            if (jet == 0)
                sendUpdateToClient();
        }
        if (Game.isNotHost(worldObj)) {
            if (isJetting()) {
                double speedFactor = 0.2;
                for (int i = 0; i < 10; i++) {
                    EffectManager.instance.steamJetEffect(worldObj, this, direction.offsetX * speedFactor, direction.offsetY * speedFactor, direction.offsetZ * speedFactor);
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

    @SuppressWarnings("unchecked")
    public List<EntityLivingBase> getEntitiesInSteamArea() {
        AxisAlignedBB area = AxisAlignedBB.getBoundingBox(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D);
        MiscTools.addCoordToAABB(area, direction.offsetX * RANGE, direction.offsetY * RANGE, direction.offsetZ * RANGE);
        area.offset(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
        List<EntityLivingBase> entities = (List<EntityLivingBase>) worldObj.getEntitiesWithinAABB(EntityLivingBase.class, area);
        return entities;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return tankManager.fill(0, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid == null || Fluids.STEAM.is(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection direction) {
        return tankManager.getTankInfo(direction);
    }

    protected abstract void triggerCheck();

    protected void jet() {
        if (!canJet()) return;
        jet = JET_TIME;
        tank.setFluid(null);
        SoundHelper.playSound(worldObj, xCoord, yCoord, zCoord, SoundHelper.SOUND_STEAM_HISS, 1, (float) (1 + MiscTools.getRand().nextGaussian() * 0.1));
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
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        direction = MiscTools.getSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving);
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        powered = PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord, zCoord);
    }

    @Override
    public boolean rotateBlock(ForgeDirection axis) {
        if (direction == axis)
            direction = axis.getOpposite();
        else
            direction = axis;
        markBlockForUpdate();
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("direction", (byte) direction.ordinal());
        data.setBoolean("powered", powered);
        tankManager.writeTanksToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        direction = ForgeDirection.getOrientation(data.getByte("direction"));
        powered = data.getBoolean("powered");
        tankManager.readTanksFromNBT(data);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(jet);
        data.writeByte(direction.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        jet = data.readByte();
        direction = ForgeDirection.getOrientation(data.readByte());
        markBlockForUpdate();
    }

}
