/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemDust extends ItemRailcraft {

    public ItemDust() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void initializeDefinintion() {
        for (EnumDust d : EnumDust.VALUES) {
            ItemStack stack = new ItemStack(this, 1, d.ordinal());
            RailcraftRegistry.register(stack);
            ForestryPlugin.addBackpackItem("miner", stack);
            OreDictionary.registerOre(d.oreTag, stack.copy());
        }
    }

    @Override
    public void definePostRecipes() {
        if (IC2Plugin.isModInstalled() && RailcraftConfig.getRecipeConfig("ic2.macerator.charcoal")) {
            IC2Plugin.addMaceratorRecipe(new ItemStack(Items.COAL, 1, 1), new ItemStack(this, 1, EnumDust.CHARCOAL.ordinal()));
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumDust dust : EnumDust.VALUES) {
            list.add(new ItemStack(this, 1, dust.ordinal()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage < 0 || damage >= EnumDust.VALUES.length)
            return "";
        return "item.railcraft." + EnumDust.VALUES[damage].tag;
    }

    @Override
    public String getOreTag(IVariantEnum variant) {
        IVariantEnum.tools.checkVariantObject(getClass(), variant);
        return ((EnumDust) variant).oreTag;
    }

    public enum EnumDust implements IVariantEnum {

        OBSIDIAN("dust.obsidian", "dustObsidian"),
        SULFUR("dust.sulfur", "dustSulfur"),
        SALTPETER("dust.saltpeter", "dustSaltpeter"),
        CHARCOAL("dust.charcoal", "dustCharcoal");
        public static final EnumDust[] VALUES = values();
        private final String tag;
        private final String oreTag;

        EnumDust(String tag, String oreTag) {
            this.tag = tag;
            this.oreTag = oreTag;
        }

        @Override
        public Object getAlternate() {
            return oreTag;
        }

        @Nonnull
        @Override
        public Class<? extends ItemRailcraft> getParentClass() {
            return ItemDust.class;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }

        @Override
        public int getItemMeta() {
            return ordinal();
        }
    }

}
