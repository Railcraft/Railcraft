/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 * <p/>
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.util.inventory;

import com.google.common.base.Predicate;
import mods.railcraft.api.core.IStackFilter;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.filters.StandardStackFilters;
import mods.railcraft.common.util.inventory.iterators.IExtInvSlot;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import mods.railcraft.common.util.inventory.wrappers.ChestWrapper;
import mods.railcraft.common.util.inventory.wrappers.SidedInventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.ITileFilter;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public abstract class InvTools {
    private static final String TAG_SLOT = "Slot";

    public static ItemStack makeStack(Item item, int qty, int meta) {
        if (item != null)
            return new ItemStack(item, qty, meta);
        return null;
    }

    @SuppressWarnings("unused")
    public static ItemStack makeStack(Block block, int qty, int meta) {
        if (block != null)
            return new ItemStack(block, qty, meta);
        return null;
    }

    public static ItemStack makeSafe(ItemStack stack) {
        if (stack.stackSize <= 0)
            return null;
        return stack;
    }

    @SuppressWarnings("unused")
    public static List<IInventory> getAdjacentInventories(World world, BlockPos pos) {
        return getAdjacentInventories(world, pos, null);
    }

    public static List<IInventory> getAdjacentInventories(World world, BlockPos pos, Class<? extends IInventory> type) {
        List<IInventory> list = new ArrayList<IInventory>(6);
        for (EnumFacing side : EnumFacing.VALUES) {
            IInventory inv = getInventoryFromSide(world, pos, side, type, null);
            if (inv != null)
                list.add(inv);
        }
        return list;
    }

//    public static Map<Integer, IInventory> getAdjacentInventoryMap(World world, int i, int j, int k) {
//        return getAdjacentInventoryMap(world, i, j, k, null);
//    }
//
//    public static Map<Integer, IInventory> getAdjacentInventoryMap(World world, int i, int j, int k, Class<? extends IInventory> type) {
//        Map<Integer, IInventory> map = new TreeMap<Integer, IInventory>();
//        for (int side = 0; side < 6; side++) {
//            IInventory inv = getInventoryFromSide(world, i, j, k, EnumFacing.VALUES[side], type, null);
//            if (inv != null)
//                map.put(side, inv);
//        }
//        return map;
//    }

    public static IInventory getInventoryFromSide(World world, BlockPos pos, EnumFacing side, final Class<? extends IInventory> type, final Class<? extends IInventory> exclude) {
        return getInventoryFromSide(world, pos, side, new ITileFilter() {
            @SuppressWarnings("SimplifiableIfStatement")
            @Override
            public boolean matches(TileEntity tile) {
                if (type != null && !type.isAssignableFrom(tile.getClass()))
                    return false;
                return exclude == null || !exclude.isAssignableFrom(tile.getClass());
            }
        });
    }

    public static IInventory getInventoryFromSide(World world, BlockPos pos, EnumFacing side, ITileFilter filter) {
        TileEntity tile = WorldPlugin.getTileEntityOnSide(world, pos, side);
        if (tile == null || !(tile instanceof IInventory) || !filter.matches(tile))
            return null;
        return getInventoryFromTile(tile, side.getOpposite());
    }

    public static IInventory getInventoryFromTile(TileEntity tile, EnumFacing side) {
        if (tile == null || !(tile instanceof IInventory))
            return null;

//        if (!PipeManager.canExtractItems(null, tile.getWorld(), tile.xCoord, tile.yCoord, tile.zCoord))
//            return null;

        if (tile instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) tile;
            return new ChestWrapper(chest);
        }
        return getInventory((IInventory) tile, side);
    }

    public static IInventory getInventory(IInventory inv, EnumFacing side) {
        if (inv == null)
            return null;

//        if (inv instanceof ISpecialInventory)
//            inv = new SpecialInventoryMapper((ISpecialInventory) inv, side);
        else if (inv instanceof ISidedInventory)
            inv = new SidedInventoryMapper((ISidedInventory) inv, side);
        return inv;
    }

    public static int[] buildSlotArray(int start, int size) {
        int[] slots = new int[size];
        for (int i = 0; i < size; i++) {
            slots[i] = start + i;
        }
        return slots;
    }

    public static boolean isSynthetic(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.hasKey("synthetic");
    }

    @SuppressWarnings("unused")
    public static void markItemSynthetic(ItemStack stack) {
        NBTTagCompound nbt = getItemData(stack);
        nbt.setBoolean("synthetic", true);
        NBTTagCompound display = nbt.getCompoundTag("display");
        nbt.setTag("display", display);
        NBTTagList lore = display.getTagList("Lore", 8);
        display.setTag("Lore", lore);
        lore.appendTag(new NBTTagString("\u00a77\u00a7o" + LocalizationPlugin.translate("item.synthetic")));
    }

    public static void addItemToolTip(ItemStack stack, String msg) {
        NBTTagCompound nbt = getItemData(stack);
        NBTTagCompound display = nbt.getCompoundTag("display");
        nbt.setTag("display", display);
        NBTTagList lore = display.getTagList("Lore", 8);
        display.setTag("Lore", lore);
        lore.appendTag(new NBTTagString(msg));
    }

    public static NBTTagCompound getItemData(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        return nbt;
    }

    public static void addNBTTag(ItemStack stack, String key, String value) {
        NBTTagCompound nbt = getItemData(stack);
        nbt.setString(key, value);
    }

    @SuppressWarnings("unused")
    public static void addNBTTag(ItemStack stack, String key, int value) {
        NBTTagCompound nbt = getItemData(stack);
        nbt.setInteger(key, value);
    }

    public static ItemStack depleteItem(ItemStack stack) {
        if (stack.stackSize == 1)
            return stack.getItem().getContainerItem(stack);
        else {
            stack.splitStack(1);
            return stack;
        }
    }

    public static ItemStack damageItem(ItemStack stack, int damage) {
        if (!stack.isItemStackDamageable()) return stack;
        int curDamage = stack.getItemDamage();
        curDamage += damage;
        stack.setItemDamage(curDamage);
        if (stack.getItemDamage() > stack.getMaxDamage()) {
            stack.stackSize--;
            stack.setItemDamage(0);
        }
        if (stack.stackSize <= 0)
            stack = null;
        return stack;
    }

    public static void dropItem(ItemStack stack, World world, BlockPos pos) {
        dropItem(stack, world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static void dropItem(ItemStack stack, World world, double x, double y, double z) {
        if (stack == null || stack.stackSize < 1)
            return;
        EntityItem entityItem = new EntityItem(world, x, y + 1.5, z, stack);
        entityItem.setDefaultPickupDelay();
        world.spawnEntityInWorld(entityItem);
    }

    public static void dropInventory(IInventory inv, World world, BlockPos pos) {
        if (Game.isNotHost(world)) return;
        for (IExtInvSlot slot : InventoryIterator.getIterable(inv)) {
            spewItem(slot.getStackInSlot(), world, pos);
            slot.setStackInSlot(null);
        }
    }

    public static void dropItems(Collection<ItemStack> items, World world, BlockPos pos) {
        if (Game.isNotHost(world)) return;
        for (ItemStack stack : items) {
            spewItem(stack, world, pos);
        }
    }

    private static void spewItem(ItemStack stack, World world, BlockPos pos) {
        if (stack != null) {
            float xOffset = MiscTools.RANDOM.nextFloat() * 0.8F + 0.1F;
            float yOffset = MiscTools.RANDOM.nextFloat() * 0.8F + 0.1F;
            float zOffset = MiscTools.RANDOM.nextFloat() * 0.8F + 0.1F;
            while (stack.stackSize > 0) {
                int numToDrop = MiscTools.RANDOM.nextInt(21) + 10;
                if (numToDrop > stack.stackSize)
                    numToDrop = stack.stackSize;
                ItemStack newStack = stack.copy();
                newStack.stackSize = numToDrop;
                stack.stackSize -= numToDrop;
                EntityItem entityItem = new EntityItem(world, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, newStack);
                float variance = 0.05F;
                entityItem.motionX = (float) MiscTools.RANDOM.nextGaussian() * variance;
                entityItem.motionY = (float) MiscTools.RANDOM.nextGaussian() * variance + 0.2F;
                entityItem.motionZ = (float) MiscTools.RANDOM.nextGaussian() * variance;
                world.spawnEntityInWorld(entityItem);
            }
        }
    }

    @SuppressWarnings("unused")
    public static boolean isInventoryEmpty(IInventory inv, EnumFacing side) {
        return isInventoryEmpty(getInventory(inv, side));
    }

    public static boolean isInventoryEmpty(IInventory inv) {
        ItemStack stack = null;
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            stack = slot.getStackInSlot();
            if (stack != null)
                break;
        }
        return stack == null;
    }

    public static boolean isAccessibleInventoryEmpty(IInventory inv, EnumFacing side) {
        return isAccessibleInventoryEmpty(getInventory(inv, side));
    }

    public static boolean isAccessibleInventoryEmpty(IInventory inv) {
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && slot.canTakeStackFromSlot(stack))
                return false;
        }
        return true;
    }

    public static boolean isInventoryFull(IInventory inv, EnumFacing side) {
        return isInventoryFull(getInventory(inv, side));
    }

    public static boolean isInventoryFull(IInventory inv) {
        ItemStack stack = null;
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            stack = slot.getStackInSlot();
            if (stack == null)
                break;
        }
        return stack != null;
    }

    public static boolean isEmptySlot(IInventory inv) {
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack == null)
                return true;
        }
        return false;
    }

    /**
     * Counts the number of items.
     *
     * @param inv the inventory
     * @return the number of items in the inventory
     */
    public static int countItems(IInventory inv) {
        int count = 0;
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null)
                count += stack.stackSize;
        }
        return count;
    }

    public static int countMaxItemStackSize(IInventory inv) {
        int count = 0;
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null)
                count += stack.getMaxStackSize();
        }
        return count;
    }

    public static int countItems(IInventory inv, Predicate<ItemStack> filter) {
        int count = 0;
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && filter.apply(stack))
                count += stack.stackSize;
        }
        return count;
    }

    public static boolean numItemsMoreThan(IInventory inv, int amount) {
        int count = 0;
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null)
                count += stack.stackSize;
            if (count >= amount)
                return true;
        }
        return false;
    }

    /**
     * Counts the number of items that match the filter.
     *
     * @param inv     the inventory
     * @param filters the items to match against
     * @return the number of items in the inventory
     */
    public static int countItems(IInventory inv, ItemStack... filters) {
        return countItems(inv, StackFilters.anyOf(filters));
    }

    public static int countItems(Collection<IInventory> inventories, ItemStack... filter) {
        int count = 0;
        for (IInventory inv : inventories) {
            count += InvTools.countItems(inv, filter);
        }
        return count;
    }

    public static int countStacks(IInventory inv) {
        int count = 0;
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null)
                count++;
        }
        return count;
    }

    /**
     * Returns true if the inventory contains the specified item.
     *
     * @param inv  the inventory  The inventory to check
     * @param item The ItemStack to look for
     * @return true is exists
     */
    public static boolean containsItem(IInventory inv, ItemStack item) {
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && isItemEqual(stack, item))
                return true;
        }
        return false;
    }

    /**
     * Returns a map backed by an <code>ItemStackMap</code> that lists the total
     * number of each type of item in the inventory.
     *
     * @param inv the inventory The <code>IInventory</code> to generate the manifest for
     * @return A <code>Map</code> that lists how many of each item is in * * *
     * the <code>IInventory</code>
     * @see ItemStackMap
     */
    public static Map<ItemStack, Integer> getManifest(IInventory inv) {
        Map<ItemStack, Integer> manifest = new ItemStackMap<Integer>();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slot = inv.getStackInSlot(i);
            if (slot != null) {
                Integer count = manifest.get(slot);
                if (count == null)
                    count = 0;
                count += slot.stackSize;
                manifest.put(slot, count);
            }
        }
        return manifest;
    }

    /**
     * Attempts to move a single item from one inventory to another.
     *
     * @param source the source inventory
     * @param dest   the destination inventory
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItem(IInventory source, IInventory dest) {
        return moveOneItem(source, dest, StandardStackFilters.ALL);
    }

    /**
     * Attempts to move a single item from one inventory to another.
     *
     * @param source  the source inventory
     * @param dest    the destination inventory
     * @param filters ItemStack to match against
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItem(IInventory source, IInventory dest, ItemStack... filters) {
        return moveOneItem(source, dest, StackFilters.anyOf(filters));
    }

    /**
     * Attempts to move a single item from one inventory to another.
     *
     * @param source the source inventory
     * @param dest   the destination inventory
     * @param filter an StandardStackFilters to match against
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItem(IInventory source, IInventory dest, IStackFilter filter) {
        InventoryManipulator imSource = InventoryManipulator.get(source);
        return imSource.moveItem(dest, filter);
    }

    /**
     * Attempts to move one item from a collection of inventories.
     *
     * @param sources the source inventories
     * @param dest    the destination inventory
     * @param filters ItemStack to match against
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItem(Collection<IInventory> sources, IInventory dest, ItemStack... filters) {
        for (IInventory inv : sources) {
            ItemStack moved = InvTools.moveOneItem(inv, dest, filters);
            if (moved != null)
                return moved;
        }
        return null;
    }

    /**
     * Attempts to move one item from a collection of inventories.
     *
     * @param sources the source inventories
     * @param dest    the destination inventory
     * @param filter  an StandardStackFilters to match against
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItem(Collection<IInventory> sources, IInventory dest, IStackFilter filter) {
        for (IInventory inv : sources) {
            ItemStack moved = InvTools.moveOneItem(inv, dest, filter);
            if (moved != null)
                return moved;
        }
        return null;
    }

    /**
     * Attempts to move one item to a collection of inventories.
     *
     * @param source       the source inventory
     * @param destinations the destination inventories
     * @param filters      ItemStack to match against
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItem(IInventory source, Collection<IInventory> destinations, ItemStack... filters) {
        for (IInventory dest : destinations) {
            ItemStack moved = InvTools.moveOneItem(source, dest, filters);
            if (moved != null)
                return moved;
        }
        return null;
    }

    /**
     * Attempts to move a single item from one inventory to another.
     * <p/>
     * Will not move any items in the filter.
     *
     * @param source the source inventory
     * @param dest   the destination inventory
     * @param filter an ItemStack[] to exclude
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItemExcept(IInventory source, IInventory dest, IStackFilter filter) {
        return moveOneItem(source, dest, filter.negate());
    }

    /**
     * Attempts to move one item from a collection of inventories.
     *
     * @param sources the source inventories
     * @param dest    the destination inventory
     * @param filter  the filter
     * @return null if nothing was moved, the stack moved otherwise
     */
    @SuppressWarnings("unused")
    public static ItemStack moveOneItemExcept(Collection<IInventory> sources, IInventory dest, IStackFilter filter) {
        for (IInventory inv : sources) {
            ItemStack moved = InvTools.moveOneItemExcept(inv, dest, filter);
            if (moved != null)
                return moved;
        }
        return null;
    }

    /**
     * Attempts to move one item to a collection of inventories.
     *
     * @param source       the source inventory
     * @param destinations the destinations
     * @param filter       the filter
     * @return null if nothing was moved, the stack moved otherwise
     */
    @SuppressWarnings("unused")
    public static ItemStack moveOneItemExcept(IInventory source, Collection<IInventory> destinations, IStackFilter filter) {
        for (IInventory dest : destinations) {
            ItemStack moved = InvTools.moveOneItemExcept(source, dest, filter);
            if (moved != null)
                return moved;
        }
        return null;
    }

    public static boolean isWildcard(ItemStack stack) {
        return isWildcard(stack.getItemDamage());
    }

    public static boolean isWildcard(int damage) {
        return damage == -1 || damage == OreDictionary.WILDCARD_VALUE;
    }

    public static boolean isItem(ItemStack stack, Item item) {
        return stack != null && item != null && stack.getItem() == item;
    }

    public static boolean isItemClass(ItemStack stack, @Nonnull Class<? extends Item> itemClass) {
        return stack != null && stack.getItem().getClass() == itemClass;
    }

    public static boolean extendsItemClass(ItemStack stack, @Nonnull Class<? extends Item> itemClass) {
        return stack != null && itemClass.isAssignableFrom(stack.getItem().getClass());
    }

    /**
     * A more robust item comparison function.
     * <p/>
     * Compares stackSize as well.
     * <p/>
     * Two null stacks will return true, unlike the other functions.
     * <p/>
     * This function is primarily intended to be used to track changes to an
     * ItemStack.
     *
     * @param a An ItemStack
     * @param b An ItemStack
     * @return True if equal
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isItemEqualStrict(ItemStack a, ItemStack b) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;
        if (a.getItem() != b.getItem())
            return false;
        if (a.stackSize != b.stackSize)
            return false;
        if (a.getItemDamage() != b.getItemDamage())
            return false;
        return a.getTagCompound() == null || a.getTagCompound().equals(b.getTagCompound());
    }

    /**
     * A more robust item comparison function.
     * <p/>
     * Compares stackSize as well.
     * <p/>
     * Two null stacks will return true, unlike the other functions.
     * <p/>
     * This function is primarily intended to be used to track changes to an
     * ItemStack.
     *
     * @param a An ItemStack
     * @param b An ItemStack
     * @return True if equal
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isItemEqualSemiStrict(ItemStack a, ItemStack b) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;
        if (a.getItem() != b.getItem())
            return false;
        if (a.getItemDamage() != b.getItemDamage())
            return false;
        return a.getTagCompound() == null || a.getTagCompound().equals(b.getTagCompound());
    }

    /**
     * A more robust item comparison function. Supports items with damage = -1
     * matching any sub-type.
     *
     * @param a An ItemStack
     * @param b An ItemStack
     * @return True if equal
     */
    public static boolean isItemEqual(ItemStack a, ItemStack b) {
        return isItemEqual(a, b, true, true);
    }

    /**
     * A more robust item comparison function. Supports items with damage = -1
     * matching any sub-type.
     *
     * @param a An ItemStack
     * @param b An ItemStack
     * @return True if equal
     */
    public static boolean isItemEqualIgnoreNBT(ItemStack a, ItemStack b) {
        return isItemEqual(a, b, true, false);
    }

    public static boolean isItemEqual(final ItemStack a, final ItemStack b, final boolean matchDamage, final boolean matchNBT) {
        if (a == null || b == null)
            return false;
        if (a.getItem() != b.getItem())
            return false;
        if (matchNBT && !ItemStack.areItemStackTagsEqual(a, b))
            return false;
        if (matchDamage && a.getHasSubtypes()) {
            if (isWildcard(a) || isWildcard(b))
                return true;
            if (a.getItemDamage() != b.getItemDamage())
                return false;
        }
        return true;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isCartItemEqual(final ItemStack a, final ItemStack b, final boolean matchDamage) {
        if (!isItemEqual(a, b, matchDamage, false))
            return false;
        return !(a.hasDisplayName() && !a.getDisplayName().equals(b.getDisplayName()));
    }

    /**
     * Returns true if the item is equal to any one of several possible matches.
     *
     * @param stack   the ItemStack to test
     * @param matches the ItemStacks to test against
     * @return true if a match is found
     */
    public static boolean isItemEqual(ItemStack stack, ItemStack... matches) {
        for (ItemStack match : matches) {
            if (isItemEqual(stack, match))
                return true;
        }
        return false;
    }

    /**
     * Returns true if the item is equal to any one of several possible matches.
     *
     * @param stack   the ItemStack to test
     * @param matches the ItemStacks to test against
     * @return true if a match is found
     */
    public static boolean isItemEqual(ItemStack stack, Collection<ItemStack> matches) {
        for (ItemStack match : matches) {
            if (isItemEqual(stack, match))
                return true;
        }
        return false;
    }

    public static boolean isItemGreaterOrEqualThan(ItemStack stackA, ItemStack stackB) {
        return isItemEqual(stackA, stackB) && stackA.stackSize >= stackB.stackSize;
    }

    public static boolean isItemLessThanOrEqualTo(ItemStack stackA, ItemStack stackB) {
        return isItemEqual(stackA, stackB) && stackA.stackSize <= stackB.stackSize;
    }

    /**
     * Places an ItemStack in a destination IInventory. Will attempt to move as
     * much of the stack as possible, returning any remainder.
     *
     * @param stack The ItemStack to put in the inventory.
     * @param dest  The destination IInventory.
     * @return Null if itemStack was completely moved, a new itemStack with
     * remaining stackSize if part or none of the stack was moved.
     */
    public static ItemStack moveItemStack(ItemStack stack, IInventory dest) {
        InventoryManipulator im = InventoryManipulator.get(dest);
        return im.addStack(stack);
    }

    /**
     * Places an ItemStack in a collection destination IInventories. Will attempt to move as
     * much of the stack as possible, returning any remainder.
     *
     * @param stack The ItemStack to put in the inventory.
     * @param dest  The destination IInventories.
     * @return Null if itemStack was completely moved, a new itemStack with
     * remaining stackSize if part or none of the stack was moved.
     */
    @SuppressWarnings("unused")
    public static ItemStack moveItemStack(ItemStack stack, Collection<IInventory> dest) {
        for (IInventory inv : dest) {
            stack = moveItemStack(stack, inv);
            if (stack == null)
                return null;
        }
        return stack;
    }

    /**
     * Checks if there is room for the ItemStack in the inventory.
     *
     * @param stack The ItemStack
     * @param dest  The IInventory
     * @return true if room for stack
     */
    public static boolean isRoomForStack(ItemStack stack, IInventory dest) {
        if (stack == null || dest == null)
            return false;
        InventoryManipulator im = InventoryManipulator.get(dest);
        return im.canAddStack(stack);
    }

//    /**
//     * Removes a up to numItems worth of items from the inventory, not caring
//     * about what the items are.
//     *
//     * @param inv the inventory the inventory
//     * @param numItems the number of items
//     * @return the items removed
//     */
//    public static ItemStack[] removeItems(IInventory inv, int numItems) {
////        if (inv instanceof ISpecialInventory)
////            return ((ISpecialInventory) inv).extractItem(true, null, numItems);
//        StandaloneInventory output = new StandaloneInventory(27);
//        for (int i = 0; i < inv.getSizeInventory(); i++) {
//            if (numItems <= 0)
//                break;
//            ItemStack slot = inv.getStackInSlot(i);
//            if (slot == null)
//                continue;
//            ItemStack removed = inv.decrStackSize(i, numItems);
//            numItems -= removed.stackSize;
//            ItemStack remainder = moveItemStack(removed, output);
//            if (remainder != null) {
//                moveItemStack(remainder, inv);
//                numItems += remainder.stackSize;
//                break;
//            }
//        }
//
//        List<ItemStack> list = new LinkedList<ItemStack>();
//        for (ItemStack stack : output.getContents()) {
//            if (stack != null)
//                list.add(stack);
//        }
//        return list.toArray(new ItemStack[0]);
//    }

    /**
     * Removes and returns a single item from the inventory.
     *
     * @param inv the inventory The inventory
     * @return An ItemStack
     */
    @SuppressWarnings("unused")
    public static ItemStack removeOneItem(IInventory inv) {
        return removeOneItem(inv, StandardStackFilters.ALL);
    }

    /**
     * Removes and returns a single item from the inventory that matches the
     * filter.
     *
     * @param inv    the inventory    The inventory
     * @param filter the filter to match against
     * @return An ItemStack
     */
    public static ItemStack removeOneItem(IInventory inv, ItemStack... filter) {
        return removeOneItem(inv, StackFilters.anyOf(filter));
    }

    /**
     * Removes and returns a single item from the inventory that matches the
     * filter.
     *
     * @param inv    the inventory    The inventory
     * @param filter the filter to match against
     * @return An ItemStack
     */
    public static ItemStack removeOneItem(IInventory inv, IStackFilter filter) {
        InventoryManipulator im = InventoryManipulator.get(inv);
        return im.removeItem(filter);
    }

    /**
     * Removes and returns a single item from the inventory that matches the
     * filter.
     *
     * @param invs   The inventories
     * @param filter the filter to match against
     * @return An ItemStack
     */
    public static ItemStack removeOneItem(Collection<IInventory> invs, IStackFilter filter) {
        for (IInventory inv : invs) {
            ItemStack stack = removeOneItem(inv, filter);
            if (stack != null)
                return stack;
        }
        return null;
    }

    /**
     * Removes a specified number of items matching the filter, but only if the
     * operation can be completed. If the function returns false, the inventory
     * will not be modified.
     *
     * @param inv    the inventory
     * @param amount the amount of items to remove
     * @param filter the filter to match against
     * @return true if there are enough items that can be removed, false
     * otherwise.
     */
    public static boolean removeItemsAbsolute(IInventory inv, int amount, ItemStack... filter) {
        return removeItemsAbsolute(inv, amount, StackFilters.anyOf(filter));
    }

    /**
     * Removes a specified number of items matching the filter, but only if the
     * operation can be completed. If the function returns false, the inventory
     * will not be modified.
     *
     * @param inv    the inventory
     * @param amount the amount of items to remove
     * @param filter the filter to match against
     * @return true if there are enough items that can be removed, false
     * otherwise.
     */
    public static boolean removeItemsAbsolute(IInventory inv, int amount, IStackFilter filter) {
        InventoryManipulator im = InventoryManipulator.get(inv);
        if (im.canRemoveItems(filter, amount)) {
            im.removeItems(filter, amount);
            return true;
        }
        return false;
    }

    /**
     * Returns a single item from the inventory that matches the
     * filter, but does not remove it.
     *
     * @param inv    the inventory    The inventory
     * @param filter the filter to match against
     * @return An ItemStack
     */
    @SuppressWarnings("unused")
    public static ItemStack findMatchingItem(IInventory inv, IStackFilter filter) {
        InventoryManipulator im = InventoryManipulator.get(inv);
        return im.tryRemoveItem(filter);
    }

    /**
     * Returns all items from the inventory that match the
     * filter, but does not remove them.
     * The resulting set will be populated with a single instance of each item type.
     *
     * @param inv    the inventory    The inventory
     * @param filter EnumItemType to match against
     * @return A Set of ItemStacks
     */
    public static Set<ItemStack> findMatchingItems(IInventory inv, IStackFilter filter) {
        Set<ItemStack> items = new ItemStackSet();
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && stack.stackSize > 0 && filter.apply(stack)) {
                stack = stack.copy();
                stack.stackSize = 1;
                items.add(stack);
            }
        }
        return items;
    }

    public static void writeInvToNBT(IInventory inv, String tag, NBTTagCompound data) {
        NBTTagList list = new NBTTagList();
        for (byte slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte(TAG_SLOT, slot);
                writeItemToNBT(stack, itemTag);
                list.appendTag(itemTag);
            }
        }
        data.setTag(tag, list);
    }

    public static void readInvFromNBT(IInventory inv, String tag, NBTTagCompound data) {
        NBTTagList list = data.getTagList(tag, 10);
        for (byte entry = 0; entry < list.tagCount(); entry++) {
            NBTTagCompound itemTag = list.getCompoundTagAt(entry);
            int slot = itemTag.getByte(TAG_SLOT);
            if (slot >= 0 && slot < inv.getSizeInventory()) {
                ItemStack stack = readItemFromNBT(itemTag);
                inv.setInventorySlotContents(slot, stack);
            }
        }
    }

    public static void writeItemToNBT(ItemStack stack, NBTTagCompound data) {
        if (stack == null || stack.stackSize <= 0)
            return;
        if (stack.stackSize > 127)
            stack.stackSize = 127;
        stack.writeToNBT(data);
    }

    public static ItemStack readItemFromNBT(NBTTagCompound data) {
        return ItemStack.loadItemStackFromNBT(data);
    }

    public static boolean isStackEqualToBlock(ItemStack stack, Block block) {
        return !(stack == null || block == null) && stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == block;
    }

    @Nullable
    public static Block getBlockFromStack(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock)
            return ((ItemBlock) stack.getItem()).getBlock();
        return null;
    }
}
