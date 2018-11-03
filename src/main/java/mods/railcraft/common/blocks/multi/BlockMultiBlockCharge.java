/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeAccess;
import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * Created by CovertJaguar on 11/1/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockMultiBlockCharge extends BlockMultiBlock implements IChargeBlock {
    protected BlockMultiBlockCharge(Material materialIn) {
        super(materialIn);
    }

    @Override
    public IChargeAccess getMeterAccess(IBlockState state, World world, BlockPos pos) {
        Optional<TileMultiBlock> tile = WorldPlugin.getTileEntity(world, pos, TileMultiBlock.class);
        return Charge.distribution.network(world).access(tile.map(TileMultiBlock::getMasterPos).orElse(pos));
    }
}
