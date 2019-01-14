/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.crafting.ISimpleRecipe;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;
import java.util.Optional;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;

/**
 * Created by CovertJaguar on 1/11/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class SingleInputRecipeCrafterLogic<R extends ISimpleRecipe> extends CrafterLogic {
    private final int inputSlot;
    protected @Nullable R recipe;
    private ItemStack lastInput = emptyStack();

    protected SingleInputRecipeCrafterLogic(Adapter adapter, int sizeInv, int inputSlot) {
        super(adapter, sizeInv);
        this.inputSlot = inputSlot;
    }

    protected abstract Optional<R> getRecipe(ItemStack input);

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void setupCrafting() {
        ItemStack input = getStackInSlot(inputSlot);
        if (!InvTools.isItemEqual(lastInput, input)) {
            lastInput = input;
            recipe = getRecipe(input).orElse(null);
            if (recipe == null && !input.isEmpty()) {
                setInventorySlotContents(inputSlot, emptyStack());
                dropItem(input);
            }
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected boolean lacksRequirements() {
        return recipe == null;
    }

    @Override
    protected final int calculateDuration() {
        Objects.requireNonNull(recipe);
        return recipe.getTickTime(getStackInSlot(inputSlot));
    }
}
