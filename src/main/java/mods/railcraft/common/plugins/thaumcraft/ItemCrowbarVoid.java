///*------------------------------------------------------------------------------
// Copyright (c) CovertJaguar, 2011-2016
// http://railcraft.info
//
// This code is the property of CovertJaguar
// and may only be used with explicit written
// permission unless otherwise specified on the
// license page at http://railcraft.info/wiki/info:license.
// -----------------------------------------------------------------------------*/
//package mods.railcraft.common.plugins.thaumcraft;
//
//import mods.railcraft.common.items.ItemCrowbar;
//import mods.railcraft.common.items.ItemMaterials;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.ItemStack;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.common.Optional;
//import thaumcraft.api.items.IWarpingGear;
//
//@Optional.InterfaceList({
//        @Optional.Interface(iface = "thaumcraft.api.items.IRepairable", modid = "Thaumcraft"),
//        @Optional.Interface(iface = "thaumcraft.api.items.IWarpingGear", modid = "Thaumcraft")
//})
//public class ItemCrowbarVoid extends ItemCrowbar implements /*IRepairable,*/ IWarpingGear {
//
//    public ItemCrowbarVoid() {
//        super(ItemMaterials.Material.VOID, ThaumcraftPlugin.getVoidmetalToolMaterial());
//    }
//
//    @Override
//    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
//        super.onUpdate(stack, world, entity, itemSlot, isSelected);
//
//        if (stack.isItemDamaged() && entity != null && entity.ticksExisted % 20 == 0 && entity instanceof EntityLivingBase) {
//            stack.damageItem(-1, (EntityLivingBase) entity);
//        }
//    }
//
//    @Override
//    public int getWarp(ItemStack itemstack, EntityPlayer player) {
//        return 1;
//    }
//}
