/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import mods.railcraft.api.core.RailcraftFakePlayer;
import mods.railcraft.api.core.items.IFilterItem;
import mods.railcraft.api.core.items.InvToolsAPI;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.collections.StackKey;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.filters.StandardStackFilters;
import mods.railcraft.common.util.inventory.iterators.IExtInvSlot;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import mods.railcraft.common.util.inventory.wrappers.IInventoryComposite;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.inventory.wrappers.InventoryComposite;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public abstract class InvTools {
    private static final String TAG_SLOT = "Slot";

    @Contract("null -> true; !null -> _;")
    public static boolean isEmpty(@Nullable ItemStack stack) {
        return InvToolsAPI.isEmpty(stack);
    }

    @Nullable
    public static ItemStack emptyStack() {
        return InvToolsAPI.emptyStack();
    }

    public static int sizeOf(@Nullable ItemStack stack) {
        if (isEmpty(stack))
            return 0;
        return stack.stackSize;
    }

    @Contract("!null, _ -> !null; null, _ -> null")
    @Nullable
    public static ItemStack setSize(@Nullable ItemStack stack, int size) {
        if (isEmpty(stack))
            return emptyStack();
        stack.stackSize = size;
        return stack;
    }

    public static String toString(@Nullable ItemStack stack) {
        if (isEmpty(stack)) return "null";
        return stack.toString();
    }

    @Nullable
    @Contract("null,_,_->null")
    public static ItemStack makeStack(@Nullable Item item, int qty, int meta) {
        if (item != null)
            return new ItemStack(item, qty, meta);
        return emptyStack();
    }

    @SuppressWarnings("unused")
    @Nullable
    @Contract("null,_,_->null")
    public static ItemStack makeStack(@Nullable Block block, int qty, int meta) {
        if (block != null)
            return new ItemStack(block, qty, meta);
        return emptyStack();
    }

    @Contract("null -> null")
    @Nullable
    public static ItemStack makeSafe(@Nullable ItemStack stack) {
        if (isEmpty(stack))
            return emptyStack();
        return stack;
    }

    @SuppressWarnings("unused")
    public static InventoryComposite getAdjacentInventories(World world, BlockPos pos) {
        return getAdjacentInventories(world, pos, null);
    }

    @Nonnull
    public static InventoryComposite getAdjacentInventories(World world, BlockPos pos, @Nullable Class<? extends TileEntity> type) {
        InventoryComposite list = InventoryComposite.make();
        for (EnumFacing side : EnumFacing.VALUES) {
            IInventoryObject inv = InventoryFactory.get(world, pos, side, type, null);
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
//            IInventory inv = get(world, i, j, k, EnumFacing.VALUES[side], type, null);
//            if (inv != null)
//                map.put(side, inv);
//        }
//        return map;
//    }

    @Nullable
    public static IItemHandler getItemHandler(@Nullable Object obj) {
        if (obj instanceof ICapabilityProvider && ((ICapabilityProvider) obj).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            return ((ICapabilityProvider) obj).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        }
        return null;
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

    @Nonnull
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

    @Nullable
    public static ItemStack depleteItem(ItemStack stack) {
        if (stack.stackSize == 1)
            return stack.getItem().getContainerItem(stack);
        else {
            stack.splitStack(1);
            return stack;
        }
    }

    @Nullable
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
            stack = emptyStack();
        return stack;
    }

    public static void dropItem(@Nullable ItemStack stack, World world, BlockPos pos) {
        dropItem(stack, world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static void dropItem(@Nullable ItemStack stack, World world, double x, double y, double z) {
        if (isEmpty(stack))
            return;
        EntityItem entityItem = new EntityItem(world, x, y + 1.5, z, stack);
        entityItem.setDefaultPickupDelay();
        world.spawnEntityInWorld(entityItem);
    }

    public static void dropInventory(IInventory inv, World world, BlockPos pos) {
        if (Game.isClient(world)) return;
        for (IExtInvSlot slot : InventoryIterator.getVanilla(inv)) {
            spewItem(slot.getStack(), world, pos);
            slot.clear();
        }
    }

    public static void dropItems(Collection<ItemStack> items, World world, BlockPos pos) {
        if (Game.isClient(world)) return;
        for (ItemStack stack : items) {
            spewItem(stack, world, pos);
        }
    }

    public static void spewItem(@Nullable ItemStack stack, World world, BlockPos pos) {
        spewItem(stack, world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static void spewItem(@Nullable ItemStack stack, World world, double x, double y, double z) {
        if (!isEmpty(stack)) {
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
                EntityItem entityItem = new EntityItem(world, x + xOffset, y + yOffset, z + zOffset, newStack);
                float variance = 0.05F;
                entityItem.motionX = (float) MiscTools.RANDOM.nextGaussian() * variance;
                entityItem.motionY = (float) MiscTools.RANDOM.nextGaussian() * variance + 0.2F;
                entityItem.motionZ = (float) MiscTools.RANDOM.nextGaussian() * variance;
                world.spawnEntityInWorld(entityItem);
            }
        }
    }

    public static void validateInventory(IInventory inv, int slot, World world, BlockPos pos, Predicate<ItemStack> canStay) {
        ItemStack stack = inv.getStackInSlot(slot);
        if (!isEmpty(stack) && !canStay.test(stack)) {
            inv.setInventorySlotContents(slot, null);
            dropItem(stack, world, pos);
        }
    }

    public static void validateInventory(IInventory inv, World world, BlockPos pos) {
        for (IExtInvSlot slot : InventoryIterator.getVanilla(inv)) {
            ItemStack stack = slot.getStack();
            if (stack != null && !inv.isItemValidForSlot(slot.getIndex(), stack)) {
                slot.setStack(null);
                dropItem(stack, world, pos);
            }
        }
    }

    public static boolean isInventoryEmpty(IInventoryComposite inv) {
        for (IInventoryObject inventoryObject : inv) {
            for (IInvSlot slot : InventoryIterator.getRailcraft(inventoryObject)) {
                if (slot.hasStack())
                    return false;
            }
        }
        return true;
    }

    public static boolean isInventoryFull(IInventoryComposite inv) {
        return !hasEmptySlot(inv);
    }

    public static boolean hasEmptySlot(IInventoryComposite inv) {
        for (IInventoryObject inventoryObject : inv) {
            for (IInvSlot slot : InventoryIterator.getRailcraft(inventoryObject)) {
                if (!slot.hasStack())
                    return true;
            }
        }
        return false;
    }

    public static int countMaxItemStackSize(IInventoryComposite inv) {
        int count = 0;
        for (IInventoryObject inventoryObject : inv) {
            for (IInvSlot slot : InventoryIterator.getRailcraft(inventoryObject)) {
                ItemStack stack = slot.getStack();
                if (!isEmpty(stack))
                    count += stack.getMaxStackSize();
            }
        }
        return count;
    }

    /**
     * Counts the number of items.
     *
     * @param inv the inventory
     * @return the number of items in the inventory
     */
    public static int countItems(IInventoryComposite inv) {
        return countItems(inv, StandardStackFilters.ALL);
    }

    public static int countItems(IInventoryComposite inv, Predicate<ItemStack> filter) {
        int count = 0;
        for (IInventoryObject inventoryObject : inv) {
            count += InventoryIterator.getRailcraft(inventoryObject).getStackStream()
                    .filter(filter)
                    .mapToInt(s -> s.stackSize)
                    .sum();
        }
        return count;
    }

    public static boolean numItemsMoreThan(IInventoryComposite inv, int amount) {
        int count = 0;
        for (IInventoryObject inventoryObject : inv) {
            for (IInvSlot slot : InventoryIterator.getRailcraft(inventoryObject)) {
                ItemStack stack = slot.getStack();
                if (!isEmpty(stack))
                    count += stack.stackSize;
                if (count >= amount)
                    return true;
            }
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
    public static int countItems(IInventoryComposite inv, ItemStack... filters) {
        return countItems(inv, StackFilters.anyOf(filters));
    }

    public static int countStacks(IInventoryComposite inv) {
        return countStacks(inv, StandardStackFilters.ALL);
    }

    public static int countStacks(IInventoryComposite inv, Predicate<ItemStack> filter) {
        int count = 0;
        for (IInventoryObject inventoryObject : inv) {
            for (ItemStack stack : InventoryIterator.getRailcraft(inventoryObject).getStacks()) {
                if (filter.test(stack))
                    count++;
            }
        }
        return count;
    }

    /**
     * Returns true if the inventory contains any of the specified items.
     *
     * @param inv   the inventory  The inventory to check
     * @param items The ItemStack to look for
     * @return true is exists
     */
    public static boolean containsItem(IInventoryComposite inv, ItemStack... items) {
        return containsItem(inv, StackFilters.anyOf(items));

    }

    /**
     * Returns true if the inventory contains the specified item.
     *
     * @param inv    the inventory  The inventory to check
     * @param filter The ItemStack to look for
     * @return true is exists
     */
    public static boolean containsItem(IInventoryComposite inv, Predicate<ItemStack> filter) {
        for (IInventoryObject inventoryObject : inv) {
            for (IInvSlot slot : InventoryIterator.getRailcraft(inventoryObject)) {
                ItemStack stack = slot.getStack();
                if (!isEmpty(stack) && filter.test(stack))
                    return true;
            }
        }
        return false;
    }

    private static void populateManifest(Multiset<StackKey> manifest, IInventoryObject inv) {
        for (ItemStack stack : InventoryIterator.getRailcraft(inv).getStacks()) {
            StackKey key = StackKey.make(stack);
            manifest.add(key, stack.stackSize);
        }
    }

    /**
     * Returns a Multiset that lists the total
     * number of each type of item in the inventory.
     *
     * @param invs the inventories to generate the manifest for
     * @return A <code>Multiset</code> that lists how many of each item is in the inventories
     */
    @Nonnull
    public static Multiset<StackKey> createManifest(IInventoryComposite invs) {
        Multiset<StackKey> manifest = HashMultiset.create();
        invs.forEach(inv -> populateManifest(manifest, inv));
        return manifest;
    }

    /**
     * Returns a Multiset that lists the total
     * number of each type of item in the inventory.
     *
     * @param invs the inventories to generate the manifest for
     * @return A <code>Multiset</code> that lists how many of each item is in the inventories
     */
    @Nonnull
    public static Multiset<StackKey> createManifest(IInventoryComposite invs, Collection<StackKey> manifestEntries) {
        Multiset<StackKey> manifest = HashMultiset.create();
        for (StackKey entry : manifestEntries) {
            Predicate<ItemStack> filter = StackFilters.matches(entry.get());
            invs.forEach(inv -> {
                for (ItemStack stack : InventoryIterator.getRailcraft(inv).getStacks()) {
                    if (filter.test(stack))
                        manifest.add(entry, stack.stackSize);
                }
            });
        }
        return manifest;
    }

    /**
     * Attempts to move a single item from one inventory to another.
     *
     * @param source  the source inventory
     * @param dest    the destination inventory
     * @param filters ItemStack to match against
     * @return null if nothing was moved, the stack moved otherwise
     */
    @Nullable
    @Deprecated
    public static ItemStack moveOneItem(IInventoryComposite source, IInventoryComposite dest, ItemStack... filters) {
        return moveOneItem(source, dest, StackFilters.anyOf(filters));
    }

    /**
     * Attempts to move a single item from one inventory to another.
     *
     * @param source the source inventory
     * @param dest   the destination inventory
     * @param filter Predicate to match against
     * @return null if nothing was moved, the stack moved otherwise
     */
    @Nullable
    public static ItemStack moveOneItem(IInventoryComposite source, IInventoryComposite dest, Predicate<ItemStack> filter) {
        for (IInventoryObject src : source) {
            for (IInventoryObject dst : dest) {
                InventoryManipulator imSource = InventoryManipulator.get(src);
                ItemStack moved = imSource.moveItem(dst, filter);
                if (!isEmpty(moved))
                    return moved;
            }
        }
        return emptyStack();
    }

    /**
     * Attempts to move a single item from one inventory to another.
     * <p/>
     * Will not move any items in the filter.
     *
     * @param source the source inventory
     * @param dest   the destination inventory
     * @param filter ItemStacks to exclude
     * @return null if nothing was moved, the stack moved otherwise
     */
    @Nullable
    public static ItemStack moveOneItemExcept(IInventoryComposite source, IInventoryComposite dest, Predicate<ItemStack> filter) {
        return moveOneItem(source, dest, filter.negate());
    }

    public static boolean isWildcard(ItemStack stack) {
        return isWildcard(stack.getItemDamage());
    }

    public static boolean isWildcard(int damage) {
        return damage == -1 || damage == OreDictionary.WILDCARD_VALUE;
    }

    @Contract("null,_->false;")
    public static boolean isItem(@Nullable ItemStack stack, @Nullable Item item) {
        return isEmpty(stack) && item != null && stack.getItem() == item;
    }

    @Contract("null,_->false;")
    public static boolean isItemClass(@Nullable ItemStack stack, @Nonnull Class<? extends Item> itemClass) {
        return !isEmpty(stack) && stack.getItem().getClass() == itemClass;
    }

    @Contract("null,_->false;")
    public static boolean extendsItemClass(@Nullable ItemStack stack, @Nonnull Class<? extends Item> itemClass) {
        return isEmpty(stack) && itemClass.isAssignableFrom(stack.getItem().getClass());
    }

    public static boolean matchesFilter(@Nullable ItemStack filter, @Nullable ItemStack stack) {
        if (stack == null || filter == null)
            return false;
        if (filter.getItem() instanceof IFilterItem) {
            return ((IFilterItem) filter.getItem()).matches(filter, stack);
        }
        return isItemEqual(stack, filter);
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
    @Contract("null,null->true;null,!null->false;!null,null->false;")
    public static boolean isItemEqualStrict(@Nullable ItemStack a, @Nullable ItemStack b) {
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
        return a.getTagCompound() == null || b.getTagCompound() == null || a.getTagCompound().equals(b.getTagCompound());
    }

    /**
     * A more robust item comparison function.
     * <p/>
     * Does not compare stackSize.
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
    @Contract("null,null->true;null,!null->false;!null,null->false;")
    public static boolean isItemEqualSemiStrict(@Nullable ItemStack a, @Nullable ItemStack b) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;
        if (a.getItem() != b.getItem())
            return false;
        if (a.getItemDamage() != b.getItemDamage())
            return false;
        return a.getTagCompound() == null || b.getTagCompound() == null || a.getTagCompound().equals(b.getTagCompound());
    }

    /**
     * A more robust item comparison function. Supports items with damage = -1
     * matching any sub-type.
     *
     * @param a An ItemStack
     * @param b An ItemStack
     * @return True if equal
     */
    @Contract("null,_ -> false;_,null -> false;")
    public static boolean isItemEqual(@Nullable ItemStack a, @Nullable ItemStack b) {
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
    @Contract("null,_ -> false;_,null -> false;")
    public static boolean isItemEqualIgnoreNBT(@Nullable ItemStack a, @Nullable ItemStack b) {
        return isItemEqual(a, b, true, false);
    }

    @Contract("null,_,_,_ -> false;_,null,_,_ -> false;")
    public static boolean isItemEqual(@Nullable final ItemStack a, @Nullable final ItemStack b, final boolean matchDamage, final boolean matchNBT) {
        if (isEmpty(a) || isEmpty(b))
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
    public static boolean isCartItemEqual(@Nullable final ItemStack a, @Nullable final ItemStack b, final boolean matchDamage) {
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
    public static boolean isItemEqual(@Nullable ItemStack stack, ItemStack... matches) {
        return Arrays.stream(matches).anyMatch(match -> isItemEqual(stack, match));
    }

    /**
     * Returns true if the item is equal to any one of several possible matches.
     *
     * @param stack   the ItemStack to test
     * @param matches the ItemStacks to test against
     * @return true if a match is found
     */
    public static boolean isItemEqual(@Nullable ItemStack stack, Collection<ItemStack> matches) {
        return matches.stream().anyMatch(match -> isItemEqual(stack, match));
    }

    @Contract("null,_ -> false;_,null -> false;")
    public static boolean isItemGreaterOrEqualThan(@Nullable ItemStack stackA, @Nullable ItemStack stackB) {
        return isItemEqual(stackA, stackB) && stackA.stackSize >= stackB.stackSize;
    }

    @Contract("null,_ -> false;_,null -> false;")
    public static boolean isItemLessThanOrEqualTo(@Nullable ItemStack stackA, @Nullable ItemStack stackB) {
        return isItemEqual(stackA, stackB) && stackA.stackSize <= stackB.stackSize;
    }

    /**
     * Places an ItemStack in a destination Inventory. Will attempt to move as
     * much of the stack as possible, returning any remainder.
     *
     * @param stack The ItemStack to put in the inventory.
     * @param dest  The destination IInventories.
     * @return Null if itemStack was completely moved, a new itemStack with
     * remaining stackSize if part or none of the stack was moved.
     */
    @SuppressWarnings("unused")
    @Nullable
    public static ItemStack moveItemStack(ItemStack stack, IInventoryComposite dest) {
        for (IInventoryObject inv : dest) {
            InventoryManipulator im = InventoryManipulator.get(inv);
            stack = im.addStack(stack);
            if (isEmpty(stack))
                return emptyStack();
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
    @Contract("null,_ -> false;")
    public static boolean isRoomForStack(@Nullable ItemStack stack, IInventoryComposite dest) {
        return !isEmpty(stack) && dest.stream().anyMatch(inv -> {
            InventoryManipulator im = InventoryManipulator.get(inv);
            return im.canAddStack(stack);
        });
    }

    /**
     * Checks if inventory will accept the ItemStack.
     *
     * @param stack The ItemStack
     * @param dest  The IInventory
     * @return true if room for stack
     */
    @Contract("null,_ -> false;")
    public static boolean acceptsItemStack(@Nullable ItemStack stack, IInventoryComposite dest) {
        if (isEmpty(stack))
            return false;
        ItemStack newStack = stack.copy();
        newStack.stackSize = 1;
        for (IInventoryObject inv : dest) {
            for (IInvSlot slot : InventoryIterator.getRailcraft(inv)) {
                if (slot.canPutStackInSlot(stack))
                    return true;
            }
        }
        return false;
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
    @Nullable
    public static ItemStack removeOneItem(IInventoryComposite inv) {
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
    @Nullable
    public static ItemStack removeOneItem(IInventoryComposite inv, ItemStack... filter) {
        return removeOneItem(inv, StackFilters.anyOf(filter));
    }

    /**
     * Removes and returns a single item from the inventory that matches the
     * filter.
     *
     * @param invs   The inventories
     * @param filter the filter to match against
     * @return An ItemStack
     */
    @Nullable
    public static ItemStack removeOneItem(IInventoryComposite invs, Predicate<ItemStack> filter) {
        for (IInventoryObject inv : invs) {
            InventoryManipulator im = InventoryManipulator.get(inv);
            ItemStack stack = im.removeItem(filter);
            if (!isEmpty(stack))
                return stack;
        }
        return emptyStack();
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
    public static boolean removeItemsAbsolute(IInventoryObject inv, int amount, ItemStack... filter) {
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
    public static boolean removeItemsAbsolute(IInventoryObject inv, int amount, Predicate<ItemStack> filter) {
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
    @Nullable
    public static ItemStack findMatchingItem(IInventoryComposite inv, Predicate<ItemStack> filter) {
        for (IInventoryObject inventoryObject : inv) {
            InventoryManipulator im = InventoryManipulator.get(inventoryObject);
            ItemStack removed = im.tryRemoveItem(filter);
            if (!isEmpty(removed))
                return removed;
        }
        return emptyStack();
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
    @Nonnull
    public static Set<StackKey> findMatchingItems(IInventoryComposite inv, Predicate<ItemStack> filter) {
        Set<StackKey> items = CollectionTools.createItemStackSet();
        for (IInventoryObject inventoryObject : inv) {
            for (IInvSlot slot : InventoryIterator.getRailcraft(inventoryObject)) {
                ItemStack stack = slot.getStack();
                if (!isEmpty(stack) && filter.test(stack)) {
                    stack = stack.copy();
                    stack.stackSize = 1;
                    items.add(StackKey.make(stack));
                }
            }
        }
        return items;
    }

    public static void writeInvToNBT(IInventory inv, String tag, NBTTagCompound data) {
        NBTTagList list = new NBTTagList();
        for (byte slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (!isEmpty(stack)) {
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

    public static void writeItemToNBT(@Nullable ItemStack stack, NBTTagCompound data) {
        if (isEmpty(stack))
            return;
        if (stack.stackSize > 127)
            stack.stackSize = 127;
        stack.writeToNBT(data);
    }

    @Nullable
    public static ItemStack readItemFromNBT(NBTTagCompound data) {
        return ItemStack.loadItemStackFromNBT(data);
    }

    public static boolean isStackEqualToBlock(@Nullable ItemStack stack, @Nullable Block block) {
        return !(isEmpty(stack) || block == null) && stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == block;
    }

    @Nullable
    @Contract("null->null")
    public static Block getBlockFromStack(@Nullable ItemStack stack) {
        if (!isEmpty(stack) && stack.getItem() instanceof ItemBlock)
            return ((ItemBlock) stack.getItem()).getBlock();
        return null;
    }

    @Nullable
    @Contract("null->null")
    public static IBlockState getBlockStateFromStack(@Nullable ItemStack stack) {
        if (isEmpty(stack))
            return null;
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            int meta = item.getMetadata(stack.getMetadata());
            return ((ItemBlock) item).getBlock().getStateFromMeta(meta);
        }
        return null;
    }

    @Nullable
    @Contract("null,_,_->null")
    public static IBlockState getBlockStateFromStack(@Nullable ItemStack stack, World world, BlockPos pos) {
        if (isEmpty(stack))
            return null;
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            int meta = item.getMetadata(stack.getMetadata());
            if (world instanceof WorldServer)
                return ((ItemBlock) item).getBlock().onBlockPlaced(world, pos, EnumFacing.UP, 0.5F, 0.5F, 0.5F, meta, RailcraftFakePlayer.get((WorldServer) world, pos.up()));
            else
                return ((ItemBlock) item).getBlock().getStateFromMeta(meta);
        }
        return null;
    }

    /**
     * @see Container#calcRedstoneFromInventory(IInventory)
     */
    public static int calcRedstoneFromInventory(@Nullable InventoryComposite inv) {
        if (inv == null)
            return 0;
        int numStacks = 0;
        float average = 0.0F;

        for (IInventoryObject inventoryObject : inv) {
            int stackLimit = inventoryObject.getBackingObject() instanceof IInventory ? ((IInventory) inventoryObject.getBackingObject()).getInventoryStackLimit() : 64;
            for (ItemStack stack : InventoryIterator.getRailcraft(inventoryObject).getStacks()) {
                average += (float) stack.stackSize / (float) Math.min(stackLimit, stack.getMaxStackSize());
                numStacks++;
            }
        }

        average = average / (float) inv.slotCount();
        return MathHelper.floor_float(average * 14.0F) + (numStacks > 0 ? 1 : 0);
    }
}
