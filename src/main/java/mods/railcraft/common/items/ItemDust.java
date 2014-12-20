/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import net.minecraft.init.Items;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemDust extends ItemRailcraft {

    public enum EnumDust {

        OBSIDIAN, SULFUR, SALTPETER, CHARCOAL;
        public static final EnumDust[] VALUES = values();
        private IIcon icon;
    };
    private static ItemDust item;

    public static ItemStack getDust(EnumDust dust) {
        return getDust(dust, 1);
    }

    public static ItemStack getDust(EnumDust dust, int qty) {
        if (item != null) {
            return new ItemStack(item, qty, dust.ordinal());
        }

        String tag = "railcraft.dust";
        if (!RailcraftConfig.isItemEnabled(tag)) {
            return null;
        }

        item = new ItemDust();
        RailcraftRegistry.register(item);

        for (EnumDust d : EnumDust.values()) {
            ItemStack stack = new ItemStack(item, 1, d.ordinal());
            RailcraftRegistry.register(stack);
            ForestryPlugin.addBackpackItem("miner", stack);
        }

        OreDictionary.registerOre("dustObsidian", new ItemStack(item, 1, EnumDust.OBSIDIAN.ordinal()));
        OreDictionary.registerOre("dustSulfur", new ItemStack(item, 1, EnumDust.SULFUR.ordinal()));
        OreDictionary.registerOre("dustSaltpeter", new ItemStack(item, 1, EnumDust.SALTPETER.ordinal()));
        OreDictionary.registerOre("dustCharcoal", new ItemStack(item, 1, EnumDust.CHARCOAL.ordinal()));

        if (IC2Plugin.isModInstalled() && RailcraftConfig.getRecipeConfig("ic2.macerator.charcoal")) {
            IC2Plugin.addMaceratorRecipe(new ItemStack(Items.coal, 1, 1), new ItemStack(item, 1, EnumDust.CHARCOAL.ordinal()));
        }

        return new ItemStack(item, qty, dust.ordinal());
    }

    public ItemDust() {
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("railcraft.dust");
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (EnumDust dust : EnumDust.VALUES) {
            dust.icon = iconRegister.registerIcon("railcraft:dust." + dust.name().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (EnumDust dust : EnumDust.VALUES) {
            list.add(new ItemStack(this, 1, dust.ordinal()));
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage >= EnumDust.VALUES.length) {
            return EnumDust.CHARCOAL.icon;
        }
        return EnumDust.VALUES[damage].icon;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage < 0 || damage >= EnumDust.VALUES.length) {
            return "";
        }
        switch (EnumDust.VALUES[damage]) {
            case OBSIDIAN:
                return "item.railcraft.dust.obsidian";
            case SULFUR:
                return "item.railcraft.dust.sulfur";
            case SALTPETER:
                return "item.railcraft.dust.saltpeter";
            case CHARCOAL:
                return "item.railcraft.dust.charcoal";
            default:
                return "";
        }
    }
}
