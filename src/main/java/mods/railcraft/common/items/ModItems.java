/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.crafting.Ingredients;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum ModItems implements IIngredientSource {

    SILK(Mod.FORESTRY, "crafting_material", 3),
    STURDY_CASING(Mod.FORESTRY, "sturdy_machine"),
    HONEY_DROP(Mod.FORESTRY, "honey_drop"),
    HONEYDEW_DROP(Mod.FORESTRY, "honeydew"),
    HONEY_COMB(Mod.FORESTRY, "bee_combs", OreDictionary.WILDCARD_VALUE),
    APATITE(Mod.FORESTRY, "apatite"),
    APATITE_ORE(Mod.FORESTRY, "resources", 0),
    BEE_QUEEN(Mod.FORESTRY, "bee_queen_ge"),
    BEE_DRONE(Mod.FORESTRY, "bee_drone_ge"),
    STICKY_RESIN(Mod.IC2, "misc_resource#resin"),
    BAT_BOX(Mod.IC2, "te#batbox"),
    MFE(Mod.IC2, "te#mfe"),
    CESU(Mod.IC2, "te#cesu"),
    MFSU(Mod.IC2, "te#mfsu"),
    BATTERY(Mod.IC2, "re_battery", OreDictionary.WILDCARD_VALUE),
    IC2_MACHINE(Mod.IC2, "resource#machine"),
    SLAG(Mod.IC2, "misc_resource#slag"),
    RUBBER_BOOTS(Mod.IC2, "rubber_boots"),
    STATIC_BOOTS(Mod.IC2, "static_boots"),
    CRUSHED_IRON(Mod.IC2, "crushed#iron"),
    DUST_IRON(Mod.IC2, "dust#iron"),
    CRUSHED_GOLD(Mod.IC2, "crushed#gold"),
    DUST_GOLD(Mod.IC2, "dust#gold"),
    CRUSHED_COPPER(Mod.IC2, "crushed#copper"),
    DUST_COPPER(Mod.IC2, "dust#copper"),
    CRUSHED_TIN(Mod.IC2, "crushed#tin"),
    DUST_TIN(Mod.IC2, "dust#tin"),
    CRUSHED_SILVER(Mod.IC2, "crushed#silver"),
    DUST_SILVER(Mod.IC2, "dust#silver"),
    CRUSHED_LEAD(Mod.IC2, "crushed#lead"),
    DUST_LEAD(Mod.IC2, "dust#lead"),
    CRUSHED_URANIUM(Mod.IC2, "crushed#uranium"),
    URANIUM_DROP(Mod.IC2, "nuclear#uranium_235"),
    ;
    private static ResourceLocation IC2_CLASSIC_NO_USE = new ResourceLocation("ic2", "itemnouse");
    private final Mod mod;
    public final String itemTag;
    public final int meta;
    private boolean needsInit = true;
    // This is nullable because can't always be sure what other mods will give us.
    private @Nullable ItemStack stack = ItemStack.EMPTY;

    ModItems(Mod mod, String itemTag) {
        this(mod, itemTag, -1);
    }

    ModItems(Mod mod, String itemTag, int meta) {
        this.mod = mod;
        this.itemTag = itemTag;
        this.meta = meta;
    }

    @Override
    public ItemStack getStack() {
        return getStack(1);
    }

    @Override
    public ItemStack getStack(int qty) {
        init();
        return InvTools.copy(stack, qty);
    }

    @Override
    public Ingredient getIngredient() {
        return Ingredients.from(getStack());
    }

    public boolean isEqual(ItemStack otherStack, boolean matchMeta, boolean matchNBT) {
        init();
        return InvTools.isItemEqual(stack, otherStack, matchMeta, matchNBT);
    }

    protected void init() {
        if (needsInit) {
            RailcraftModuleManager.Stage stage = RailcraftModuleManager.getStage();
            if (stage.compareTo(RailcraftModuleManager.Stage.INIT) < 0)
                throw new RuntimeException("Don't use ModItems before INIT");
            if (mod.isLoaded()) {
                needsInit = false;
                if (mod == Mod.IC2) {
                    ItemStack s = IC2Plugin.getItem(itemTag);
                    if (!InvTools.isEmpty(s) && !Objects.equals(s.getItem().getRegistryName(), IC2_CLASSIC_NO_USE))
                        stack = s;
                } else if (mod == Mod.FORESTRY)
                    stack = ForestryPlugin.getItem(itemTag);
                if (InvTools.isEmpty(stack))
                    Game.log().msg(Level.DEBUG, "Searched for but failed to find {0} item {1}", mod.name(), itemTag);
                else if (meta >= 0)
                    stack.setItemDamage(meta);
            }
        }
    }
}
