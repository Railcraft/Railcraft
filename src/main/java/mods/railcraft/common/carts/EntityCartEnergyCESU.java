///*------------------------------------------------------------------------------
// Copyright (c) CovertJaguar, 2011-2016
// http://railcraft.info
//
// This code is the property of CovertJaguar
// and may only be used with explicit written
// permission unless otherwise specified on the
// license page at http://railcraft.info/wiki/info:license.
// -----------------------------------------------------------------------------*/
//package mods.railcraft.common.carts;
//
//import mods.railcraft.common.plugins.ic2.IC2Plugin;
//import mods.railcraft.common.plugins.misc.Mod;
//import net.minecraft.item.ItemStack;
//import net.minecraft.world.World;
//
///**
// * @author CovertJaguar <http://www.railcraft.info>
// */
//public final class EntityCartEnergyCESU extends CartBaseEnergy {
//
//    public EntityCartEnergyCESU(World world) {
//        super(world);
//    }
//
//    public EntityCartEnergyCESU(World world, double d, double d1, double d2) {
//        this(world);
//        setPosition(d, d1 + getYOffset(), d2);
//        motionX = 0.0D;
//        motionY = 0.0D;
//        motionZ = 0.0D;
//        prevPosX = d;
//        prevPosY = d1;
//        prevPosZ = d2;
//    }
//
//    @Override
//    public IRailcraftCartContainer getCartType() {
//        return RailcraftCarts.ENERGY_CESU;
//    }
//
//    @Override
//    public int getTier() {
//        return 2;
//    }
//
//    @Override
//    public int getCapacity() {
//        return 300000;
//    }
//
//    @Override
//    public int getTransferLimit() {
//        return 128;
//    }
//
//    @Override
//    public ItemStack getIC2Item() {
//        //IC2 Classic so no RenderCrash Happens
//        return IC2Plugin.getItem(Mod.IC2_CLASSIC.isLoaded() ? "mfsUnit" : "cesuUnit");
//    }
//
//}
