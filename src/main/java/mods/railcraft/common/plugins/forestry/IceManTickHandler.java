///*------------------------------------------------------------------------------
// Copyright (c) CovertJaguar, 2011-2016
// http://railcraft.info
//
// This code is the property of CovertJaguar
// and may only be used with explicit written
// permission unless otherwise specified on the
// license page at http://railcraft.info/wiki/info:license.
// -----------------------------------------------------------------------------*/
//package mods.railcraft.common.plugins.forestry;
//
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.common.gameevent.TickEvent;
//import net.minecraftforge.fml.relauncher.Side;
//
///**
// *
// * @author CovertJaguar <http://www.railcraft.info>
// */
//public class IceManTickHandler {
//
//    @SubscribeEvent
//    public void tick(TickEvent.PlayerTickEvent event) {
//        if (event.side == Side.CLIENT)
//            return;
//        EntityPlayer player = event.player;
//        if (player.openContainer != player.inventoryContainer) return;
//        for (ItemStack stack : player.inventory.mainInventory) {
//            if (stack == null || stack.getItem() == null) continue;
//            if (stack.getItem() == ForestryPlugin.icemanBackpackT1 || stack.getItem() == ForestryPlugin.icemanBackpackT2)
//                IcemanBackpack.getInstance().compactInventory(stack);
//        }
//    }
//
//}
