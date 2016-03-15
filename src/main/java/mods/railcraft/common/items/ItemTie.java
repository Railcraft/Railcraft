/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemTie extends ItemRailcraft {

    public ItemTie() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 2; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String base = super.getUnlocalizedName();
        switch (stack.getItemDamage()) {
            case 1:
                return base + ".stone";
            default:
                return base + ".wood";
        }
    }

    @Override
    public void initItem() {
        for (EnumTie tie : EnumTie.VALUES) {
            RailcraftRegistry.register(new ItemStack(this, 1, tie.ordinal()));
        }
        LootPlugin.addLootRailway(RailcraftItem.tie.getStack(1, EnumTie.WOOD), 4, 16, "tie.wood");
        LootPlugin.addLootWorkshop(RailcraftItem.tie.getStack(1, EnumTie.STONE), 4, 16, "tie.stone");
    }

    @Override
    public void defineRecipes() {
        ItemStack tieStone = RailcraftItem.tie.getStack(1, EnumTie.STONE);
        CraftingPlugin.addShapedRecipe(tieStone,
                " O ",
                "###",
                'O', RailcraftItem.rebar,
                '#', new ItemStack(Blocks.stone_slab, 1, 0));
    }

    @Override
    public void definePostRecipes() {
        ItemStack tieWood = RailcraftItem.tie.getStack(1, EnumTie.WOOD);
        for (ItemStack container : FluidHelper.getContainersFilledWith(Fluids.CREOSOTE.getB(1))) {
            CraftingPlugin.addShapedRecipe(tieWood,
                    " O ",
                    "###",
                    'O', container,
                    '#', "slabWood");
        }
    }

    public enum EnumTie implements IItemMetaEnum {
        WOOD("slabWood"),
        STONE(Blocks.stone_slab);
        public static final EnumTie[] VALUES = values();
        private Object alternate;

        EnumTie(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate() {
            return alternate;
        }

        @Override
        public Class<? extends ItemRailcraft> getItemClass() {
            return ItemTie.class;
        }

    }

}
