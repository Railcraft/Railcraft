/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.single;

import cofh.redstoneflux.impl.EnergyStorage;
import cofh.redstoneflux.api.IEnergyConnection;
import mods.railcraft.common.blocks.ISmartTile;
import mods.railcraft.common.blocks.RailcraftTickingTileEntity;
import mods.railcraft.common.blocks.machine.interfaces.ITileNonSolid;
import mods.railcraft.common.blocks.machine.interfaces.ITileRotate;
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.rf.RedstoneFluxPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileEngine extends RailcraftTickingTileEntity implements IEnergyConnection, ITileRotate, ITileNonSolid, ISmartTile {

    public float currentOutput;
    public int energy;
    private EnumFacing direction = EnumFacing.UP;
    private float pistonProgress = 0.25F;
    private int pistonStage;
    private boolean powered;
    private boolean isActive;
    private boolean needsInit = true;
    //    public int outputDebug, genDebug, cycleTick;
    private EnergyStage energyStage = EnergyStage.BLUE;
    public final RFEnergyIndicator rfIndicator = new RFEnergyIndicator(new EnergyStorage(maxEnergy(), maxEnergyReceived(), maxEnergyExtracted()) {
        @Override
        public int getEnergyStored() {
            return energy;
        }
    });

    protected TileEngine() {
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
    public void update() {
        super.update();
        if (Game.isClient(world)) {
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
            } else if (isActive)
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
//                ChatPlugin.sendLocalizedChatToAllFromServer(world, "Ticks=%d, Gen=%d, Out=%d", clock - cycleTick, genDebug, outputDebug);
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
        if (Game.isHost(world))
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
    public boolean rotateBlock(EnumFacing axis) {
        if (getEnergyStage() == EnergyStage.OVERHEAT) {
            resetEnergyStage();
            return true;
        }
        return switchOrientation();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        switchOrientation();
        checkPower();
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block, BlockPos pos) {
        super.onNeighborBlockChange(state, block, pos);
        if (Game.isClient(getWorld()))
            return;
        checkPower();
    }

    private void checkPower() {
        boolean p = PowerPlugin.isBlockBeingPowered(world, getPos());
        if (powered != p) {
            powered = p;
            sendUpdateToClient();
        }
    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public boolean canConnectRedstone(@Nullable EnumFacing dir) {
        return true;
    }

    public boolean switchOrientation() {
        for (int i = direction.ordinal() + 1; i < direction.ordinal() + 6; ++i) {
            EnumFacing dir = EnumFacing.getFront(i % 6);

            TileEntity tile = tileCache.getTileOnSide(dir);

            if (RedstoneFluxPlugin.canTileReceivePower(tile, dir.getOpposite())) {
                direction = dir;
                notifyBlocksOfNeighborChange();
                sendUpdateToClient();
                if (Game.isClient(world))
                    markBlockForUpdate();
                return true;
            }
        }
        return false;
    }

    public EnumFacing getOrientation() {
        return direction;
    }

    @Override
    public BlockFaceShape getShape(EnumFacing side) {
        return direction.getOpposite() == side ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
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
        if (Game.isHost(world)) {
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
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setByte("direction", (byte) direction.ordinal());
        data.setBoolean("powered", powered);
        data.setInteger("energyRF", energy);
        data.setFloat("currentOutput", currentOutput);
        data.setByte("energyStage", (byte) energyStage.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        direction = EnumFacing.getFront(data.getByte("direction"));
        powered = data.getBoolean("powered");
        energy = data.getInteger("energyRF");
        currentOutput = data.getFloat("currentOutput");
        energyStage = EnergyStage.fromOrdinal(data.getByte("energyStage"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(direction.ordinal());
        data.writeByte(getEnergyStage().ordinal());
        data.writeBoolean(isActive);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        direction = EnumFacing.getFront(data.readByte());
        energyStage = EnergyStage.fromOrdinal(data.readByte());
        isActive = data.readBoolean();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return from == direction;
    }

    @Override
    public EnumFacing getFacing() {
        return direction;
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
