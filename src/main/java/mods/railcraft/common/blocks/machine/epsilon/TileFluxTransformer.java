/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.epsilon;

import cofh.api.energy.IEnergyReceiver;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: migrate to new charge API
public class TileFluxTransformer extends TileMultiBlock implements IEnergyReceiver {

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

//    private final ChargeHandler chargeHandler = new ChargeHandler(this, IChargeBlock.ConnectType.BLOCK, 0.25);

    public TileFluxTransformer() {
        super(patterns);
    }

    public static void placeFluxTransformer(World world, BlockPos pos) {
        MultiBlockPattern pattern = TileFluxTransformer.patterns.get(0);
        Map<Character, IBlockState> blockMapping = new HashMap<Character, IBlockState>();
        blockMapping.put('B', EnumMachineEpsilon.FLUX_TRANSFORMER.getDefaultState());
        pattern.placeStructure(world, pos, blockMapping);
    }

//    @Override
//    public void update() {
//        super.update();
//        if (Game.isClient(getWorld()))
//            return;
//        chargeHandler.tick();
//    }

    @Override
    public EnumMachineEpsilon getMachineType() {
        return EnumMachineEpsilon.FLUX_TRANSFORMER;
    }

//    @Override
//    public ChargeHandler getChargeHandler() {
//        return chargeHandler;
//    }

    //    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
//        if (!isStructureValid())
//            return 0;
//        double chargeDifference = chargeHandler.getCapacity() - chargeHandler.getCharge();
//        if (chargeDifference > 0.0) {
//            if (!simulate)
//                chargeHandler.addCharge((maxReceive / EU_RF_RATIO) * EFFICIENCY);
//            return maxReceive;
//        }
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

//    @Override
//    public void readFromNBT(NBTTagCompound data) {
//        super.readFromNBT(data);
//        chargeHandler.readFromNBT(data);
//    }
//
//    @Override
//    public NBTTagCompound writeToNBT(NBTTagCompound data) {
//        super.writeToNBT(data);
//        chargeHandler.writeToNBT(data);
//        return data;
//    }

}
