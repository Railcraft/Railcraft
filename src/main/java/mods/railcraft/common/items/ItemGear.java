/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
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

public class ItemGear extends ItemRailcraft {

    public enum EnumGear implements IItemMetaEnum {

        GOLD_PLATE("ingotGold"),
        IRON("blockIron"),
        STEEL("blockSteel"),
        BUSHING("ingotTin");
        public static final EnumGear[] VALUES = values();
        private IIcon icon;
        private Object alternate;

        EnumGear(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate() {
            return alternate;
        }

        @Override
        public Class<? extends ItemRailcraft> getItemClass() {
            return ItemGear.class;
        }

    }

    public ItemGear() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void initItem() {
        for (EnumGear gear : EnumGear.values()) {
            ItemStack stack = new ItemStack(this, 1, gear.ordinal());
            RailcraftRegistry.register(stack);
        }

        OreDictionary.registerOre("gearIron", RailcraftItem.gear.getStack(1, EnumGear.IRON));

        ItemStack itemStack = new ItemStack(this, 1, EnumGear.BUSHING.ordinal());
        LootPlugin.addLootRailway(itemStack, 1, 8, "gear.bushing");
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (EnumGear gear : EnumGear.VALUES) {
            gear.icon = iconRegister.registerIcon("railcraft:part.gear." + gear.name().toLowerCase(Locale.ENGLISH).replace("_", "."));
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (EnumGear gear : EnumGear.VALUES) {
            list.add(new ItemStack(this, 1, gear.ordinal()));
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage < 0 || damage >= EnumGear.VALUES.length)
            return EnumGear.IRON.icon;
        return EnumGear.VALUES[damage].icon;
    }

    @Override
    public void defineRecipes() {
        ItemStack bushing = RailcraftItem.gear.getStack(EnumGear.BUSHING);

        RailcraftItem gear = RailcraftItem.gear;

        CraftingPlugin.addShapedRecipe(gear.getStack(2, EnumGear.BUSHING),
                "TT",
                "TT",
                'T', "ingotTin");

        CraftingPlugin.addShapedRecipe(gear.getStack(EnumGear.GOLD_PLATE),
                " G ",
                "GBG",
                " G ",
                'G', "nuggetGold",
                'B', bushing);

        CraftingPlugin.addShapedRecipe(gear.getStack(EnumGear.IRON),
                " I ",
                "IBI",
                " I ",
                'I', "ingotIron",
                'B', bushing);

        CraftingPlugin.addShapedRecipe(gear.getStack(EnumGear.STEEL),
                " I ",
                "IBI",
                " I ",
                'I', "ingotSteel",
                'B', bushing);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage < 0 || damage >= EnumGear.VALUES.length)
            return "";
        switch (EnumGear.VALUES[damage]) {
            case GOLD_PLATE:
                return "item.railcraft.part.gear.gold.plate";
            case IRON:
                return "item.railcraft.part.gear.iron";
            case STEEL:
                return "item.railcraft.part.gear.steel";
            case BUSHING:
                return "item.railcraft.part.gear.bushing";
            default:
                return "";
        }
    }

}
