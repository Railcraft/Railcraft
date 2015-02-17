/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum ModItems {

    cellEmpty(Mod.IC2, "cell"),
    canEmpty(Mod.FORESTRY, "canEmpty"),
    waxCapsule(Mod.FORESTRY, "waxCapsule"),
    refractoryEmpty(Mod.FORESTRY, "refractoryEmpty"),
    refractoryWax(Mod.FORESTRY, "refractoryWax"),
    ingotTin(Mod.FORESTRY, "ingotTin"),
    beeswax(Mod.FORESTRY, "beeswax");
    private final Mod mod;
    public final String itemTag;
    private boolean needsInit = true;
    private ItemStack stack;

    private ModItems(Mod mod, String itemTag) {
        this.mod = mod;
        this.itemTag = itemTag;
    }

    public ItemStack get() {
        if (needsInit) needsInit = false;
        init();
        return stack;
    }

    protected void init() {
        if (mod == Mod.IC2)
            stack = IC2Plugin.getItem(itemTag);
        else if (mod == Mod.FORESTRY)
            stack = ForestryPlugin.getItem(itemTag);
        if(stack == null)
           Game.log(Level.DEBUG, "Searched for but failed to find {0} item {1}", mod.name(), itemTag);
    }

    private static enum Mod {

        FORESTRY, IC2;
    };
}
