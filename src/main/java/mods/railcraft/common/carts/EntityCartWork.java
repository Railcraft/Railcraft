/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.gui.EnumGui;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartWork extends CartBase {

    public EntityCartWork(World world) {
        super(world);
    }

    public EntityCartWork(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.WORK;
    }

    @Override
    protected Optional<EnumGui> getGuiType() {
        return EnumGui.CART_WORK.op();
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.CRAFTING_TABLE.getDefaultState();
    }
}
