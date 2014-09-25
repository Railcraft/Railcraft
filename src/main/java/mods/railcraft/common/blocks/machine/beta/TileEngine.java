/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.tools.IToolWrench;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.gui.widgets.IIndicatorController;
import mods.railcraft.common.gui.widgets.IndicatorController;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileEngine extends TileMachineBase implements IPowerReceptor, IPipeConnection, IPowerEmitter {

    private ForgeDirection direction = ForgeDirection.UP;
    private float pistonProgress = 0;
    public float currentOutput = 0;
    private int pistonStage;
    private boolean powered;
    private boolean isActive;
    private boolean needsInit = true;
    public double energy;
    public double extraEnergy;
    private EnergyStage energyStage = EnergyStage.BLUE;
    private final PowerHandler provider = new PowerHandler(this, PowerHandler.Type.ENGINE);
    private final IIndicatorController energyIndicator = new EnergyIndicator();

    private class EnergyIndicator extends IndicatorController {

        @Override
        public void refreshToolTip() {
            tip.text = String.format("%.0f MJ", energy);
        }

        @Override
        public int getScaledLevel(int size) {
            double e = Math.min(energy, maxEnergy());
            return (int) (e * size / maxEnergy());
        }

    };

    public IIndicatorController getEnergyIndicator() {
        return energyIndicator;
    }

    public float getCurrentOutput() {
        return currentOutput;
    }

    public double getEnergy() {
        return energy;
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

    public TileEngine() {
        initEnergyProvider();
    }

    private void initEnergyProvider() {
        provider.configure(2, maxEnergyReceived(), 1, maxEnergy());
        provider.configurePowerPerdition(1, 100);
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

        provider.update();

        if (getEnergyStage() == EnergyStage.OVERHEAT)
            overheat();
        else if (pistonStage != 0) {
            pistonProgress += getPistonSpeed();

            if (pistonProgress > 0.5 && pistonStage == 1) {
                pistonStage = 2;

                TileEntity tile = WorldPlugin.getTileEntityOnSide(worldObj, xCoord, yCoord, zCoord, direction);

                if (EngineTools.isPoweredTile(tile, direction.getOpposite())) {
                    IPowerReceptor receptor = (IPowerReceptor) tile;

                    PowerReceiver recProv = receptor.getPowerReceiver(direction.getOpposite());

                    double extracted = extractEnergy(recProv.getMinEnergyReceived(), recProv.getMaxEnergyReceived(), true);

                    if (extracted > 0)
                        recProv.receiveEnergy(PowerHandler.Type.ENGINE, extracted, direction.getOpposite());
                }
            } else if (pistonProgress >= 1) {
                pistonProgress = 0;
                pistonStage = 0;
            }
        } else if (powered) {
            TileEntity tile = WorldPlugin.getTileEntityOnSide(worldObj, xCoord, yCoord, zCoord, direction);

            if (EngineTools.isPoweredTile(tile, direction.getOpposite())) {
                IPowerReceptor receptor = (IPowerReceptor) tile;

                PowerReceiver recProv = receptor.getPowerReceiver(direction.getOpposite());

                if (extractEnergy(recProv.getMinEnergyReceived(), recProv.getMaxEnergyReceived(), false) > 0) {
                    pistonStage = 1;
                    setActive(true);
                } else
                    setActive(false);
            } else
                setActive(false);

        } else
            setActive(false);

        burn();
    }

    protected void overheat() {
        subtractEnergy(5);
    }

    protected abstract void burn();

    private void setActive(boolean isActive) {
        if (this.isActive != isActive) {
            this.isActive = isActive;
            sendUpdateToClient();
        }
    }

    public boolean isActive() {
        return isActive;
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
        return switchOrientation();
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving) {
        super.onBlockPlacedBy(entityliving);
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
        for (int i = direction.ordinal() + 1; i <= direction.ordinal() + 6; ++i) {
            ForgeDirection dir = ForgeDirection.getOrientation(i % 6);

            TileEntity tile = WorldPlugin.getTileEntityOnSide(worldObj, xCoord, yCoord, zCoord, dir);

            if (EngineTools.isPoweredTile(tile, dir.getOpposite())) {
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

    @Override
    public PowerReceiver getPowerReceiver(ForgeDirection side) {
        return provider.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler workProvider) {
        if (Game.isNotHost(worldObj))
            return;
        double e = provider.useEnergy(1, maxEnergyReceived(), true) * 0.95;
        extraEnergy += e;
        addEnergy(e);
    }

    public double getEnergyLevel() {
        return energy / maxEnergy();
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

    public void addEnergy(double addition) {
        energy += addition;

        if (energy > maxEnergy())
            energy = maxEnergy();
    }

    public void subtractEnergy(double subtraction) {
        energy -= subtraction;
        if (energy < 0)
            energy = 0;
    }

    public double extractEnergy(double min, double max, boolean doExtract) {
        if (energy < min)
            return 0;

        double actualMax;

        double combinedMax = maxEnergyExtracted() + extraEnergy * 0.5;
        if (max > combinedMax)
            actualMax = combinedMax;
        else
            actualMax = max;

        double extracted;

        if (energy >= actualMax) {
            extracted = actualMax;
            if (doExtract) {
                energy -= actualMax;
                extraEnergy -= Math.min(actualMax, extraEnergy);
            }
        } else {
            extracted = energy;
            if (doExtract) {
                energy = 0;
                extraEnergy = 0;
            }
        }

        return extracted;
    }

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
        data.setFloat("energy", (float) energy);
        data.setFloat("currentOutput", currentOutput);
        data.setByte("energyStage", (byte) energyStage.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        direction = ForgeDirection.getOrientation(data.getByte("direction"));
        powered = data.getBoolean("powered");
        energy = data.getFloat("energy");
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
    public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
        if (type == PipeType.POWER)
            return ConnectOverride.DEFAULT;
        if (with == direction)
            return ConnectOverride.DISCONNECT;
        return ConnectOverride.DEFAULT;
    }

    @Override
    public boolean canEmitPowerFrom(ForgeDirection side) {
        return side == direction;
    }

}
