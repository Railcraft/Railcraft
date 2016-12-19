/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class OreDictPlugin {

    public static void registerNewTags() {
        OreDictionary.registerOre("gateWood", Blocks.ACACIA_FENCE_GATE);
        OreDictionary.registerOre("gateWood", Blocks.BIRCH_FENCE_GATE);
        OreDictionary.registerOre("gateWood", Blocks.DARK_OAK_FENCE_GATE);
        OreDictionary.registerOre("gateWood", Blocks.JUNGLE_FENCE_GATE);
        OreDictionary.registerOre("gateWood", Blocks.OAK_FENCE_GATE);
        OreDictionary.registerOre("gateWood", Blocks.SPRUCE_FENCE_GATE);
    }

    public static boolean isOreType(String oreName, ItemStack stack) {
        if (!oreExists(oreName))
            return false;
        int id = OreDictionary.getOreID(oreName);
        int[] stackIds = OreDictionary.getOreIDs(stack);
        return ArrayUtils.contains(stackIds, id);
//        List<ItemStack> ores = OreDictionary.getOres(oreName);
//        for (ItemStack ore : ores) {
//            if (InvTools.isItemEqual(ore, stack))
//                return true;
//        }
//        return false;
    }

    public static ItemStack getOre(String name, int qty) {
        List<ItemStack> ores = OreDictionary.getOres(name);
        for (ItemStack ore : ores) {
            if (!InvTools.isWildcard(ore)) {
                ore = ore.copy();
                ore.setCount(Math.min(qty, ore.getMaxStackSize()));
                return ore;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean oreExists(String name) {
        return OreDictionary.doesOreNameExist(name);
    }

    public static Set<Block> getOreBlocks() {
        String[] names = OreDictionary.getOreNames();
        return Arrays.stream(names)
                .filter(n -> n.startsWith("ore"))
                .flatMap(n -> OreDictionary.getOres(n).stream())
                .filter(stack -> stack.getItem() instanceof ItemBlock)
                .map(InvTools::getBlockFromStack)
                .collect(Collectors.toSet());
    }

}
