package mods.railcraft.common.util.crafting;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;
import static mods.railcraft.common.util.inventory.InvTools.makeSafe;
import static mods.railcraft.common.util.inventory.InvTools.setSize;

/**
 * A version of {@link net.minecraftforge.oredict.ShapedOreRecipe shaped ore recipe}
 * that supports fluid.
 */
public class ShapedFluidRecipe implements IRecipe {
    //Added in for future ease of change, but hard coded for now.
    public static final int MAX_CRAFT_GRID_WIDTH = 3;
    public static final int MAX_CRAFT_GRID_HEIGHT = 3;

    protected ItemStack output;
    protected Object[] input;
    protected int[] drains;
    protected int width = 0;
    protected int height = 0;
    protected boolean mirrored = true;

    public ShapedFluidRecipe(ItemStack result, Object... recipe) {
        output = result.copy();

        StringBuilder sb = new StringBuilder();
        int idx = 0;

        if (recipe[idx] instanceof Boolean) {
            mirrored = (Boolean) recipe[idx];
            if (recipe[idx + 1] instanceof Object[]) {
                recipe = (Object[]) recipe[idx + 1];
            } else {
                idx = 1;
            }
        }

        if (recipe[idx] instanceof String[]) {
            String[] parts = ((String[]) recipe[idx++]);

            for (String s : parts) {
                width = s.length();
                sb.append(s);
            }

            height = parts.length;
        } else {
            while (recipe[idx] instanceof String) {
                String s = (String) recipe[idx++];
                sb.append(s);
                width = s.length();
                height++;
            }
        }

        String shape = sb.toString();

        if (width * height != shape.length()) {
            StringBuilder ret = new StringBuilder("Invalid shaped ore recipe: ");
            for (Object tmp : recipe) {
                ret.append(tmp).append(", ");
            }
            ret.append(output);
            throw new RuntimeException(ret.toString());
        }

        HashMap<Character, Object> itemMap = new HashMap<>();

        for (; idx < recipe.length; idx += 2) {
            Character chr = (Character) recipe[idx];
            Object in = recipe[idx + 1];

            if (in instanceof ItemStack) {
                itemMap.put(chr, ((ItemStack) in).copy());
            } else if (in instanceof Item) {
                itemMap.put(chr, new ItemStack((Item) in));
            } else if (in instanceof Block) {
                itemMap.put(chr, new ItemStack((Block) in, 1, OreDictionary.WILDCARD_VALUE));
            } else if (in instanceof String) {
                itemMap.put(chr, OreDictionary.getOres((String) in));
            } else if (in instanceof FluidStack) {
                itemMap.put(chr, ((FluidStack) in).copy());
            } else {
                StringBuilder ret = new StringBuilder("Invalid shaped ore recipe: ");
                for (Object tmp : recipe) {
                    ret.append(tmp).append(", ");
                }
                ret.append(output);
                throw new RuntimeException(ret.toString());
            }
        }

        input = new Object[width * height];
        int x = 0;
        for (char chr : shape.toCharArray()) {
            input[x++] = itemMap.get(chr);
        }
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        return output.copy();
    }

    /**
     * Returns the size of the recipe area
     */
    @Override
    public int getRecipeSize() {
        return input.length;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        drains = new int[inv.getSizeInventory()];
        for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++) {
            for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y) {
                if (checkMatch(inv, x, y, false)) {
                    return true;
                }

                if (mirrored && checkMatch(inv, x, y, true)) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
        Arrays.fill(drains, 0);
        for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
            for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++) {
                int subX = x - startX;
                int subY = y - startY;
                Object target = null;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
                    if (mirror) {
                        target = input[width - subX - 1 + subY * width];
                    } else {
                        target = input[subX + subY * width];
                    }
                }

                ItemStack slot = inv.getStackInRowAndColumn(x, y);
                int pos = y * inv.getWidth() + x;

                if (target instanceof ItemStack) {
                    if (!OreDictionary.itemMatches((ItemStack) target, slot, false)) {
                        return false;
                    }
                } else if (target instanceof List) {
                    boolean matched = false;

                    Iterator<ItemStack> itr = ((List<ItemStack>) target).iterator();
                    while (itr.hasNext() && !matched) {
                        matched = OreDictionary.itemMatches(itr.next(), slot, false);
                    }

                    if (!matched) {
                        return false;
                    }
                } else if (target instanceof FluidStack) {
                    if (isEmpty(slot))
                        return false;
                    ItemStack toCheck = setSize(slot.copy(), 1);
                    if (toCheck.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                        IFluidHandler handler = toCheck.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                        FluidStack fluidStack = handler.drain((FluidStack) target, false);
                        if (fluidStack == null || fluidStack.amount < ((FluidStack) target).amount) {
                            return false;
                        }
                        drains[pos] = ((FluidStack) target).amount;
                    } else if (toCheck.getItem() instanceof IFluidContainerItem) {
                        // legacy
                        IFluidContainerItem handler = (IFluidContainerItem) toCheck.getItem();
                        FluidStack fluidStack = handler.getFluid(toCheck);
                        if (fluidStack == null || fluidStack.getFluid() != ((FluidStack) target).getFluid() || fluidStack.amount < ((FluidStack) target).amount) {
                            return false;
                        }
                        drains[pos] = -((FluidStack) target).amount;
                    } else {
                        return false;
                    }
                } else if (target == null && !isEmpty(slot)) {
                    return false;
                }
            }
        }

        return true;
    }

    @SuppressWarnings("unused")
    public ShapedFluidRecipe setMirrored(boolean mirror) {
        mirrored = mirror;
        return this;
    }

    /**
     * Returns the input for this recipe, any mod accessing this value should never
     * manipulate the values in this array as it will effect the recipe itself.
     *
     * @return The recipes input vales.
     */
    public Object[] getInput() {
        return this.input;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] ret = new ItemStack[inv.getSizeInventory()];
        for (int i = 0; i < ret.length; i++) {
            if (drains[i] != 0) {
                ret[i] = inv.getStackInSlot(i);
                if (isEmpty(ret[i]))
                    continue;
                ret[i] = setSize(ret[i].copy(), 1);
                if (drains[i] > 0) {
                    ret[i].getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).drain(drains[i], true);
                } else {
                    IFluidContainerItem fluidContainerItem = (IFluidContainerItem) ret[i].getItem();
                    fluidContainerItem.drain(ret[i], -drains[i], true);
                }
                ret[i] = makeSafe(ret[i]);
            } else {
                ret[i] = ForgeHooks.getContainerItem(inv.getStackInSlot(i));
            }
        }
        return ret;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
