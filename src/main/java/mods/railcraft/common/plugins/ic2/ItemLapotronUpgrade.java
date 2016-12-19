///*------------------------------------------------------------------------------
// Copyright (c) CovertJaguar, 2011-2016
// http://railcraft.info
//
// This code is the property of CovertJaguar
// and may only be used with explicit written
// permission unless otherwise specified on the
// license page at http://railcraft.info/wiki/info:license.
// -----------------------------------------------------------------------------*/
//
//package mods.railcraft.common.plugins.ic2;
//
//import ic2.api.recipe.Recipes;
//import mods.railcraft.common.items.ItemRailcraft;
//import net.minecraft.init.Blocks;
//import net.minecraft.item.ItemStack;
//
///**
// * Created by CovertJaguar on 8/5/2016 for Railcraft.
// *
// * @author CovertJaguar <http://www.railcraft.info>
// */
//public class ItemLapotronUpgrade extends ItemRailcraft {
//    public ItemLapotronUpgrade() {
//        setMaxStackSize(9);
//    }
//
//    @Override
//    public void defineRecipes() {
//        ItemStack lapotron = IC2Plugin.getItem("lapotronCrystal");
//        ItemStack glassCable = IC2Plugin.getItem("glassFiberCableItem");
//        ItemStack circuit = IC2Plugin.getItem("advancedCircuit");
//
//        if (lapotron != null && glassCable != null && circuit != null) {
//            lapotron.copy();
////                lapotron.setItemDamage(-1);
//            Recipes.advRecipes.addRecipe(new ItemStack(this),
//                    "GGG",
//
//                    "wLw",
//                    "GCG",
//                    'G', new ItemStack(Blocks.GLASS, 1, 0),
//                    'w', glassCable,
//                    'C', circuit,
//                    'L', lapotron);
//        }
//    }
//}
