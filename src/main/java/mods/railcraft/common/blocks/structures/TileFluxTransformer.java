/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IBattery;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.ChargeComparatorLogic;
import mods.railcraft.common.blocks.logic.ChargeSourceLogic;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class TileFluxTransformer extends TileLogic implements IEnergyStorage {

    private static final List<StructurePattern> patterns = new ArrayList<>();

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
        patterns.add(new StructurePattern(map));
    }

    public TileFluxTransformer() {
        setLogic(new StructureLogic("flux", this, patterns, new ChargeSourceLogic(Logic.Adapter.of(this), Charge.distribution))
                .addSubLogic(new ChargeComparatorLogic(Logic.Adapter.of(this), Charge.distribution)));
    }

    public static void placeFluxTransformer(World world, BlockPos pos) {
        StructurePattern pattern = TileFluxTransformer.patterns.get(0);
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        blockMapping.put('B', RailcraftBlocks.FLUX_TRANSFORMER.getDefaultState());
        pattern.placeStructure(world, pos, blockMapping);
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return getLogic(StructureLogic.class).map(l -> l.getState() == StructureLogic.StructureState.VALID).orElse(false)
                ? base.withProperty(BlockFluxTransformer.ICON, 1)
                : base.withProperty(BlockFluxTransformer.ICON, 0);
    }

    @Override
    protected void setWorldCreate(World worldIn) {
        setWorld(worldIn);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return getLogic(ChargeSourceLogic.class)
                .map(ChargeSourceLogic::getBattery)
                .filter(IBattery::needsCharging)
                .map(battery -> {
                    if (!simulate)
                        battery.addCharge(maxReceive * RailcraftConstants.FE_EU_RATIO);
                    return maxReceive;
                }).orElse(0);
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
}
