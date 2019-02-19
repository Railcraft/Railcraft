/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class OreDictPlugin {

    public static void registerNewTags() {
        OreDictionary.registerOre("gateWood", Blocks.ACACIA_FENCE_GATE);
        OreDictionary.registerOre("gateWood", Blocks.BIRCH_FENCE_GATE);
        OreDictionary.registerOre("gateWood", Blocks.DARK_OAK_FENCE_GATE);
        OreDictionary.registerOre("gateWood", Blocks.JUNGLE_FENCE_GATE);
        OreDictionary.registerOre("gateWood", Blocks.OAK_FENCE_GATE);
        OreDictionary.registerOre("gateWood", Blocks.SPRUCE_FENCE_GATE);
    }

    public static boolean hasOreType(String oreName, Iterable<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (isOreType(oreName, stack))
                return true;
        }
        return false;
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

    public static boolean matches(ItemStack prototype, ItemStack target) {
        int[] prototypeIds = OreDictionary.getOreIDs(prototype);
        int[] targetIds = OreDictionary.getOreIDs(target);
        return Arrays.stream(targetIds).anyMatch(id -> ArrayUtils.contains(prototypeIds, id));
    }

    public static List<String> getOreTags(ItemStack stack) {
        int[] ids = OreDictionary.getOreIDs(stack);
        return Arrays.stream(ids).mapToObj(OreDictionary::getOreName).collect(Collectors.toList());
    }

    public static ItemStack getOre(String name, int qty) {
        List<ItemStack> ores = OreDictionary.getOres(name);
        for (ItemStack ore : ores) {
            if (!InvTools.isWildcard(ore)) {
                ore = ore.copy();
                setSize(ore, Math.min(qty, ore.getMaxStackSize()));
                return ore;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean oreExists(String name) {
        return !OreDictionary.getOres(name, false).isEmpty();
    }

    public static Set<IBlockState> getOreBlockStates() {
        String[] names = OreDictionary.getOreNames();
        return Arrays.stream(names)
                .filter(n -> n.startsWith("ore"))
                .flatMap(n -> OreDictionary.getOres(n).stream())
                .map(InvTools::getBlockStateFromStack)
                .filter(Predicates.realBlock())
                .collect(Collectors.toSet());
    }

}
