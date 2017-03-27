/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum ModItems {

    CELL_EMPTY(Mod.IC2, "cell"),
    BAT_BOX(Mod.IC2, "batBox"),
    MFE(Mod.IC2, "mfeUnit"),
    CESU(Mod.IC2, "cesuUnit"),
    MFSU(Mod.IC2, "mfsUnit"),
    BATTERY(Mod.IC2, "reBattery"),
    IC2_MACHINE(Mod.IC2, "machine"),
    CAN_EMPTY(Mod.FORESTRY, "canEmpty"),
    WAX_CAPSULE(Mod.FORESTRY, "waxCapsule"),
    REFRACTORY_EMPTY(Mod.FORESTRY, "refractoryEmpty"),
    REFRACTORY_WAX(Mod.FORESTRY, "refractoryWax"),
    INGOT_TIN(Mod.FORESTRY, "ingotTin"),
    BEESWAX(Mod.FORESTRY, "beeswax");
    private final Mod mod;
    public final String itemTag;
    private boolean init;
    private ItemStack stack;

    ModItems(Mod mod, String itemTag) {
        this.mod = mod;
        this.itemTag = itemTag;
    }

    @Nullable
    public ItemStack get() {
        if (!mod.isLoaded())
            return null;
        if (init) {
            init = true;
            init();
        }
        if (stack != null)
            return stack.copy();
        return null;
    }

    protected void init() {
        if (mod == Mod.IC2)
            stack = IC2Plugin.getItem(itemTag);
        else if (mod == Mod.FORESTRY)
            stack = ForestryPlugin.getItem(itemTag);
        if (stack == null)
            Game.log(Level.DEBUG, "Searched for but failed to find {0} item {1}", mod.name(), itemTag);
    }
}
