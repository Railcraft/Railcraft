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
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemIngot extends ItemRailcraft {

    public enum EnumIngot implements IItemMetaEnum {

        STEEL("ingotSteel"),
        COPPER("ingotCopper"),
        TIN("ingotTin"),
        LEAD("ingotLead");
        public static final EnumIngot[] VALUES = values();
        private IIcon icon;
        private String oreTag;

        EnumIngot(String oreTag) {
            this.oreTag = oreTag;
        }

        @Override
        public Object getAlternate() {
            return oreTag;
        }

        @Override
        public Class<? extends ItemRailcraft> getItemClass() {
            return ItemIngot.class;
        }

    }

    public ItemIngot() {
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("railcraft.ingot");
        setSmeltingExperience(1);
    }

    @Override
    public void initItem() {
        for (EnumIngot i : EnumIngot.VALUES) {
            ItemStack stack = new ItemStack(this, 1, i.ordinal());
            ForestryPlugin.addBackpackItem("miner", stack);
            RailcraftRegistry.register(stack);
            Metal m = Metal.get(i);
            OreDictionary.registerOre(m.getIngotTag(), m.getIngot());
        }

        LootPlugin.addLootTool(new ItemStack(this), 5, 9, "steel.ingot");
    }

    @Override
    public String getOreTag(IItemMetaEnum meta) {
        assertMeta(meta);
        return ((EnumIngot) meta).oreTag;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (EnumIngot ingot : EnumIngot.VALUES) {
            ingot.icon = iconRegister.registerIcon("railcraft:ingot." + ingot.name().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        for (EnumIngot ingot : EnumIngot.VALUES) {
            list.add(new ItemStack(this, 1, ingot.ordinal()));
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage >= EnumIngot.VALUES.length)
            return EnumIngot.STEEL.icon;
        return EnumIngot.VALUES[damage].icon;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage < 0 || damage >= EnumIngot.VALUES.length)
            return "item.railcraft.ingot";
        switch (EnumIngot.VALUES[damage]) {
            case STEEL:
                return "item.railcraft.ingot.steel";
            case COPPER:
                return "item.railcraft.ingot.copper";
            case TIN:
                return "item.railcraft.ingot.tin";
            case LEAD:
                return "item.railcraft.ingot.lead";
            default:
                return "item.railcraft.ingot";
        }
    }

}
