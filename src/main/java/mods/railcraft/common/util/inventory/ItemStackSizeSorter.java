/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.item.ItemStack;

public class ItemStackSizeSorter implements Comparator<ItemStack> {

    private static ItemStackSizeSorter instance;

    private static ItemStackSizeSorter getInstance() {
        if (instance == null) {
            instance = new ItemStackSizeSorter();
        }
        return instance;
    }

    public static void sort(List<ItemStack> list) {
        Collections.sort(list, getInstance());
    }

    @Override
    public int compare(ItemStack o1, ItemStack o2) {
        return o1.stackSize - o2.stackSize;
    }

}
