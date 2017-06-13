/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.plugins.ic2.IC2Plugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public final class EntityCartEnergyMFSU extends CartBaseEnergy {

    public EntityCartEnergyMFSU(World world) {
        super(world);
    }

    public EntityCartEnergyMFSU(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public int getTier() {
        return 3;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return IC2Plugin.getBlockState("te", "mfsu");
    }

    @Override
    public int getCapacity() {
        return 10000000;
    }

    @Override
    public int getTransferLimit() {
        return 512;
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.ENERGY_MFSU;
    }

}
