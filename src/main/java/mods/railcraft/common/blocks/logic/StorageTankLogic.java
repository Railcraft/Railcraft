/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools.ProcessType;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.gui.EnumGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 1/28/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StorageTankLogic extends InventoryLogic {
    public static final int TANK_INDEX = 0;
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_PROCESS = 1;
    public static final int SLOT_OUTPUT = 2;
    private final FilteredTank tank;

    public StorageTankLogic(Adapter adapter, int capacity) {
        this(adapter, capacity, null);
    }

    public StorageTankLogic(Adapter adapter, int capacity, @Nullable Supplier<@Nullable Fluid> filter) {
        super(adapter, 3);
        tank = new FilteredTank(capacity, adapter.tile().orElse(null));
        if (filter != null)
            tank.setFilterFluid(filter);
        addLogic(new FluidLogic(adapter).addTank(tank));
        addLogic(new BucketProcessorLogic(adapter, SLOT_INPUT, ProcessType.FILL_THEN_DRAIN));
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        if (slot == SLOT_INPUT) {
            Fluid tankFluid = tank.getFluidType();
            if (tankFluid == null) {
                return FluidItemHelper.isContainer(stack);
            }
            return FluidItemHelper.isRoomInContainer(stack, tankFluid) || FluidItemHelper.containsFluid(stack, tankFluid);
        }
        return false;
    }

    @Override
    public IItemHandlerModifiable getItemHandler(@Nullable EnumFacing side) {
        return new InvWrapper(this) {
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == SLOT_OUTPUT)
                    return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return EnumGui.TANK;
    }
}
