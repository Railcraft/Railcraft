package mods.railcraft.common.blocks.machine.epsilon;

import cofh.api.energy.IEnergyReceiver;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileFluxTransformer extends TileMultiBlock implements IElectricGrid, IEnergyReceiver {

    public static final double EU_RF_RATIO = 4;
    public static final double EFFICIENCY = 0.8F;
    private static final List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();

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

    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.BLOCK, 0.25);

    public TileFluxTransformer() {
        super(patterns);
    }

    public static void placeFluxTransformer(World world, BlockPos pos) {
        for (MultiBlockPattern pattern : TileFluxTransformer.patterns) {
            Map<Character, IBlockState> blockMapping = new HashMap<Character, IBlockState>();
            blockMapping.put('B', EnumMachineEpsilon.FLUX_TRANSFORMER.getState());
            pattern.placeStructure(world, pos, blockMapping);
            return;
        }
    }

    @Override
    public void update() {
        super.update();
        if (Game.isNotHost(getWorld()))
            return;
        chargeHandler.tick();
    }

    @Override
    public EnumMachineEpsilon getMachineType() {
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
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
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
    public int getEnergyStored(EnumFacing from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
    }

    @Nonnull
    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
    }

}
