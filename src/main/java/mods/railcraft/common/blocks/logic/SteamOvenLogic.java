/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import com.google.common.base.Preconditions;
import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.RailcraftSoundEvents;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;

import static net.minecraft.util.EnumFacing.NORTH;

/**
 * Created by CovertJaguar on 1/11/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SteamOvenLogic extends CrafterLogic implements INeedsFuel {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 9;
    private static final int STEAM_PER_STEP = 500; // total == 80000
    private static final FluidStack DRAIN_STACK = Fluids.STEAM.get(STEAM_PER_STEP);
    private static final int TOTAL_COOK_TIME = 256;
    private static final int ITEMS_SMELTED = 9;
    private static final int TANK_CAPACITY = 8 * FluidTools.BUCKET_VOLUME;
    private final TankManager tankManager = new TankManager();
    private final FilteredTank tank;
    private final InventoryMapper invInput = InventoryMapper.make(this, SLOT_INPUT, 9);
    private final InventoryMapper invOutput = new InventoryMapper(this, SLOT_OUTPUT, 9).ignoreItemChecks();
    private EnumFacing facing = NORTH;

    public SteamOvenLogic(Adapter adapter) {
        super(adapter, 18);
        tank = new FilteredTank(TANK_CAPACITY, adapter.tile()).setFilterFluid(Fluids.STEAM);
        addSubLogic(new TankLogic(adapter).addTank(tank));
        setDuration(TOTAL_COOK_TIME);
    }

    @Override
    protected boolean lacksRequirements() {
        return InventoryIterator.get(invInput).streamStacks()
                .noneMatch(s -> InvTools.nonEmpty(FurnaceRecipes.instance().getSmeltingResult(s)));
    }

    @Override
    protected int calculateDuration() {
        return TOTAL_COOK_TIME;
    }

    @Override
    protected boolean doProcessStep() {
        if (!needsFuel()) {
            tank.drain(STEAM_PER_STEP, true);
            return true;
        }
        return false;
    }

    @Override
    protected boolean craftAndPush() {
        int count = 0;
        boolean changed = true;
        boolean smelted = false;
        while (count < ITEMS_SMELTED && changed) {
            changed = false;
            for (int slot = 0; slot < 9 && count < ITEMS_SMELTED; slot++) {
                ItemStack stack = invInput.getStackInSlot(slot);
                if (!InvTools.isEmpty(stack)) {
                    ItemStack output = InvTools.copy(FurnaceRecipes.instance().getSmeltingResult(stack));
                    if (!InvTools.isEmpty(output)
                            && invOutput.canFit(output)
                            && InvTools.isEmpty(invOutput.addStack(output))) {
                        invInput.decrStackSize(slot, 1);
                        changed = true;
                        count++;
                    }
                }
            }
            smelted |= changed;
        }
        if (smelted)
            SoundHelper.playSound(theWorldAsserted(), null, getPos(),
                    RailcraftSoundEvents.MECHANICAL_STEAM_BURST,
                    SoundCategory.BLOCKS,
                    1F, (float) (1 + MiscTools.RANDOM.nextGaussian() * 0.1));
        return smelted;
    }

    @Override
    public boolean needsFuel() {
        FluidStack steam = tank.drain(STEAM_PER_STEP, false);
        return !Fluids.contains(steam, DRAIN_STACK);
    }

    public StandardTank getTank() {
        return tank;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        if (InvTools.isEmpty(stack))
            return false;
        if (slot >= SLOT_OUTPUT)
            return false;
        return InvTools.nonEmpty(FurnaceRecipes.instance().getSmeltingResult(stack));
    }

    @Override
    public IItemHandlerModifiable getItemHandler(@Nullable EnumFacing side) {
        return new InvWrapper(this) {
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot < SLOT_OUTPUT)
                    return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        Preconditions.checkArgument(facing.getAxis() != EnumFacing.Axis.Y, "Cannot set facing to up or down.");
        this.facing = facing;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("facing", (byte) facing.getHorizontalIndex());
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        facing = EnumFacing.byHorizontalIndex(data.getByte("facing"));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeEnum(facing);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        EnumFacing f = data.readEnum(EnumFacing.VALUES);
        if (facing != f) {
            facing = f;
            adapter.updateModels();
        }
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return EnumGui.STEAM_OVEN;
    }
}
