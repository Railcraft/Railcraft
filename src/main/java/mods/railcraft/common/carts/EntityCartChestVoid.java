/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.VoidChestLogic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class EntityCartChestVoid extends CartBaseLogicChest {

    public EntityCartChestVoid(World world) {
        super(world);
    }

    public EntityCartChestVoid(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    {
        setLogic(new VoidChestLogic(Logic.Adapter.of(this)));
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.CHEST_VOID;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return RailcraftBlocks.CHEST_VOID.getDefaultState();
    }
}
