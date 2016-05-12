/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import buildcraft.api.tools.IToolWrench;
import cofh.api.energy.IEnergyConnection;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.rf.RedstoneFluxPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileEngine extends TileMachineBase implements IEnergyConnection {
    public float currentOutput = 0;
    public int energy;
    private ForgeDirection direction = ForgeDirection.UP;
    private float pistonProgress = 0;
    private int pistonStage;
    private boolean powered;
    private boolean isActive;
    private boolean needsInit = true;
    //    public int outputDebug, genDebug, cycleTick;
    private EnergyStage energyStage = EnergyStage.BLUE;

    public TileEngine() {
    }

    public float getCurrentOutput() {
        return currentOutput;
    }

    public int getEnergy() {
        return energy;
    }

    protected void playSoundIn() {
    }

    protected void playSoundOut() {
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(worldObj)) {
            if (pistonStage != 0) {
                pistonProgress += getPistonSpeed();
                if (pistonProgress > 0.5 && pistonStage == 1) {
                    pistonStage = 2;
                    playSoundOut();
                } else if (pistonProgress >= 1) {
                    pistonStage = 0;
                    pistonProgress = 0;
                    playSoundIn();
                }
            } else if (this.isActive)
                pistonStage = 1;

            return;
        }

        if (needsInit) {
            needsInit = false;
            checkPower();
        }

        if (!powered)
            if (energy > 1)
                energy--;

        if (getEnergyStage() == EnergyStage.OVERHEAT)
            overheat();
        else if (pistonStage != 0) {
            pistonProgress += getPistonSpeed();

            if (pistonProgress > 0.5 && pistonStage == 1) {
                pistonStage = 2;

                TileEntity tile = tileCache.getTileOnSide(direction);

                if (RedstoneFluxPlugin.canTileReceivePower(tile, direction.getOpposite())) {
                    RedstoneFluxPlugin.pushToTile(tile, direction.getOpposite(), extractEnergy());
                }
            } else if (pistonProgress >= 1) {
                pistonProgress = 0;
                pistonStage = 0;
//                ChatPlugin.sendLocalizedChatToAllFromServer(worldObj, "Ticks=%d, Gen=%d, Out=%d", clock - cycleTick, genDebug, outputDebug);
//                outputDebug = 0;
//                genDebug = 0;
//                cycleTick = clock;
            }
        } else if (powered) {
            TileEntity tile = tileCache.getTileOnSide(direction);

            if (RedstoneFluxPlugin.canTileReceivePower(tile, direction.getOpposite()))
                if (energy > 0) {
                    pistonStage = 1;
                    setActive(true);
                } else
                    setActive(false);
            else
                setActive(false);
        } else
            setActive(false);

        burn();
    }

    protected void overheat() {
        subtractEnergy(50);
    }

    protected abstract void burn();

    public boolean isActive() {
        return isActive;
    }

    private void setActive(boolean isActive) {
        if (this.isActive != isActive) {
            this.isActive = isActive;
            sendUpdateToClient();
        }
    }

    public double getPistonSpeed() {
        if (Game.isHost(worldObj))
            return Math.max(0.16 * getEnergyLevel(), 0.01);
        switch (getEnergyStage()) {
            case BLUE:
                return 0.01;
            case GREEN:
                return 0.02;
            case YELLOW:
                return 0.04;
            case ORANGE:
                return 0.08;
            case RED:
                return 0.16;
            default:
                return 0.0;
        }
    }

    @Override
    public boolean blockActivated(EntityPlayer player, int side) {
        ItemStack current = player.inventory.getCurrentItem();
        if (current != null)
            if (current.getItem() instanceof IToolWrench) {
                IToolWrench wrench = (IToolWrench) current.getItem();
                if (wrench.canWrench(player, xCoord, yCoord, zCoord))
                    if (Game.isHost(worldObj) && getEnergyStage() == EnergyStage.OVERHEAT) {
                        resetEnergyStage();
                        wrench.wrenchUsed(player, xCoord, yCoord, zCoord);
                        return true;
                    }
            }
        return super.blockActivated(player, side);
    }

    @Override
    public boolean rotateBlock(ForgeDirection axis) {
        if (getEnergyStage() == EnergyStage.OVERHEAT)
            return false;
        return switchOrientation();
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        switchOrientation();
        checkPower();
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        if (Game.isNotHost(getWorld()))
            return;
        checkPower();
    }

    private void checkPower() {
        boolean p = PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord, zCoord);
        if (powered != p) {
            powered = p;
            sendUpdateToClient();
        }
    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public boolean canConnectRedstone(int dir) {
        return true;
    }

    public boolean switchOrientation() {
        for (int i = direction.ordinal() + 1; i < direction.ordinal() + 6; ++i) {
            ForgeDirection dir = ForgeDirection.getOrientation(i % 6);

            TileEntity tile = tileCache.getTileOnSide(dir);

            if (RedstoneFluxPlugin.canTileReceivePower(tile, dir.getOpposite())) {
                direction = dir;
                notifyBlocksOfNeighborChange();
                sendUpdateToClient();
                if (Game.isNotHost(worldObj))
                    markBlockForUpdate();
                return true;
            }
        }
        return false;
    }

    public ForgeDirection getOrientation() {
        return direction;
    }

    @Override
    public boolean isSideSolid(ForgeDirection side) {
        return direction.getOpposite() == side;
    }

    public double getEnergyLevel() {
        return (double) energy / (double) maxEnergy();
    }

    protected EnergyStage computeEnergyStage() {
        double energyLevel = getEnergyLevel();
        if (energyLevel < 0.2)
            return EnergyStage.BLUE;
        else if (energyLevel < 0.4)
            return EnergyStage.GREEN;
        else if (energyLevel < 0.6)
            return EnergyStage.YELLOW;
        else if (energyLevel < 0.8)
            return EnergyStage.ORANGE;
        else if (energyLevel < 1)
            return EnergyStage.RED;
        else
            return EnergyStage.OVERHEAT;
    }

    public final EnergyStage getEnergyStage() {
        if (Game.isHost(worldObj)) {
            if (energyStage == EnergyStage.OVERHEAT)
                return energyStage;
            EnergyStage newStage = computeEnergyStage();

            if (energyStage != newStage) {
                energyStage = newStage;
                sendUpdateToClient();
            }
        }

        return energyStage;
    }

    public final void resetEnergyStage() {
        EnergyStage newStage = computeEnergyStage();

        if (energyStage != newStage) {
            energyStage = newStage;
            sendUpdateToClient();
        }
    }

    public void addEnergy(int addition) {
        energy += addition;
//        genDebug += addition;

        if (energy > maxEnergy())
            energy = maxEnergy();
        if (energy < 0)
            energy = 0;
    }

    public void subtractEnergy(int subtraction) {
        energy -= subtraction;

        if (energy > maxEnergy())
            energy = maxEnergy();
        if (energy < 0)
            energy = 0;
    }

    public int extractEnergy() {
        int amount = maxEnergyExtracted();
        if (energy >= amount) {
            energy -= amount;
            return amount;
        }
        int returnValue = energy;
        energy = 0;
        return returnValue;
    }

    //    public int extractEnergy(int min, int max, boolean doExtract) {
//        if (energy < min)
//            return 0;
//
//        int actualMax;
//
//        int engineMax = maxEnergyExtracted();// + extraEnergy * 0.5;
//        if (max > engineMax)
//            actualMax = engineMax;
//        else
//            actualMax = max;
//
//        int extracted;
//
//        if (energy >= actualMax) {
//            extracted = actualMax;
//            if (doExtract)
//                energy -= actualMax; //extraEnergy -= Math.min(actualMax, extraEnergy);
//        } else {
//            extracted = energy;
//            if (doExtract)
//                energy = 0; //extraEnergy = 0;
//        }
//
//        return extracted;
//    }
    public float getProgress() {
        return pistonProgress;
    }

    public abstract int maxEnergy();

    public abstract int maxEnergyExtracted();

    public abstract int maxEnergyReceived();

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setByte("direction", (byte) direction.ordinal());
        data.setBoolean("powered", powered);
        data.setInteger("energyRF", energy);
        data.setFloat("currentOutput", currentOutput);
        data.setByte("energyStage", (byte) energyStage.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        direction = ForgeDirection.getOrientation(data.getByte("direction"));
        powered = data.getBoolean("powered");
        energy = data.getInteger("energyRF");
        currentOutput = data.getFloat("currentOutput");
        energyStage = EnergyStage.fromOrdinal(data.getByte("energyStage"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(direction.ordinal());
        data.writeByte(getEnergyStage().ordinal());
        data.writeBoolean(isActive);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        direction = ForgeDirection.getOrientation(data.readByte());
        energyStage = EnergyStage.fromOrdinal(data.readByte());
        isActive = data.readBoolean();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return from == direction;
    }

    public enum EnergyStage {

        BLUE, GREEN, YELLOW, ORANGE, RED, OVERHEAT;
        public static final EnergyStage[] VALUES = values();

        public static EnergyStage fromOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal >= VALUES.length)
                return BLUE;
            return VALUES[ordinal];
        }

    }
}
