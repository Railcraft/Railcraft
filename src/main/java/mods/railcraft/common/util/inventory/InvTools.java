/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory;

import mods.railcraft.api.core.RailcraftFakePlayer;
import mods.railcraft.api.items.IFilterItem;
import mods.railcraft.api.items.InvToolsAPI;
import mods.railcraft.common.util.inventory.iterators.IExtInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public abstract class InvTools {
    public static final String TAG_SLOT = "Slot";

    @Contract("null -> true; !null -> _;")
    public static boolean isEmpty(@Nullable ItemStack stack) {
        return InvToolsAPI.isEmpty(stack);
    }

    @Contract("null -> false; !null -> _;")
    public static boolean nonEmpty(@Nullable ItemStack stack) {
        return !InvToolsAPI.isEmpty(stack);
    }

    public static ItemStack emptyStack() {
        return InvToolsAPI.emptyStack();
    }

    public static int sizeOf(ItemStack stack) {
        if (isEmpty(stack))
            return 0;
        return stack.getCount();
    }

    public static ItemStack setSize(ItemStack stack, int size) {
        if (isEmpty(stack))
            return emptyStack();
        stack.setCount(size);
        return stack;
    }

    public static ItemStack incSize(@Nullable ItemStack stack, int size) {
        if (isEmpty(stack))
            return emptyStack();
        stack.grow(size);
        return stack;
    }

    public static ItemStack decSize(@Nullable ItemStack stack, int size) {
        if (isEmpty(stack))
            return emptyStack();
        stack.shrink(size);
        return stack;
    }

    public static ItemStack inc(@Nullable ItemStack stack) {
        if (isEmpty(stack))
            return emptyStack();
        stack.grow(1);
        return stack;
    }

    public static ItemStack dec(@Nullable ItemStack stack) {
        if (isEmpty(stack))
            return emptyStack();
        stack.shrink(1);
        return stack;
    }

    public static String toString(@Nullable ItemStack stack) {
        if (isEmpty(stack)) return "ItemStack.EMPTY";
        return stack.toString();
    }

    public static ItemStack makeStack(@Nullable Item item, int qty, int meta) {
        if (item != null)
            return new ItemStack(item, qty, meta);
        return emptyStack();
    }

    public static ItemStack makeStack(@Nullable Block block, int qty, int meta) {
        if (block != null)
            return new ItemStack(block, qty, meta);
        return emptyStack();
    }

    public static ItemStack makeSafe(@Nullable ItemStack stack) {
        if (isEmpty(stack))
            return emptyStack();
        return stack;
    }

    public static ItemStack copy(ItemStack stack) {
        return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }

    public static ItemStack copy(ItemStack stack, int newSize) {
        ItemStack ret = copy(stack);
        if (!isEmpty(ret))
            ret.setCount(newSize);
        return ret;
    }

    public static ItemStack copyOne(ItemStack stack) {
        return copy(stack, 1);
    }

    public static boolean canMerge(ItemStack target, ItemStack source) {
        return target.isEmpty() || (isItemEqual(target, source) && target.getCount() + source.getCount() <= target.getMaxStackSize());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canMerge(ItemStack target, ItemStack source, int slotLimit) {
        return canMerge(target, source) && target.getCount() + source.getCount() <= slotLimit;
    }

    public static int[] buildSlotArray(int start, int size) {
        return IntStream.range(0, size).map(i -> start + i).toArray();
    }

//    @Deprecated
//    public static boolean isSynthetic(ItemStack stack) {
//        NBTTagCompound nbt = stack.getTagCompound();
//        return nbt != null && nbt.hasKey("synthetic");
//    }
//
//    @SuppressWarnings("unused")
//    public static void markItemSynthetic(ItemStack stack) {
//        NBTTagCompound nbt = getItemData(stack);
//        nbt.setBoolean("synthetic", true);
//        NBTTagCompound display = nbt.getCompoundTag("display");
//        nbt.setTag("display", display);
//        NBTTagList lore = display.getTagList("Lore", 8);
//        display.setTag("Lore", lore);
//        lore.appendTag(new NBTTagString("\u00a77\u00a7o" + LocalizationPlugin.translate("item.synthetic")));
//    }

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

    public static void addTagString(ItemStack stack, String key, String value) {
        NBTTagCompound nbt = getItemData(stack);
        nbt.setString(key, value);
    }

    @SuppressWarnings("unused")
    public static void addTagInt(ItemStack stack, String key, int value) {
        NBTTagCompound nbt = getItemData(stack);
        nbt.setInteger(key, value);
    }

    public static void addTagBoolean(ItemStack stack, String key, boolean value) {
        NBTTagCompound nbt = getItemData(stack);
        nbt.setBoolean(key, value);
    }

    public static boolean getTagBoolean(ItemStack stack, String key) {
        NBTTagCompound nbt = getItemData(stack);
        return nbt.getBoolean(key);
    }

    public static void addTagDouble(ItemStack stack, String key, double value) {
        NBTTagCompound nbt = getItemData(stack);
        nbt.setDouble(key, value);
    }

    public static double getTagDouble(ItemStack stack, String key) {
        NBTTagCompound nbt = getItemData(stack);
        return nbt.getDouble(key);
    }

    public static ItemStack depleteItem(ItemStack stack) {
        if (sizeOf(stack) == 1)
            return stack.getItem().getContainerItem(stack);
        else {
            stack.splitStack(1);
            return stack;
        }
    }

    public static ItemStack damageItem(ItemStack stack, int damage) {
        return damageItem(stack, damage, null);
    }

    public static ItemStack damageItem(ItemStack stack, int damage, @Nullable EntityPlayerMP owner) {
        return stack.attemptDamageItem(damage, owner == null ? MiscTools.RANDOM : owner.getRNG(), owner) ? emptyStack() : stack;
    }

    public static void dropItem(@Nullable ItemStack stack, World world, BlockPos pos) {
        dropItem(stack, world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static void dropItem(@Nullable ItemStack stack, World world, double x, double y, double z) {
        if (isEmpty(stack))
            return;
        EntityItem entityItem = new EntityItem(world, x, y + 1.5, z, stack);
        entityItem.setDefaultPickupDelay();
        world.spawnEntity(entityItem);
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
            while (!isEmpty(stack)) {
                int numToDrop = MiscTools.RANDOM.nextInt(21) + 10;
                if (numToDrop > sizeOf(stack))
                    numToDrop = sizeOf(stack);
                ItemStack newStack = stack.copy();
                setSize(newStack, numToDrop);
                decSize(stack, numToDrop);
                EntityItem entityItem = new EntityItem(world, x + xOffset, y + yOffset, z + zOffset, newStack);
                float variance = 0.05F;
                entityItem.motionX = (float) MiscTools.RANDOM.nextGaussian() * variance;
                entityItem.motionY = (float) MiscTools.RANDOM.nextGaussian() * variance + 0.2F;
                entityItem.motionZ = (float) MiscTools.RANDOM.nextGaussian() * variance;
                world.spawnEntity(entityItem);
            }
        }
    }

    public static void validateInventory(IInventory inv, int slot, World world, BlockPos pos, Predicate<ItemStack> canStay) {
        ItemStack stack = inv.getStackInSlot(slot);
        if (!isEmpty(stack) && !canStay.test(stack)) {
            inv.setInventorySlotContents(slot, emptyStack());
            dropItem(stack, world, pos);
        }
    }

    public static void validateInventory(IInventory inv, World world, BlockPos pos) {
        for (IExtInvSlot slot : InventoryIterator.getVanilla(inv)) {
            ItemStack stack = slot.getStack();
            if (!isEmpty(stack) && !inv.isItemValidForSlot(slot.getIndex(), stack)) {
                slot.clear();
                dropItem(stack, world, pos);
            }
        }
    }

    public static boolean isWildcard(ItemStack stack) {
        return isWildcard(stack.getItemDamage());
    }

    public static boolean isWildcard(int damage) {
        return damage == -1 || damage == OreDictionary.WILDCARD_VALUE;
    }

    @Contract("_,null->false;")
    public static boolean isItem(ItemStack stack, @Nullable Item item) {
        return !isEmpty(stack) && item != null && stack.getItem() == item;
    }

    public static boolean isItemClass(ItemStack stack, Class<? extends Item> itemClass) {
        return !isEmpty(stack) && stack.getItem().getClass() == itemClass;
    }

    public static boolean extendsItemClass(ItemStack stack, Class<? extends Item> itemClass) {
        return isEmpty(stack) && itemClass.isAssignableFrom(stack.getItem().getClass());
    }

    public static boolean matchesFilter(ItemStack filter, ItemStack stack) {
        if (isEmpty(stack) || isEmpty(filter))
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
    @Contract("null,null->true")
    public static boolean isItemEqualStrict(@Nullable ItemStack a, @Nullable ItemStack b) {
        if (isEmpty(a) && isEmpty(b))
            return true;
        if (isEmpty(a) || isEmpty(b))
            return false;
        if (a.getItem() != b.getItem())
            return false;
        if (sizeOf(a) != sizeOf(b))
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
    public static boolean isItemEqual(final @Nullable ItemStack a, final @Nullable ItemStack b, final boolean matchDamage, final boolean matchNBT) {
        if (isEmpty(a) || isEmpty(b))
            return false;
        if (a.getItem() != b.getItem())
            return false;
        if (matchNBT && !ItemStack.areItemStackTagsEqual(a, b))
            return false;
        if (matchDamage && a.getHasSubtypes()) {
            if (isWildcard(a) || isWildcard(b))
                return true;
            return a.getItemDamage() == b.getItemDamage();
        }
        return true;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isCartItemEqual(final @Nullable ItemStack a, final @Nullable ItemStack b, final boolean matchDamage) {
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
        return isItemEqual(stackA, stackB) && sizeOf(stackA) >= sizeOf(stackB);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Contract("null,_ -> false;_,null -> false;")
    public static boolean isItemLessThanOrEqualTo(@Nullable ItemStack stackA, @Nullable ItemStack stackB) {
        return isItemEqual(stackA, stackB) && sizeOf(stackA) <= sizeOf(stackB);
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

    public static void writeItemToNBT(ItemStack stack, NBTTagCompound data) {
        if (isEmpty(stack))
            return;
        if (sizeOf(stack) > 127)
            setSize(stack, 127);
        stack.writeToNBT(data);
    }

    public static ItemStack readItemFromNBT(NBTTagCompound data) {
        return new ItemStack(data);
    }

    public static boolean isStackEqualToBlock(ItemStack stack, @Nullable Block block) {
        return !(isEmpty(stack) || block == null) && stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == block;
    }

    public static IBlockState getBlockStateFromStack(ItemStack stack) {
        if (isEmpty(stack))
            return Blocks.AIR.getDefaultState();
        Item item = stack.getItem();
        Block block = GameData.getBlockItemMap().inverse().get(item);
        //noinspection deprecation
        return block == null ? Blocks.AIR.getDefaultState() : block.getStateFromMeta(stack.getItemDamage());
    }

    public static @Nullable IBlockState getBlockStateFromStack(ItemStack stack, World world, BlockPos pos) {
        if (isEmpty(stack))
            return null;
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            int meta = item.getMetadata(stack.getMetadata());
            if (world instanceof WorldServer)
                return ((ItemBlock) item).getBlock().getStateForPlacement(world, pos, EnumFacing.UP, 0.5F, 0.5F, 0.5F, meta, RailcraftFakePlayer.get((WorldServer) world, pos.up()), EnumHand.MAIN_HAND);
                //TODO fix get state for placement for that hand
            else
                //noinspection deprecation
                return ((ItemBlock) item).getBlock().getStateFromMeta(meta);
        }
        return null;
    }

}
