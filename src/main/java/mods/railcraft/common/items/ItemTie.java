/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;

public class ItemTie extends ItemRailcraftSubtyped {

    public ItemTie() {
        super(EnumTie.class);
    }

    @Override
    public void initializeDefinintion() {
        for (EnumTie tie : EnumTie.VALUES) {
            RailcraftRegistry.register(this, tie, new ItemStack(this, 1, tie.ordinal()));
        }
        LootPlugin.addLootUnique(RailcraftItems.TIE, EnumTie.WOOD, 4, 16, LootPlugin.Type.RAILWAY);
        LootPlugin.addLootUnique(RailcraftItems.TIE, EnumTie.STONE, 4, 16, LootPlugin.Type.WORKSHOP);
    }

    @Override
    public void defineRecipes() {
        ItemStack tieStone = RailcraftItems.TIE.getStack(1, EnumTie.STONE);
        CraftingPlugin.addRecipe(tieStone,
                " O ",
                "#r#",
                'O', Items.WATER_BUCKET,
                'r', RailcraftItems.REBAR,
                '#', RailcraftItems.CONCRETE);
    }

    @Override
    public void finalizeDefinition() {
        ItemStack tieWood = RailcraftItems.TIE.getStack(1, EnumTie.WOOD);
        // TODO fix recipe
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
        public Object getAlternate(IRailcraftRecipeIngredient container) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
