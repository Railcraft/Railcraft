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
import mods.railcraft.common.blocks.logic.MetalsChestLogic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class EntityCartChestMetals extends CartBaseLogicChest {

    public EntityCartChestMetals(World world) {
        super(world);
    }

    public EntityCartChestMetals(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    {
        setLogic(new MetalsChestLogic(Logic.Adapter.of(this)));
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.CHEST_METALS;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return RailcraftBlocks.CHEST_METALS.getDefaultState();
    }
}
