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
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public class ItemGear extends ItemRailcraft {

    public ItemGear() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void initializeDefinintion() {
        for (EnumGear gear : EnumGear.values()) {
            ItemStack stack = new ItemStack(this, 1, gear.ordinal());
            RailcraftRegistry.register(stack);
        }

        OreDictionary.registerOre("gearIron", RailcraftItems.gear.getStack(1, EnumGear.IRON));

        ItemStack itemStack = new ItemStack(this, 1, EnumGear.BUSHING.ordinal());
        LootPlugin.addLoot(itemStack, 1, 8, LootPlugin.Type.RAILWAY, "gear.bushing");
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumGear gear : EnumGear.VALUES) {
            list.add(new ItemStack(this, 1, gear.ordinal()));
        }
    }

    @Override
    public void defineRecipes() {
        ItemStack bushing = RailcraftItems.gear.getStack(EnumGear.BUSHING);

        RailcraftItems gear = RailcraftItems.gear;

        CraftingPlugin.addRecipe(gear.getStack(2, EnumGear.BUSHING),
                "TT",
                "TT",
                'T', "ingotTin");

        CraftingPlugin.addRecipe(gear.getStack(EnumGear.GOLD_PLATE),
                " G ",
                "GBG",
                " G ",
                'G', "nuggetGold",
                'B', bushing);

        CraftingPlugin.addRecipe(gear.getStack(EnumGear.IRON),
                " I ",
                "IBI",
                " I ",
                'I', "ingotIron",
                'B', bushing);

        CraftingPlugin.addRecipe(gear.getStack(EnumGear.STEEL),
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

    public enum EnumGear implements IVariantEnum {

        GOLD_PLATE("ingotGold"),
        IRON("blockIron"),
        STEEL("blockSteel"),
        BUSHING("ingotTin");
        public static final EnumGear[] VALUES = values();
        private Object alternate;

        EnumGear(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate() {
            return alternate;
        }

        @Nonnull
        @Override
        public Class<? extends ItemRailcraft> getParentClass() {
            return ItemGear.class;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH).replace('_', '.');
        }
    }

}
