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
import mods.railcraft.api.fuel.FluidFuelManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.BoilerFuelTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.steam.FluidFuelProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileBoilerFireboxFluid extends TileBoilerFirebox {

    private static final int TANK_FUEL = 2;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    protected final BoilerFuelTank tankFuel = new BoilerFuelTank(FluidTools.BUCKET_VOLUME * 16, this);

    public TileBoilerFireboxFluid() {
        super(2);
        tankManager.add(tankFuel);
        boiler.setFuelProvider(new FluidFuelProvider(tankFuel));
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
    protected void process() {
    }

    @Override
    protected void processBuckets() {
        super.processBuckets();
        FluidTools.drainContainers(tankFuel, inventory, SLOT_LIQUID_INPUT, SLOT_LIQUID_OUTPUT);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == SLOT_LIQUID_OUTPUT;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!isStructureValid())
            return false;
        switch (slot) {
            case SLOT_LIQUID_INPUT:
                FluidStack fluid = FluidItemHelper.getFluidStackInContainer(stack);
                if (fluid == null)
                    return false;
                if (Fluids.WATER.is(fluid) || FluidFuelManager.getFuelValue(fluid) > 0)
                    return true;
        }
        return false;
    }

    @Override
    public boolean needsFuel() {
        TileBoilerFireboxFluid mBlock = (TileBoilerFireboxFluid) getMasterBlock();
        return mBlock != null && mBlock.tankFuel.getFluidAmount() < (mBlock.tankFuel.getCapacity() / 4);
    }

    @Override
    public EnumGui getGui() {
        return EnumGui.BOILER_LIQUID;
    }
}
