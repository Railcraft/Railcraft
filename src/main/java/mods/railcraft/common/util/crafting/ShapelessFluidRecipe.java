/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static mods.railcraft.common.util.inventory.InvTools.*;

/**
 * A version of {@link net.minecraftforge.oredict.ShapelessOreRecipe shaped ore recipe}
 * that supports fluid.
 */
//TODO something like cofh's fluid recipe
public class ShapelessFluidRecipe extends BaseRecipe {
    protected ItemStack output;
    protected List<Object> input = new ArrayList<>();
    protected int[] drains;

    public ShapelessFluidRecipe(ItemStack result, Object... recipe) {
        super(CraftingPlugin.getGenerator().next().getPath());
        output = result.copy();
        for (Object in : recipe) {
            if (in instanceof ItemStack) {
                input.add(((ItemStack) in).copy());
            } else if (in instanceof Item) {
                input.add(new ItemStack((Item) in));
            } else if (in instanceof Block) {
                input.add(new ItemStack((Block) in));
            } else if (in instanceof String) {
                input.add(OreDictionary.getOres((String) in));
            } else if (in instanceof FluidStack) {
                input.add(((FluidStack) in).copy());
            } else {
                StringBuilder ret = new StringBuilder("Invalid shapeless ore recipe: ");
                for (Object tmp : recipe) {
                    ret.append(tmp).append(", ");
                }
                ret.append(output);
                throw new RuntimeException(ret.toString());
            }
        }
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= input.size();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        return output.copy();
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public boolean matches(InventoryCrafting var1, World world) {
        List<Object> required = new ArrayList<>(input);

        drains = new int[var1.getSizeInventory()];
        for (int i = 0; i < var1.getSizeInventory(); i++) {
            ItemStack slot = var1.getStackInSlot(i);

            if (!isEmpty(slot)) {
                boolean inRecipe = false;

                for (Iterator<Object> iterator = required.iterator(); iterator.hasNext(); ) {
                    boolean match = false;
                    Object next = iterator.next();

                    if (next instanceof ItemStack) {
                        match = OreDictionary.itemMatches((ItemStack) next, slot, false);
                    } else if (next instanceof List) {
                        Iterator<ItemStack> itr = ((List<ItemStack>) next).iterator();
                        while (itr.hasNext() && !match) {
                            match = OreDictionary.itemMatches(itr.next(), slot, false);
                        }
                    } else if (next instanceof FluidStack) {
                        ItemStack toCheck = setSize(slot.copy(), 1);
                        if (toCheck.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                            IFluidHandlerItem handler = toCheck.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                            if (handler == null) {
                                continue;
                            }
                            FluidStack fluidStack = handler.drain((FluidStack) next, false);
                            if (fluidStack == null || fluidStack.amount < ((FluidStack) next).amount) {
                                continue;
                            }
                            match = true;
                            drains[i] = ((FluidStack) next).amount;
                        } else {
                            continue;
                        }
                    }

                    if (match) {
                        inRecipe = true;
                        iterator.remove();
                        break;
                    }
                }

                if (!inRecipe) {
                    return false;
                }
            }
        }

        return required.isEmpty();
    }

    /**
     * Returns the input for this recipe, any mod accessing this value should never
     * manipulate the values in this array as it will effect the recipe itself.
     *
     * @return The recipes input vales.
     */
    public List<Object> getInput() {
        return this.input;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        ItemStack[] ret = new ItemStack[inv.getSizeInventory()];
        if (drains.length != ret.length) {
            drains = new int[ret.length];
        }
        for (int i = 0; i < ret.length; i++) {
            if (drains[i] != 0) {
                ret[i] = inv.getStackInSlot(i);
                if (isEmpty(ret[i]))
                    continue;
                ret[i] = setSize(ret[i].copy(), 1);
                IFluidHandlerItem handler = ret[i].getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (handler == null) {
                    continue;
                }
                handler.drain(drains[i], true);
                ret[i] = makeSafe(handler.getContainer());
            } else {
                ret[i] = ForgeHooks.getContainerItem(inv.getStackInSlot(i));
            }
        }
        return NonNullList.from(ItemStack.EMPTY, ret);
    }
}