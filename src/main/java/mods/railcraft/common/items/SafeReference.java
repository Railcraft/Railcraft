/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.common.util.misc.Code;
import net.minecraft.item.Item;

import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 1/17/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SafeReference {
    public static Supplier<Item> makeItem(String className) {
        return () -> {
            try {
                Class<? extends Item> clazz = Code.cast(Class.forName("mods.railcraft.common.items." + className));
                return clazz.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        };
    }
}
