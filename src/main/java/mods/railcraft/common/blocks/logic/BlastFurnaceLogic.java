/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.inventory.wrappers.InventoryWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

/**
 * Created by CovertJaguar on 12/29/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlastFurnaceLogic extends SingleInputRecipeCrafterLogic<IBlastFurnaceCrafter.IRecipe> {
    public static final Predicate<ItemStack> INPUT_FILTER = stack -> Crafters.blastFurnace().getRecipe(stack).isPresent();
    public static final Predicate<ItemStack> FUEL_FILTER = stack -> Crafters.blastFurnace().getFuel(stack).isPresent();
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_SLAG = 3;
    public final InventoryMapper invFuel = InventoryMapper.make(this, SLOT_FUEL, 1);
    //    private final InventoryMapper invInput = new InventoryMapper(this, SLOT_INPUT, 1);
    private final InventoryWrapper invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1).ignoreItemChecks();
    private final InventoryWrapper invSlag = new InventoryMapper(this, SLOT_SLAG, 1).ignoreItemChecks();

    public BlastFurnaceLogic(Adapter adapter) {
        super(adapter, 4, SLOT_INPUT);
        addLogic(new FurnaceLogic(adapter) {
            private static final int FUEL_PER_TICK = 5;

            @Override
            protected void updateServer() {
                super.updateServer();
                setBurnTime(getBurnTime() - FUEL_PER_TICK);
            }

            @Override
            public boolean needsFuel() {
                ItemStack fuel = getStackInSlot(SLOT_FUEL);
                return sizeOf(fuel) < 8;
            }

            @Override
            public void loadFuel() {
                ItemStack fuel;
                if (getBurnTime() > FUEL_PER_TICK * 2 || (fuel = getStackInSlot(SLOT_FUEL)).isEmpty()) return;
                int itemBurnTime = Crafters.blastFurnace().getFuel(fuel).map(f -> f.getTickTime(fuel)).orElse(0);
                if (itemBurnTime <= 0) return;
                setCurrentItemBurnTime(itemBurnTime + getBurnTime());
                setBurnTime(getCurrentItemBurnTime());
                setInventorySlotContents(SLOT_FUEL, InvTools.depleteItem(fuel));
            }

        });
    }

    @Override
    protected Optional<IBlastFurnaceCrafter.IRecipe> getRecipe(ItemStack input) {
        return Crafters.blastFurnace().getRecipe(input);
    }

    @Override
    protected boolean craftAndPush() {
        Objects.requireNonNull(recipe);
        ItemStack nextOutput = recipe.getOutput();

        if (!invOutput.canFit(nextOutput)) return false;

        ItemStack nextSlag = RailcraftItems.DUST.getStack(recipe.getSlagOutput(), ItemDust.EnumDust.SLAG);

        if (!invSlag.canFit(nextSlag)) return false;

        invOutput.addStack(nextOutput);
        invSlag.addStack(nextSlag);
        decrStackSize(SLOT_INPUT, 1);

        setProgress(0);
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        switch (slot) {
            case SLOT_OUTPUT:
            case SLOT_SLAG:
                return false;
            case SLOT_FUEL:
                return FUEL_FILTER.test(stack);
            case SLOT_INPUT:
                return INPUT_FILTER.test(stack);
        }
        return false;
    }

    @Override
    public IItemHandlerModifiable getItemHandler(@Nullable EnumFacing side) {
        return new InvWrapper(this) {
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot != SLOT_OUTPUT && slot != SLOT_SLAG)
                    return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return EnumGui.BLAST_FURNACE;
    }
}
