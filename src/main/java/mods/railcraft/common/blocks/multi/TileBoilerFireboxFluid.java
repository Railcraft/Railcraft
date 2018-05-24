/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.api.fuel.FuelManager;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.BoilerFuelTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.steam.FluidFuelProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileBoilerFireboxFluid extends TileBoilerFirebox<TileBoilerFireboxFluid> {

    private static final int TANK_FUEL = 2;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    protected final BoilerFuelTank tankFuel = new BoilerFuelTank(FluidTools.BUCKET_VOLUME * 16, this);

    public TileBoilerFireboxFluid() {
        super(2);
        tankManager.add(tankFuel);
        boiler.setFuelProvider(new FluidFuelProvider(tankFuel));
    }

    public static void placeFluidBoiler(World world, BlockPos pos, int width, int height, boolean highPressure, int water, FluidStack fuel) {
        for (MultiBlockPattern pattern : TileBoiler.patterns) {
            if (pattern.getPatternHeight() - 3 == height && pattern.getPatternWidthX() - 2 == width) {
                Map<Character, IBlockState> blockMapping = new HashMap<>();
//              //TODO
//                blockMapping.put('F', EnumMachineBeta.BOILER_FIREBOX_FLUID.getDefaultState());
//                blockMapping.put('H', highPressure ? EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getDefaultState() : EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.getDefaultState());
                TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
                if (tile instanceof TileBoilerFireboxFluid) {
                    TileBoilerFireboxFluid master = (TileBoilerFireboxFluid) tile;
                    master.tankWater.setFluid(Fluids.WATER.get(water));
                    master.tankFuel.setFluid(fuel);
                }
                return;
            }
        }
    }

//    @Override
//    public EnumMachineBeta getMachineType() {
//        return EnumMachineBeta.BOILER_FIREBOX_FLUID;
//    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileBoilerFireboxFluid mBlock = getMasterBlock();
        if (mBlock != null) {
            GuiHandler.openGui(EnumGui.BOILER_LIQUID, player, world, mBlock.getPos());
            return true;
        }
        return false;
    }

    @Override
    protected void process() {
    }

    @Override
    protected void processBuckets() {
        super.processBuckets();
        //FIXME
//        FluidTools.drainContainers(this, inventory, SLOT_LIQUID_INPUT, SLOT_LIQUID_OUTPUT);
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
                Fluid fluid = FluidItemHelper.getFluidInContainer(stack);
                if (fluid == null)
                    return false;
                if (Fluids.WATER.is(fluid) || FuelManager.getBoilerFuelValue(fluid) > 0)
                    return true;
        }
        return false;
    }

    @Override
    public boolean needsFuel() {
        TileBoilerFireboxFluid mBlock = getMasterBlock();
        return mBlock != null && mBlock.tankFuel.getFluidAmount() < (mBlock.tankFuel.getCapacity() / 4);
    }

    @NotNull
    @Override
    @Nonnull
    public EnumGui getGui() {
        return EnumGui.BOILER_LIQUID;
    }
}
