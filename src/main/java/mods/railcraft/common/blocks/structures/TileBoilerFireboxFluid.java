/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mods.railcraft.api.fuel.FluidFuelManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.BoilerFuelTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.steam.FluidFuelProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileBoilerFireboxFluid extends TileBoilerFirebox {

    private static final int TANK_FUEL = 2;
    private final BoilerFuelTank tankFuel = new BoilerFuelTank(FluidTools.BUCKET_VOLUME * 16, this);

    public TileBoilerFireboxFluid() {
        getLogic(StructureLogic.class)
                .ifPresent(structureLogic -> {
                    structureLogic.getKernel(FluidLogic.class)
                            .map(FluidLogic::getTankManager).ifPresent(tm -> tm.add(tankFuel));
//                    structureLogic.addSubLogic(new StorageTankLogic(Logic.Adapter.of(this), FluidTools.BUCKET_VOLUME * 16,
//                            fluid -> fluid != null
//                                    && !Fluids.WATER.is(fluid)
//                                    && FluidFuelManager.getFuelValue(fluid) > 0
//                                    && super.matchesFilter(fluid)));
                    structureLogic.getKernel(BoilerLogic.class)
                            .ifPresent(boilerLogic -> {
                                boilerLogic.setFuelProvider(new FluidFuelProvider(tankFuel));
                                boilerLogic.addLogic(new InventoryLogic(Logic.Adapter.of(this), 3) {
                                    @Override
                                    public IItemHandlerModifiable getItemHandler(@Nullable EnumFacing side) {
                                        return new InvWrapper(this) {
                                            @Nonnull
                                            @Override
                                            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                                                if (slot != SLOT_OUTPUT_FLUID)
                                                    return ItemStack.EMPTY;
                                                return super.extractItem(slot, amount, simulate);
                                            }
                                        };
                                    }

                                    @Override
                                    public boolean isItemValidForSlot(int slot, ItemStack stack) {
                                        if (!super.isItemValidForSlot(slot, stack))
                                            return false;
                                        if (slot == SLOT_INPUT_FLUID) {
                                            FluidStack fluid = FluidItemHelper.getFluidStackInContainer(stack);
                                            if (fluid == null)
                                                return false;
                                            return Fluids.WATER.is(fluid) || FluidFuelManager.getFuelValue(fluid) > 0;
                                        }
                                        return false;
                                    }
                                });
                            });
                });
    }

    public static void placeFluidBoiler(World world, BlockPos pos, int width, int height, boolean highPressure, int water, FluidStack fuel) {
        for (StructurePattern pattern : TileBoiler.patterns) {
            if (pattern.getPatternHeight() - 3 == height && pattern.getPatternWidthX() - 2 == width) {
                Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
                blockMapping.put('F', RailcraftBlocks.BOILER_FIREBOX_FLUID.getDefaultState());
                blockMapping.put('H', highPressure ? RailcraftBlocks.BOILER_TANK_PRESSURE_HIGH.getDefaultState() : RailcraftBlocks.BOILER_TANK_PRESSURE_LOW.getDefaultState());
                Optional<TileLogic> tile = pattern.placeStructure(world, pos, blockMapping);
                // FIXME
//                if (tile instanceof TileBoilerFireboxFluid) {
//                    TileBoilerFireboxFluid master = (TileBoilerFireboxFluid) tile;
//                    master.tankWater.setFluid(Fluids.WATER.get(water));
//                    master.tankFuel.setFluid(fuel);
//                }
                return;
            }
        }
    }

    @Override
    public EnumGui getGui() {
        return EnumGui.BOILER_FLUID;
    }
}
