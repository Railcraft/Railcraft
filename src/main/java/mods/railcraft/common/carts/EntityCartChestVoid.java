/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.util.chest.ChestLogic;
import mods.railcraft.common.util.chest.VoidChestLogic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class EntityCartChestVoid extends EntityCartChestRailcraft {

    public EntityCartChestVoid(World world) {
        super(world);
    }

    @Override
    protected ChestLogic createLogic() {
        return new VoidChestLogic(world, this);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.CHEST_VOID;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return RailcraftBlocks.CHEST_VOID.getDefaultState();
    }

    @Override
    protected int getTickInterval() {
        return 8;
    }
}
