/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartAnchorAdmin extends EntityCartAnchor {

    public EntityCartAnchorAdmin(World world) {
        super(world);
    }

    public EntityCartAnchorAdmin(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        return EnumCart.getCartType(stack) == EnumCart.ANCHOR_ADMIN;
    }

    @Override
    public boolean needsFuel() {
        return false;
    }

    @Override
    public IBlockState getDisplayTile() {
        return EnumMachineAlpha.ANCHOR_ADMIN.getState();
    }

    @Override
    public String getInventoryName() {
        return LocalizationPlugin.translate(EnumCart.ANCHOR_ADMIN.getTag());
    }

    @Override
    public IIcon getBlockTextureOnSide(int side) {
        if (side < 2 && !getFlag(TICKET_FLAG))
            return EnumMachineAlpha.ANCHOR_ADMIN.getTexture(6);
        return EnumMachineAlpha.ANCHOR_ADMIN.getTexture(side);
    }

}
