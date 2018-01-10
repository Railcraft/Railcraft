/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import cofh.api.energy.IEnergyReceiver;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.gui.EnumGui;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileFluxTransformer extends TileMultiBlock implements IEnergyReceiver {

    public static final double EU_RF_RATIO = 4;
    public static final double EFFICIENCY = 0.8F;
    private static final List<MultiBlockPattern> patterns = new ArrayList<>();

    private IChargeBlock.ChargeBattery battery;

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

    public static void placeFluxTransformer(World world, BlockPos pos) {
        MultiBlockPattern pattern = TileFluxTransformer.patterns.get(0);
        Map<Character, IBlockState> blockMapping = new HashMap<>();
//        blockMapping.put('B', EnumMachineEpsilon.FLUX_TRANSFORMER.getDefaultState()); TODO
        pattern.placeStructure(world, pos, blockMapping);
    }

    private IChargeBlock.ChargeBattery getBattery() {
        if (battery == null) {
            battery = ChargeManager.getNetwork(worldObj).getTileBattery(pos, () -> new IChargeBlock.ChargeBattery(1024, 512, .7));
        }
        return battery;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if (!isStructureValid())
            return 0;
        IChargeBlock.ChargeBattery battery = getBattery();
        double chargeDifference = battery.getCapacity() - battery.getCharge();
        if (chargeDifference > 0.0) {
            if (!simulate)
                battery.addCharge((maxReceive / EU_RF_RATIO) * EFFICIENCY);
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
    protected void setWorldCreate(World worldIn) {
        setWorldObj(worldIn);
    }

    @Nullable
    @Override
    public EnumGui getGui() {
        return null;
    }
}
