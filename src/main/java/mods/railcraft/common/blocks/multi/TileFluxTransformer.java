/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mods.railcraft.api.charge.IBatteryTile;
import mods.railcraft.api.charge.IBlockBattery;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.charge.ChargeBattery;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.gui.EnumGui;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class TileFluxTransformer extends TileMultiBlock<TileFluxTransformer, TileFluxTransformer, TileFluxTransformer> implements IEnergyStorage, IBatteryTile {

    public static final double EU_RF_RATIO = 4;
    public static final double EFFICIENCY = 0.8F;
    private static final List<MultiBlockPattern> patterns = new ArrayList<>();

    private final ChargeBattery battery = new ChargeBattery(1024, 512, EFFICIENCY);

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

    @Override
    protected Class<TileFluxTransformer> defineSelfClass() {
        return TileFluxTransformer.class;
    }

    @Override
    protected Class<TileFluxTransformer> defineMasterClass() {
        return TileFluxTransformer.class;
    }

    @Override
    protected Class<TileFluxTransformer> defineLeastCommonClass() {
        return TileFluxTransformer.class;
    }

    @Override
    public IBlockBattery getBattery() {
        return getMasterBattery();
    }

    @Nullable
    ChargeBattery getMasterBattery() {
        if (isStructureValid()) {
            return getMasterBlock().battery;
        }
        return null;
    }

    @Override
    protected void setWorldCreate(World worldIn) {
        setWorld(worldIn);
    }

    @NotNull
    @Override
    public EnumGui getGui() {
        throw new UnsupportedOperationException("No GUI");
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        return false;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!isStructureValid())
            return 0;
        ChargeBattery battery = getMasterBattery();
        if (battery == null)
            return 0;
        double chargeDifference = battery.getCapacity() - battery.getCharge();
        if (chargeDifference > 0.0) {
            double chargeTo = (maxReceive / EU_RF_RATIO) * EFFICIENCY;
            final double leftOver;
            if (simulate) {
                if (chargeTo + battery.getCharge() > battery.getCapacity()) {
                    leftOver = chargeTo + battery.getCharge() - battery.getCapacity();
                } else {
                    leftOver = 0;
                }
            } else {
                leftOver = battery.addCharge(chargeTo);
            }

            return MathHelper.floor(leftOver / EFFICIENCY * EU_RF_RATIO);
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

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    protected void onMasterChanged() {
        super.onMasterChanged();
        if (isStructureValid()) {
            ChargeManager.getDimension(world).getNode(pos).loadBattery();
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
        ChargeManager.getDimension(world).getNode(pos).unloadBattery();
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        battery.readFromNBT(data);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        battery.writeToNBT(data);
        return data;
    }
}
