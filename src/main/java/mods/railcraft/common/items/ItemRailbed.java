/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;
import java.util.Locale;

public class ItemRailbed extends ItemRailcraft {

    public enum EnumRailbed implements IItemMetaEnum {
        WOOD("stickWood"),
        STONE(Blocks.stone_slab);
        public static final EnumRailbed[] VALUES = values();
        private IIcon icon;
        private Object alternate;

        EnumRailbed(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate() {
            return alternate;
        }

        @Override
        public Class<? extends ItemRailcraft> getItemClass() {
            return ItemRailbed.class;
        }
    }

    public ItemRailbed() {
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("railcraft.part.railbed");
    }

    @Override
    public void initItem() {
        for (EnumRailbed railbed : EnumRailbed.VALUES) {
            RailcraftRegistry.register(new ItemStack(this, 1, railbed.ordinal()));
        }
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (EnumRailbed railbed : EnumRailbed.VALUES) {
            railbed.icon = iconRegister.registerIcon("railcraft:part.railbed." + railbed.name().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        for (int i = 0; i < 2; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage >= EnumRailbed.VALUES.length)
            return EnumRailbed.WOOD.icon;
        return EnumRailbed.VALUES[damage].icon;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        switch (stack.getItemDamage()) {
            case 1:
                return "item.railcraft.part.railbed.stone";
            default:
                return "item.railcraft.part.railbed.wood";
        }
    }

    @Override
    public void defineRecipes() {
        RailcraftItem item = RailcraftItem.railbed;

        Object tieWood = RailcraftItem.tie.getRecipeObject(EnumTie.WOOD);
        CraftingPlugin.addShapelessRecipe(item.getStack(1, EnumRailbed.WOOD),
                tieWood, tieWood, tieWood, tieWood);

        Object tieStone = RailcraftItem.tie.getRecipeObject(EnumTie.STONE);
        CraftingPlugin.addShapelessRecipe(item.getStack(1, EnumRailbed.STONE),
                tieStone, tieStone, tieStone, tieStone);
    }

}
