/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.FluidTools.ProcessType;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * Created by CovertJaguar on 1/28/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WaterTankLogic extends InventoryLogic {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_PROCESS = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int TANK_CAPACITY = FluidTools.BUCKET_VOLUME * 16;
    private final StandardTank tank;
    private FluidTools.ProcessState processState = FluidTools.ProcessState.RESET;

    public WaterTankLogic(Adapter adapter) {
        super(adapter, 3);
        tank = new FilteredTank(TANK_CAPACITY, adapter.tile().orElse(null)).setFilterFluid(Fluids.WATER);
        addSubLogic(new TankLogic(adapter).addTank(tank));
    }

    @Override
    protected void updateServer() {
        super.updateServer();

        if (clock(FluidTools.BUCKET_FILL_TIME))
            processState = FluidTools.processContainer(this, tank, ProcessType.DRAIN_THEN_FILL, processState);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        if (slot == SLOT_INPUT) {
            return FluidItemHelper.isRoomInContainer(stack, Fluids.WATER.get()) || FluidItemHelper.containsFluid(stack, Fluids.WATER.get());
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
    public boolean interact(EntityPlayer player, EnumHand hand) {
        return FluidTools.interactWithFluidHandler(player, hand, tank) || super.interact(player, hand);
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return EnumGui.TANK_WATER;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTPlugin.writeEnumOrdinal(data, "processState", processState);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        processState = NBTPlugin.readEnumOrdinal(data, "processState", FluidTools.ProcessState.values(), FluidTools.ProcessState.RESET);
    }
}
