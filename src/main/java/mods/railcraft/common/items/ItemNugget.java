/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;
import mods.railcraft.common.plugins.forge.ItemRegistry;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Items;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemNugget extends ItemRailcraft {

    public static enum EnumNugget {

        IRON, STEEL, COPPER, TIN, LEAD;
        private IIcon icon;
        public static EnumNugget[] VALUES = values();

    };

    private static Item item;

    public static ItemStack getNugget(EnumNugget nugget) {
        return getNugget(nugget, 1);
    }

    public static ItemStack getNugget(EnumNugget nugget, int qty) {
        if (nugget == null)
            return null;
        if (item != null)
            return new ItemStack(item, qty, nugget.ordinal());
        
        String tag = "railcraft.nugget";
        if (!RailcraftConfig.isItemEnabled(tag))
            return null;

        item = new ItemNugget().setUnlocalizedName(tag);
        ItemRegistry.registerItem(item);

        for (EnumNugget n : EnumNugget.VALUES) {
            ItemStack stack = new ItemStack(item, 1, n.ordinal());
            ItemRegistry.registerItemStack(item.getUnlocalizedName(stack), stack);
            ForestryPlugin.addBackpackItem("miner", stack);
            Metal m = Metal.get(n);
            OreDictionary.registerOre(m.getNuggetTag(), m.getNugget());
            registerRecipe(m);
        }

        return new ItemStack(item, qty, nugget.ordinal());
    }

    private static void registerRecipe(Metal metal) {
        CraftingPlugin.addShapelessRecipe(metal.getNugget(9), metal.getIngot());
        CraftingPlugin.addShapedRecipe(metal.getIngot(),
                "NNN",
                "NNN",
                "NNN",
                'N', metal.getNuggetTag());
    }

    public ItemNugget() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (EnumNugget nugget : EnumNugget.VALUES) {
            nugget.icon = iconRegister.registerIcon("railcraft:nugget." + nugget.name().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        for (EnumNugget dust : EnumNugget.VALUES) {
            list.add(new ItemStack(this, 1, dust.ordinal()));
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage >= EnumNugget.values().length)
            return EnumNugget.IRON.icon;
        return EnumNugget.VALUES[damage].icon;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage < 0 || damage >= EnumNugget.VALUES.length)
            return "";
        switch (EnumNugget.VALUES[damage]) {
            case IRON:
                return "item.railcraft.nugget.iron";
            case STEEL:
                return "item.railcraft.nugget.steel";
            case COPPER:
                return "item.railcraft.nugget.copper";
            case TIN:
                return "item.railcraft.nugget.tin";
            case LEAD:
                return "item.railcraft.nugget.lead";
            default:
                return "";
        }
    }

}
