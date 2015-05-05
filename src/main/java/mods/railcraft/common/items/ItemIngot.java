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
import mods.railcraft.common.plugins.forge.LootPlugin;
import net.minecraft.init.Items;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemIngot extends ItemRailcraft {

    public enum EnumIngot implements IItemMetaEnum {

        STEEL, COPPER, TIN, LEAD;
        public static final EnumIngot[] VALUES = values();
        private IIcon icon;

        @Override
        public Class<? extends ItemRailcraft> getItemClass() {
            return ItemIngot.class;
        }

    };

    private static ItemIngot item;

    public static ItemStack getIngot(EnumIngot ingot) {
        return getIngot(ingot, 1);
    }

    public static ItemStack getIngot(EnumIngot ingot, int qty) {
        if (item != null)
            return new ItemStack(item, qty, ingot.ordinal());

        String tag = "railcraft.ingot";

        if (!RailcraftConfig.isItemEnabled(tag))
            return new ItemStack(Items.iron_ingot, qty);

        item = new ItemIngot();
        RailcraftRegistry.register(item);

        for (EnumIngot i : EnumIngot.VALUES) {
            ItemStack stack = new ItemStack(item, 1, i.ordinal());
            ForestryPlugin.addBackpackItem("miner", stack);
            RailcraftRegistry.register(stack);
            Metal m = Metal.get(i);
            OreDictionary.registerOre(m.getIngotTag(), m.getIngot());
        }

        LootPlugin.addLootTool(new ItemStack(item), 5, 9, "steel.ingot");

        return new ItemStack(item, qty, ingot.ordinal());
    }

    public ItemIngot() {
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("railcraft.ingot");
        setSmeltingExperience(1);

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
