/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.logic;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.ICokeOvenCrafter;
import mods.railcraft.common.fluids.*;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;

/**
 * Created by CovertJaguar on 12/27/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CokeOvenLogic extends CrafterLogic implements ITank {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_OUTPUT_FLUID = 2;
    public static final int SLOT_LIQUID_INPUT = 3;
    private static final int COOK_STEP_LENGTH = 50;
    private static final int TANK_CAPACITY = 64 * FluidTools.BUCKET_VOLUME;
    private final TankManager tankManager = new TankManager();
    private final StandardTank tank;
    private final InventoryMapper invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1).ignoreItemChecks();
    private int finishedAt;

    public CokeOvenLogic(Adapter adapter) {
        super(adapter, 4);
        tank = new StandardTank(TANK_CAPACITY, adapter.tile());
        tankManager.add(tank);
    }

    @Override
    void updateServer() {
        super.updateServer();
        if (clock() > finishedAt + COOK_STEP_LENGTH + 5)
            if (progress <= 0)
                setProcessing(false);

        ItemStack input = getStackInSlot(SLOT_INPUT);
        if (!InvTools.isEmpty(input)) {
            if (!paused && clock(COOK_STEP_LENGTH)) {
                ICokeOvenCrafter.IRecipe recipe = Crafters.cokeOven().getRecipe(input).orElse(null);

                if (recipe != null) {
                    ItemStack output = recipe.getOutput();
                    FluidStack fluidOutput = recipe.getFluidOutput();
                    if (invOutput.canFit(output)
                            && (fluidOutput == null || tank.fill(fluidOutput, false) >= fluidOutput.amount)) {
                        duration = recipe.getTickTime(input);
                        progress += COOK_STEP_LENGTH;
                        setProcessing(true);

                        if (progress >= duration) {
                            progress = 0;
                            finishedAt = clock();
                            decrStackSize(SLOT_INPUT, 1);
                            invOutput.addStack(output);
                            tank.fill(fluidOutput, true);
                            sendUpdateToClient();
                        }
                    } else {
                        progress = 0;
                        setProcessing(false);
                    }
                } else {
                    progress = 0;
                    setProcessing(false);
                    setInventorySlotContents(SLOT_INPUT, emptyStack());
                    dropItem(input);
                }
            }
        } else {
            progress = 0;
            setProcessing(false);
        }

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
            FluidTools.fillContainers(getTankManager(), this, SLOT_LIQUID_INPUT, SLOT_OUTPUT_FLUID, Fluids.CREOSOTE.get());
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

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != SLOT_OUTPUT && slot != SLOT_OUTPUT_FLUID)
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        tankManager.writeTanksToNBT(data);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
    }
}
