/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IOutputEntry;
import mods.railcraft.api.crafting.IRockCrusherCrafter;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.IInvSlot;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.InventoryCopy;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by CovertJaguar on 1/12/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RockCrusherLogic extends CrafterLogic {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 9;
    private static final double CRUSHING_POWER_COST_PER_TICK = 160;
    private static final double CRUSHING_POWER_COST_PER_STEP = CRUSHING_POWER_COST_PER_TICK * PROGRESS_STEP;
    private static final double MAX_STORED_CHARGE = 8_000;
    private static final Optional<IRockCrusherCrafter.IRecipe> DESTRUCTION_RECIPE = Optional.of(new IRockCrusherCrafter.IRecipe() {
        @Override
        public List<IOutputEntry> getOutputs() {
            return Collections.emptyList();
        }

        @Override
        public ResourceLocation getName() {
            return new ResourceLocation("railcraft", "destruction");
        }

        @Override
        public Ingredient getInput() {
            return new Ingredient() {
                @Override
                public boolean test(@javax.annotation.Nullable ItemStack input) {
                    return true;
                }
            };
        }

        @Override
        public int getTickTime(ItemStack input) {
            return IRockCrusherCrafter.PROCESS_TIME;
        }
    });
    public final InventoryMapper invInput = new InventoryMapper(this, 0, 9).ignoreItemChecks();
    public final InventoryMapper invOutput = new InventoryMapper(this, 9, 9).ignoreItemChecks();
    private Optional<IRockCrusherCrafter.IRecipe> currentRecipe = Optional.empty();
    private final Random random = new Random();
    private int currentSlot;
    private double storedCharge;
    private ItemStack crushed = new ItemStack(Blocks.COBBLESTONE);

    public RockCrusherLogic(Adapter adapter) {
        super(adapter, 18);
    }

    @Override
    protected void updateServer() {
        super.updateServer();
        storedCharge += Charge.distribution.network(theWorldAsserted()).access(getPos())
                .removeCharge(Math.min(CRUSHING_POWER_COST_PER_TICK, Math.max(0.0, MAX_STORED_CHARGE - storedCharge)));
    }

    private boolean isRecipeValid() {
        return currentRecipe.map(r -> r.getInput().apply(invInput.getStackInSlot(currentSlot))).orElse(false);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        currentRecipe = Optional.empty();
    }

    @Override
    protected void setupCrafting() {
        if (!isRecipeValid()) {
            currentRecipe = Optional.empty();
            for (IInvSlot slot : InventoryIterator.get(invInput)) {
                if (slot.hasStack()) {
                    currentSlot = slot.getIndex();
                    ItemStack stack = slot.getStack();
                    Optional<IRockCrusherCrafter.IRecipe> newRecipe = Crafters.rockCrusher().getRecipe(stack);
                    if (!newRecipe.isPresent()) {
                        newRecipe = DESTRUCTION_RECIPE;
                    }
                    currentRecipe = newRecipe;
                    break;
                }
            }
        }
    }

    @Override
    protected int calculateDuration() {
        return currentRecipe.map(r -> r.getTickTime(invInput.getStackInSlot(currentSlot))).orElse(IRockCrusherCrafter.PROCESS_TIME);
    }

    @Override
    protected boolean lacksRequirements() {
        return !currentRecipe.isPresent();
    }

    @Override
    protected boolean doProcessStep() {
        return useInternalCharge(CRUSHING_POWER_COST_PER_STEP);
    }

    public ItemStack getCrushed() {
        return crushed;
    }

    @Override
    protected boolean craftAndPush() {
        final IRockCrusherCrafter.IRecipe recipe = currentRecipe.orElseThrow(NullPointerException::new);
        InventoryCopy tempInv = new InventoryCopy(invOutput);
        List<ItemStack> outputs = recipe.pollOutputs(random);
        boolean hasRoom = outputs.stream()
                .map(tempInv::addStack)
                .allMatch(InvTools::isEmpty);

        if (hasRoom) {
            outputs.forEach(invOutput::addStack);
            crushed = invInput.removeOneItem(recipe.getInput());

            SoundHelper.playSound(theWorldAsserted(), null, getPos(), SoundEvents.ENTITY_IRONGOLEM_DEATH,
                    SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.25F + 0.7F);
            return true;
        }
        return false;
    }

    public boolean hasInternalCapacity(double amount) {
        return storedCharge >= amount;
    }

    public boolean useInternalCharge(double amount) {
        if (storedCharge >= amount) {
            storedCharge -= amount;
            return true;
        }
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot < 9 && super.isItemValidForSlot(slot, stack) && Crafters.rockCrusher().getRecipe(stack).isPresent();
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

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setDouble("charge", storedCharge);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        storedCharge = data.getDouble("charge");
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeItemStack(crushed);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        crushed = data.readItemStack();
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return EnumGui.ROCK_CRUSHER;
    }
}
