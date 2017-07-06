/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.machine.worldspike.BlockWorldspike;
import mods.railcraft.common.blocks.machine.worldspike.WorldspikeVariant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartWorldspikeAdmin extends EntityCartWorldspikeStandard {

    public EntityCartWorldspikeAdmin(World world) {
        super(world);
    }

    public EntityCartWorldspikeAdmin(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.WORLDSPIKE_ADMIN;
    }

    @Override
    public boolean needsFuel() {
        return false;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return WorldspikeVariant.ADMIN.getDefaultState().withProperty(BlockWorldspike.ENABLED, hasTicketFlag());
    }

}
