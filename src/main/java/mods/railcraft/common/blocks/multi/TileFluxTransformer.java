/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.IChargeBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class TileFluxTransformer extends TileMultiBlock implements IEnergyStorage {

    public static final double EU_RF_RATIO = 4;
    public static final double EFFICIENCY = 0.8F;
    private static final List<MultiBlockPattern> patterns = new ArrayList<>();

    private @Nullable IChargeBlock.ChargeBattery battery;

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
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        blockMapping.put('B', RailcraftBlocks.FLUX_TRANSFORMER.getDefaultState());
        pattern.placeStructure(world, pos, blockMapping);
    }

    @Nullable
    IChargeBlock.ChargeBattery getMasterBattery() {
        TileFluxTransformer mBlock = (TileFluxTransformer) getMasterBlock();
        if (mBlock != null) {
            return mBlock.getBattery();
        }
        return null;
    }

    private IChargeBlock.ChargeBattery getBattery() {
        if (battery == null) {
            battery = ChargeManager.instance.getNetwork(world).getTileBattery(pos, () -> new IChargeBlock.ChargeBattery(1024, 512, EFFICIENCY));
        }
        return battery;
    }

    @Override
    protected void setWorldCreate(World worldIn) {
        setWorld(worldIn);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        return false;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!isStructureValid())
            return 0;
        IChargeBlock.ChargeBattery battery = getMasterBattery();
        if (battery == null)
            return 0;
        double chargeDifference = battery.getCapacity() - battery.getCharge();
        if (chargeDifference > 0.0) {
            if (!simulate)
                battery.addCharge((maxReceive / EU_RF_RATIO) * EFFICIENCY);
            return maxReceive;
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    protected void onMasterChanged() {
        super.onMasterChanged();
        if (isStructureValid()) {
            ((IChargeBlock) getBlockType()).registerNode(getBlockState(), world, pos);
        } else {
            clean();
        }
    }

    @Override
    protected void onMasterReset() {
        super.onMasterReset();
        clean();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        clean();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        clean();
    }

    private void clean() {
        ChargeManager.instance.getNetwork(world).deregisterChargeNode(pos);
        battery = null;
    }
}
