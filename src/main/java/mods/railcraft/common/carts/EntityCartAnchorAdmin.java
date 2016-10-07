/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartAnchorAdmin extends EntityCartAnchorWorld {

    public EntityCartAnchorAdmin(World world) {
        super(world);
    }

    public EntityCartAnchorAdmin(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        return RailcraftCarts.getCartType(stack) == RailcraftCarts.ANCHOR_ADMIN;
    }

    @Override
    public boolean needsFuel() {
        return false;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return EnumMachineAlpha.ANCHOR_ADMIN.getDefaultState().withProperty(TileAnchorWorld.DISABLED, !hasTicketFlag());
    }

}
