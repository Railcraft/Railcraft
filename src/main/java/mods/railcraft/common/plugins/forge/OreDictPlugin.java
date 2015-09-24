/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class OreDictPlugin {

    public static boolean isOreType(String oreName, ItemStack stack) {
        List<ItemStack> ores = OreDictionary.getOres(oreName);
        for (ItemStack ore : ores) {
            if (InvTools.isItemEqual(ore, stack))
                return true;
        }
        return false;
    }

    public static ItemStack getOre(String name, int qty) {
        List<ItemStack> ores = OreDictionary.getOres(name);
        for (ItemStack ore : ores) {
            if (!InvTools.isWildcard(ore)) {
                ore = ore.copy();
                ore.stackSize = Math.min(qty, ore.getMaxStackSize());
                return ore;
            }
        }
        return null;
    }

    public static boolean oreExists(String name) {
        return !OreDictionary.getOres(name).isEmpty();
    }

    public static Set<Block> getOreBlocks() {
        Set<Block> ores = new HashSet<Block>();
        String[] names = OreDictionary.getOreNames();
        for (String name : names) {
            if (name.startsWith("ore"))
                for (ItemStack stack : OreDictionary.getOres(name)) {
                    if (stack.getItem() instanceof ItemBlock)
                        ores.add(InvTools.getBlockFromStack(stack));
                }
        }
        return ores;
    }

}
