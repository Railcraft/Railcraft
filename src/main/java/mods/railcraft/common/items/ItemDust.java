/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemDust extends ItemRailcraft {

    public enum EnumDust implements IItemMetaEnum {

        OBSIDIAN("dust.obsidian", "dustObsidian"),
        SULFUR("dust.sulfur", "dustSulfur"),
        SALTPETER("dust.saltpeter", "dustSaltpeter"),
        CHARCOAL("dust.charcoal", "dustCharcoal");
        public static final EnumDust[] VALUES = values();
        private final String tag;
        private final String oreTag;
        private IIcon icon;

        EnumDust(String tag, String oreTag) {
            this.tag = tag;
            this.oreTag = oreTag;
        }

        @Override
        public Object getAlternate() {
            return oreTag;
        }

        @Override
        public Class<? extends ItemRailcraft> getItemClass() {
            return ItemDust.class;
        }
    }

    public ItemDust() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void initItem() {
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
            IC2Plugin.addMaceratorRecipe(new ItemStack(Items.coal, 1, 1), new ItemStack(this, 1, EnumDust.CHARCOAL.ordinal()));
        }
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (EnumDust dust : EnumDust.VALUES) {
            dust.icon = iconRegister.registerIcon("railcraft:" + dust.tag);
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
        if (damage < 0 || damage >= EnumDust.VALUES.length)
            return "";
        return "item.railcraft." + EnumDust.VALUES[damage].tag;
    }

    @Override
    public String getOreTag(IItemMetaEnum meta) {
        assertMeta(meta);
        return ((EnumDust) meta).oreTag;
    }

}
