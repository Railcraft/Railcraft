/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemDust extends ItemRailcraftSubtyped {

    public ItemDust() {
        super(EnumDust.class);
    }

    @Override
    public void initializeDefinintion() {
        for (EnumDust d : EnumDust.VALUES) {
            ItemStack stack = new ItemStack(this, 1, d.ordinal());
            RailcraftRegistry.register(this, d, stack);
            ForestryPlugin.addBackpackItem("forestry.miner", stack);
            OreDictionary.registerOre(d.oreTag, stack.copy());
        }
    }

    @Override
    public void finalizeDefinition() {
        if (Mod.anyLoaded(Mod.IC2, Mod.IC2_CLASSIC) && RailcraftConfig.getRecipeConfig("ic2.macerator.charcoal")) {
            IC2Plugin.addMaceratorRecipe(new ItemStack(Items.COAL, 1, 1), new ItemStack(this, 1, EnumDust.CHARCOAL.ordinal()));
        }
        if (Mod.IC2.isLoaded() && RailcraftConfig.getRecipeConfig("ic2.macerator.slag")) {
            IC2Plugin.addMaceratorRecipe(ModItems.SLAG.get(), new ItemStack(this, 1, EnumDust.SLAG.ordinal()));
        }
    }

    public enum EnumDust implements IVariantEnum {

        OBSIDIAN("dustObsidian"),
        SULFUR("dustSulfur"),
        SALTPETER("dustSaltpeter"),
        CHARCOAL("dustCharcoal"),
        SLAG("dustSlag"),
        NICKEL("dustNickel"),
        SMALL_NICKEL("dustTinyNickel");
        public static final EnumDust[] VALUES = values();
        private final String oreTag;

        EnumDust(String oreTag) {
            this.oreTag = oreTag;
        }

        @Nullable
        @Override
        public String getOreTag() {
            return oreTag;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
