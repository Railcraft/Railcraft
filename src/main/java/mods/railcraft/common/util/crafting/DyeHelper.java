/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DyeHelper {

    private static Multimap<EnumColor, ItemStack> dyes;

    public static Multimap<EnumColor, ItemStack> getDyes() {
        if (dyes == null) {
            dyes = LinkedListMultimap.create();
            for (EnumColor color : EnumColor.VALUES) {
                dyes.putAll(color, OreDictionary.getOres(EnumColor.DYES[color.ordinal()]));
            }
        }
        return dyes;
    }

}
