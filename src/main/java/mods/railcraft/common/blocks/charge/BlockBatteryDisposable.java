/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

/**
 * Created by CovertJaguar on 6/16/2022 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockBatteryDisposable extends BlockBattery {

    protected abstract IRailcraftBlockContainer getEmpty();

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        Charge.distribution.network(world).access(pos).getBattery().ifPresent(bat -> {
            if (bat.getCharge() <= 0)
                WorldPlugin.setBlockState(world, pos, getEmpty().getDefaultState());
        });
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Optional.ofNullable((Item) getEmpty().item()).orElse(Items.AIR);
    }
}
