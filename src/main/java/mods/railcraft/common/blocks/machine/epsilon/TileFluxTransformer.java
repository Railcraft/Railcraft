package mods.railcraft.common.blocks.machine.epsilon;

import cofh.api.energy.IEnergyHandler;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mods.railcraft.common.blocks.RailcraftBlocks;
import net.minecraft.world.World;

public class TileFluxTransformer extends TileMultiBlock implements IElectricGrid, IEnergyHandler {

    public static void placeFluxTransformer(World world, int x, int y, int z) {
        for (MultiBlockPattern pattern : TileFluxTransformer.patterns) {
            Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
            blockMapping.put('B', EnumMachineEpsilon.FLUX_TRANSFORMER.ordinal());
            pattern.placeStructure(world, x, y, z, RailcraftBlocks.getBlockMachineEpsilon(), blockMapping);
            return;
        }
    }

    public static final double EU_RF_RATIO = 4;
    public static final double EFFICIENCY = 0.8F;
    private static final List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.BLOCK, 0.25);

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

    public TileFluxTransformer() {
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
        return EnumMachineEpsilon.FLUX_TRANSFORMER;
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
        if (!isStructureValid())
            return 0;
        double chargeDifference = chargeHandler.getCapacity() - chargeHandler.getCharge();
        if (chargeDifference > 0.0) {
            if (!simulate)
                chargeHandler.addCharge((maxReceive / EU_RF_RATIO) * EFFICIENCY);
            return maxReceive;
        }
        return 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
    }

}
