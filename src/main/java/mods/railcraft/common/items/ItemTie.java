/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.IVariantEnum;
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
import java.util.Locale;

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
    public void initializeDefinintion() {
        for (EnumTie tie : EnumTie.VALUES) {
            RailcraftRegistry.register(new ItemStack(this, 1, tie.ordinal()));
        }
        LootPlugin.addLoot(RailcraftItems.tie.getStack(1, EnumTie.WOOD), 4, 16, LootPlugin.Type.RAILWAY, "tie.wood");
        LootPlugin.addLoot(RailcraftItems.tie.getStack(1, EnumTie.STONE), 4, 16, LootPlugin.Type.WORKSHOP, "tie.stone");
    }

    @Override
    public void defineRecipes() {
        ItemStack tieStone = RailcraftItems.tie.getStack(1, EnumTie.STONE);
        CraftingPlugin.addRecipe(tieStone,
                " O ",
                "###",
                'O', RailcraftItems.rebar,
                '#', new ItemStack(Blocks.STONE_SLAB, 1, 0));
    }

    @Override
    public void finalizeDefinition() {
        ItemStack tieWood = RailcraftItems.tie.getStack(1, EnumTie.WOOD);
        for (ItemStack container : FluidHelper.getContainersFilledWith(Fluids.CREOSOTE.getB(1))) {
            CraftingPlugin.addRecipe(tieWood,
                    " O ",
                    "###",
                    'O', container,
                    '#', "slabWood");
        }
    }

    public enum EnumTie implements IVariantEnum {
        WOOD("slabWood"),
        STONE(Blocks.STONE_SLAB);
        public static final EnumTie[] VALUES = values();
        private Object alternate;

        EnumTie(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate(IRailcraftObjectContainer container) {
            return alternate;
        }

        @Override
        public boolean isValid(Class<?> clazz) {
            return clazz == ItemTie.class;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

}
