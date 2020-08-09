/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.ICokeOvenCrafter;
import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;
import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

/**
 * Created by CovertJaguar on 12/27/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CokeOvenLogic extends SingleInputRecipeCrafterLogic<ICokeOvenCrafter.IRecipe> implements INeedsFuel {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_OUTPUT_FLUID = 2;
    public static final int SLOT_LIQUID_INPUT = 3;
    public static final int TANK_CAPACITY = 64 * FluidTools.BUCKET_VOLUME;
    private final StandardTank tank;
    private final InventoryMapper invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1).ignoreItemChecks();
    private int multiplier = 1;

    public CokeOvenLogic(Adapter adapter) {
        super(adapter, 4, SLOT_INPUT);
        tank = new StandardTank(TANK_CAPACITY, adapter.tile().orElse(null));
        addSubLogic(new FluidLogic(adapter).addTank(tank));
    }

    @Override
    protected Optional<ICokeOvenCrafter.IRecipe> getRecipe(ItemStack input) {
        return Crafters.cokeOven().getRecipe(input);
    }

    @Override
    protected boolean craftAndPush() {
        boolean crafted = false;
        for (int ii = 0; ii < multiplier; ii++) {
            crafted |= craftAndPushImp();
        }
        return crafted;
    }

    private boolean craftAndPushImp() {
        Objects.requireNonNull(recipe);
        ItemStack output = recipe.getOutput();
        FluidStack fluidOutput = recipe.getFluidOutput();
        if (invOutput.canFit(output)
                && (fluidOutput == null || tank.fill(fluidOutput, false) >= fluidOutput.amount)) {
            decrStackSize(SLOT_INPUT, 1);
            invOutput.addStack(output);
            tank.fill(fluidOutput, true);
            return true;
        }
        return false;
    }

    @Override
    protected void updateServer() {
        super.updateServer();

        ItemStack topSlot = getStackInSlot(SLOT_LIQUID_INPUT);
        if (!InvTools.isEmpty(topSlot) && !FluidItemHelper.isContainer(topSlot)) {
            setInventorySlotContents(SLOT_LIQUID_INPUT, emptyStack());
            dropItem(topSlot);
        }

        ItemStack bottomSlot = getStackInSlot(SLOT_OUTPUT_FLUID);
        if (!InvTools.isEmpty(bottomSlot) && !FluidItemHelper.isContainer(bottomSlot)) {
            setInventorySlotContents(SLOT_OUTPUT_FLUID, emptyStack());
            dropItem(bottomSlot);
        }

        if (clock(FluidTools.BUCKET_FILL_TIME))
            FluidTools.fillContainers(tank, this, SLOT_LIQUID_INPUT, SLOT_OUTPUT_FLUID, Fluids.CREOSOTE.get());
    }

    @Override
    public void onStructureChanged(boolean isComplete, boolean isMaster, Object[] data) {
        super.onStructureChanged(isComplete, isMaster, data);
        multiplier = (Integer) data[1];
    }

    @Override
    public boolean needsFuel() {
        ItemStack fuel = getStackInSlot(SLOT_INPUT);
        return sizeOf(fuel) < 8;
    }

    public StandardTank getTank() {
        return tank;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        switch (slot) {
            case SLOT_INPUT:
                return Crafters.cokeOven().getRecipe(stack).isPresent();
            case SLOT_LIQUID_INPUT:
                return FluidItemHelper.isRoomInContainer(stack);
            default:
                return false;
        }
    }

    @Override
    public IItemHandlerModifiable getItemHandler(@Nullable EnumFacing side) {
        return new InvWrapper(this) {
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot != SLOT_OUTPUT && slot != SLOT_OUTPUT_FLUID)
                    return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return EnumGui.COKE_OVEN;
    }
}
