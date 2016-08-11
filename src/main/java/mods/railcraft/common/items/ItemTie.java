/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Locale;

public class ItemTie extends ItemRailcraftSubtyped {

    public ItemTie() {
        super(EnumTie.class);
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
        public Object getAlternate(String objectTag) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
