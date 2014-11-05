package mods.railcraft.common.blocks.machine.epsilon;

import cofh.api.energy.IEnergyHandler;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class TileFluxFeeder extends TileMultiBlock implements IElectricGrid, IEnergyHandler {

    public static void placeFluxCharger() {

    }

    public static final int EU_RF_RATIO = 4;
    private static final List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.BLOCK, 1);

    static {
        char[][][] map = {
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'*', 'O', 'O', '*'},},
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'*', 'O', 'O', '*'}
                },
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'*', 'O', 'O', '*'}
                },
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'*', 'O', 'O', '*'},},};
        patterns.add(new MultiBlockPattern(map));
    }

    public TileFluxFeeder() {
        super(patterns);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(getWorld()))
            return;
        chargeHandler.tick();
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineEpsilon.FLUX_FEEDER;
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        TileFluxFeeder masterBlock = (TileFluxFeeder) getMasterBlock();
        int receiveAmount = 0;
        if (masterBlock != null) {
            ChargeHandler chargeHandler = masterBlock.getChargeHandler();
            double chargeDifference = chargeHandler.getCapacity() - chargeHandler.getCharge();
            if (chargeDifference > maxReceive / EU_RF_RATIO) {
                receiveAmount = maxReceive / EU_RF_RATIO;
            } else {
                receiveAmount = MathHelper.floor_double(chargeDifference);
            }
            if (!simulate)
                chargeHandler.addCharge(receiveAmount);
        }
        return receiveAmount;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        TileFluxFeeder masterBlock = (TileFluxFeeder) getMasterBlock();
        if (masterBlock != null)
            return (int) masterBlock.getChargeHandler().getCharge() * EU_RF_RATIO;
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        TileFluxFeeder masterBlock = (TileFluxFeeder) getMasterBlock();
        if (masterBlock != null)
            return (int) masterBlock.getChargeHandler().getCapacity() * EU_RF_RATIO;
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (isMaster())
            chargeHandler.readFromNBT(data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (isStructureValid() && isMaster())
            chargeHandler.writeToNBT(data);
    }
}
