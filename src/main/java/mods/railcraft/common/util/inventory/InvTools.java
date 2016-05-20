/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory;

import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.filters.ArrayStackFilter;
import mods.railcraft.common.util.inventory.filters.InvertedStackFilter;
import mods.railcraft.common.util.inventory.filters.StackFilter;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import mods.railcraft.common.util.inventory.wrappers.ChestWrapper;
import mods.railcraft.common.util.inventory.wrappers.IInvSlot;
import mods.railcraft.common.util.inventory.wrappers.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.SidedInventoryMapper;
import mods.railcraft.common.util.misc.EnumColor;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.ITileFilter;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class InvTools {
    private static final String TAG_SLOT = "Slot";

    public static ItemStack makeStack(Item item, int qty, int meta) {
        if (item != null)
            return new ItemStack(item, qty, meta);
        return null;
    }

    public static ItemStack makeStack(Block block, int qty, int meta) {
        if (block != null)
            return new ItemStack(block, qty, meta);
        return null;
    }

    public static List<IInventory> getAdjacentInventories(World world, int i, int j, int k) {
        return getAdjacentInventories(world, i, j, k, null);
    }

    public static List<IInventory> getAdjacentInventories(World world, int i, int j, int k, Class<? extends IInventory> type) {
        List<IInventory> list = new ArrayList<IInventory>(5);
        for (int side = 0; side < 6; side++) {
            IInventory inv = getInventoryFromSide(world, i, j, k, ForgeDirection.getOrientation(side), type, null);
            if (inv != null)
                list.add(inv);
        }
        return list;
    }

    public static Map<Integer, IInventory> getAdjacentInventoryMap(World world, int i, int j, int k) {
        return getAdjacentInventoryMap(world, i, j, k, null);
    }

    public static Map<Integer, IInventory> getAdjacentInventoryMap(World world, int i, int j, int k, Class<? extends IInventory> type) {
        Map<Integer, IInventory> map = new TreeMap<Integer, IInventory>();
        for (int side = 0; side < 6; side++) {
            IInventory inv = getInventoryFromSide(world, i, j, k, ForgeDirection.getOrientation(side), type, null);
            if (inv != null)
                map.put(side, inv);
        }
        return map;
    }

    public static IInventory getInventoryFromSide(World world, int x, int y, int z, ForgeDirection side, final Class<? extends IInventory> type, final Class<? extends IInventory> exclude) {
        return getInventoryFromSide(world, x, y, z, side, new ITileFilter() {
            @Override
            public boolean matches(TileEntity tile) {
                if (type != null && !type.isAssignableFrom(tile.getClass()))
                    return false;
                return exclude == null || !exclude.isAssignableFrom(tile.getClass());
            }
        });
    }

    public static IInventory getInventoryFromSide(World world, int x, int y, int z, ForgeDirection side, ITileFilter filter) {
        TileEntity tile = WorldPlugin.getTileEntityOnSide(world, x, y, z, side);
        if (tile == null || !(tile instanceof IInventory) || !filter.matches(tile))
            return null;
        return getInventoryFromTile(tile, side.getOpposite());
    }

    public static IInventory getInventoryFromTile(TileEntity tile, ForgeDirection side) {
        if (tile == null || !(tile instanceof IInventory))
            return null;

//        if (!PipeManager.canExtractItems(null, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord))
//            return null;

        if (tile instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) tile;
            return new ChestWrapper(chest);
        }
        return getInventory((IInventory) tile, side);
    }

    public static IInventory getInventory(IInventory inv, ForgeDirection side) {
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

    public static EnumColor getItemColor(ItemStack stack) {
        if (stack == null)
            return null;
        if (isStackEqualToBlock(stack, Blocks.wool))
            return EnumColor.fromId(15 - stack.getItemDamage());
        if (stack.getItem() == Items.dye)
            return EnumColor.fromId(stack.getItemDamage());
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey("color"))
            return EnumColor.fromId(nbt.getByte("color"));
        return null;
    }

    public static ItemStack setItemColor(ItemStack stack, EnumColor color) {
        if (color == null) return stack;
        NBTTagCompound nbt = getItemData(stack);
        nbt.setByte("color", (byte) color.ordinal());
        return stack;
    }

    public static boolean isSynthetic(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.hasKey("synthetic");
    }

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

    @Nonnull
    public static NBTTagCompound getItemData(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        return nbt;
    }

    @Nonnull
    public static NBTTagCompound getItemDataRailcraft(ItemStack stack) {
        NBTTagCompound nbt = getItemData(stack);
        NBTTagCompound railcraftTag;
        if (nbt.hasKey(Railcraft.getModId()))
            railcraftTag = nbt.getCompoundTag(Railcraft.getModId());
        else {
            railcraftTag = new NBTTagCompound();
            nbt.setTag(Railcraft.getModId(), railcraftTag);
        }
        return railcraftTag;
    }

    public static void addNBTTag(ItemStack stack, String key, String value) {
        NBTTagCompound nbt = getItemData(stack);
        nbt.setString(key, value);
    }

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

    public static void dropItem(ItemStack stack, World world, double x, double y, double z) {
        if (stack == null || stack.stackSize < 1)
            return;
        EntityItem entityItem = new EntityItem(world, x, y + 1.5, z, stack);
        entityItem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityItem);
    }

    public static void dropInventory(IInventory inv, World world, int x, int y, int z) {
        if (Game.isNotHost(world)) return;
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            spewItem(slot.getStackInSlot(), world, x, y, z);
            slot.setStackInSlot(null);
        }
    }

    public static void dropItems(Collection<ItemStack> items, World world, int x, int y, int z) {
        if (Game.isNotHost(world)) return;
        for (ItemStack stack : items) {
            spewItem(stack, world, x, y, z);
        }
    }

    private static void spewItem(ItemStack stack, World world, int x, int y, int z) {
        if (stack != null) {
            float xOffset = MiscTools.getRand().nextFloat() * 0.8F + 0.1F;
            float yOffset = MiscTools.getRand().nextFloat() * 0.8F + 0.1F;
            float zOffset = MiscTools.getRand().nextFloat() * 0.8F + 0.1F;
            while (stack.stackSize > 0) {
                int numToDrop = MiscTools.getRand().nextInt(21) + 10;
                if (numToDrop > stack.stackSize)
                    numToDrop = stack.stackSize;
                ItemStack newStack = stack.copy();
                newStack.stackSize = numToDrop;
                stack.stackSize -= numToDrop;
                EntityItem entityItem = new EntityItem(world, (float) x + xOffset, (float) y + yOffset, (float) z + zOffset, newStack);
                float variance = 0.05F;
                entityItem.motionX = (float) MiscTools.getRand().nextGaussian() * variance;
                entityItem.motionY = (float) MiscTools.getRand().nextGaussian() * variance + 0.2F;
                entityItem.motionZ = (float) MiscTools.getRand().nextGaussian() * variance;
                world.spawnEntityInWorld(entityItem);
            }
        }
    }

    public static boolean isInventoryEmpty(IInventory inv, ForgeDirection side) {
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

    public static boolean isAccessibleInventoryEmpty(IInventory inv, ForgeDirection side) {
        return isInventoryEmpty(getInventory(inv, side));
    }

    public static boolean isAccessibleInventoryEmpty(IInventory inv) {
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && slot.canTakeStackFromSlot(stack))
                return false;
        }
        return true;
    }

    public static boolean isInventoryFull(IInventory inv, ForgeDirection side) {
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

    public static int countItems(IInventory inv, IStackFilter filter) {
        int count = 0;
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && filter.matches(stack))
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
     */
    public static int countItems(IInventory inv, ItemStack... filters) {
        return countItems(inv, new ArrayStackFilter(filters));
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
     * @param inv  The IIinventory to check
     * @param item The ItemStack to look for
     * @return true is exists
     */
    public static boolean containsItem(IInventory inv, ItemStack item) {
        return countItems(inv, item) > 0;
    }

    /**
     * Returns a map backed by an <code>ItemStackMap</code> that lists the total
     * number of each type of item in the inventory.
     *
     * @param inv The <code>IInventory</code> to generate the manifest for
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
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItem(IInventory source, IInventory dest) {
        return moveOneItem(source, dest, StackFilter.ALL);
    }

    /**
     * Attempts to move a single item from one inventory to another.
     *
     * @param filters ItemStack to match against
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItem(IInventory source, IInventory dest, ItemStack... filters) {
        return moveOneItem(source, dest, new ArrayStackFilter(filters));
    }

    /**
     * Attempts to move a single item from one inventory to another.
     *
     * @param filter an IStackFilter to match against
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItem(IInventory source, IInventory dest, IStackFilter filter) {
        InventoryManipulator imSource = InventoryManipulator.get(source);
        return imSource.moveItem(dest, filter);
    }

    /**
     * Attempts to move one item from a collection of inventories.
     *
     * @param filters ItemStack to match against
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
     * @param filter an IStackFilter to match against
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
     * @param filters ItemStack to match against
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
     * @param filters an ItemStack[] to exclude
     * @return null if nothing was moved, the stack moved otherwise
     */
    public static ItemStack moveOneItemExcept(IInventory source, IInventory dest, ItemStack... filters) {
        return moveOneItem(source, dest, new InvertedStackFilter(new ArrayStackFilter(filters)));
    }

    /**
     * Attempts to move one item from a collection of inventories.
     */
    public static ItemStack moveOneItemExcept(Collection<IInventory> sources, IInventory dest, ItemStack... filters) {
        for (IInventory inv : sources) {
            ItemStack moved = InvTools.moveOneItemExcept(inv, dest, filters);
            if (moved != null)
                return moved;
        }
        return null;
    }

    /**
     * Attempts to move one item to a collection of inventories.
     */
    public static ItemStack moveOneItemExcept(IInventory source, Collection<IInventory> destinations, ItemStack... filters) {
        for (IInventory dest : destinations) {
            ItemStack moved = InvTools.moveOneItemExcept(source, dest, filters);
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
        return a.stackTagCompound == null || a.stackTagCompound.equals(b.stackTagCompound);
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
    public static boolean isItemEqualSemiStrict(ItemStack a, ItemStack b) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;
        if (a.getItem() != b.getItem())
            return false;
        if (a.getItemDamage() != b.getItemDamage())
            return false;
        return a.stackTagCompound == null || a.stackTagCompound.equals(b.stackTagCompound);
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

    public static boolean isCartItemEqual(final ItemStack a, final ItemStack b, final boolean matchDamage) {
        if (!isItemEqual(a, b, matchDamage, false))
            return false;
        if (a.hasDisplayName() && !a.getDisplayName().equals(b.getDisplayName()))
            return false;
        return true;
    }

    /**
     * Returns true if the item is equal to any one of several possible matches.
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
     */
    public static boolean isItemEqual(ItemStack stack, Collection<ItemStack> matches) {
        for (ItemStack match : matches) {
            if (isItemEqual(stack, match))
                return true;
        }
        return false;
    }

    public static boolean isItemGreaterOrEqualThan(ItemStack stackA, ItemStack stackB) {
        if (!isItemEqual(stackA, stackB))
            return false;
        return stackA.stackSize >= stackB.stackSize;
    }

    public static boolean isItemLessThanOrEqualTo(ItemStack stackA, ItemStack stackB) {
        if (!isItemEqual(stackA, stackB))
            return false;
        return stackA.stackSize <= stackB.stackSize;
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

    /**
     * Removes a up to numItems worth of items from the inventory, not caring
     * about what the items are.
     */
    public static ItemStack[] removeItems(IInventory inv, int numItems) {
//        if (inv instanceof ISpecialInventory)
//            return ((ISpecialInventory) inv).extractItem(true, ForgeDirection.UNKNOWN, numItems);
        StandaloneInventory output = new StandaloneInventory(27);
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (numItems <= 0)
                break;
            ItemStack slot = inv.getStackInSlot(i);
            if (slot == null)
                continue;
            ItemStack removed = inv.decrStackSize(i, numItems);
            numItems -= removed.stackSize;
            ItemStack remainder = moveItemStack(removed, output);
            if (remainder != null) {
                moveItemStack(remainder, inv);
                numItems += remainder.stackSize;
                break;
            }
        }

        List<ItemStack> list = new LinkedList<ItemStack>();
        for (ItemStack stack : output.getContents()) {
            if (stack != null)
                list.add(stack);
        }
        return list.toArray(new ItemStack[0]);
    }

    /**
     * Removes and returns a single item from the inventory.
     *
     * @param inv The inventory
     * @return An ItemStack
     */
    public static ItemStack removeOneItem(IInventory inv) {
        return removeOneItem(inv, StackFilter.ALL);
    }

    /**
     * Removes and returns a single item from the inventory that matches the
     * filter.
     *
     * @param inv    The inventory
     * @param filter ItemStack to match against
     * @return An ItemStack
     */
    public static ItemStack removeOneItem(IInventory inv, ItemStack... filter) {
        return removeOneItem(inv, new ArrayStackFilter(filter));
    }

    /**
     * Removes and returns a single item from the inventory that matches the
     * filter.
     *
     * @param inv    The inventory
     * @param filter EnumItemType to match against
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
     * @param filter EnumItemType to match against
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
     * @return true if there are enough items that can be removed, false
     * otherwise.
     */
    public static boolean removeItemsAbsolute(IInventory inv, int amount, ItemStack... filter) {
        return removeItemsAbsolute(inv, amount, new ArrayStackFilter(filter));
    }

    /**
     * Removes a specified number of items matching the filter, but only if the
     * operation can be completed. If the function returns false, the inventory
     * will not be modified.
     *
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
     * @param inv    The inventory
     * @param filter IStackFilter to match against
     * @return An ItemStack
     */
    public static ItemStack findMatchingItem(IInventory inv, IStackFilter filter) {
        InventoryManipulator im = InventoryManipulator.get(inv);
        return im.tryRemoveItem(filter);
    }

    /**
     * Returns all items from the inventory that match the
     * filter, but does not remove them.
     * The resulting set will be populated with a single instance of each item type.
     *
     * @param inv    The inventory
     * @param filter EnumItemType to match against
     * @return A Set of ItemStacks
     */
    public static Set<ItemStack> findMatchingItems(IInventory inv, IStackFilter filter) {
        Set<ItemStack> items = new ItemStackSet();
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && stack.stackSize > 0 && filter.matches(stack)) {
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
        if (stack == null || block == null)
            return false;
        if (stack.getItem() instanceof ItemBlock)
            return ((ItemBlock) stack.getItem()).field_150939_a == block;
        return false;
    }

    public static Block getBlockFromStack(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock)
            return ((ItemBlock) stack.getItem()).field_150939_a;
        return null;
    }
}
