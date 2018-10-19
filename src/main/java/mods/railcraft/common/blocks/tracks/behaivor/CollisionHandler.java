/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 8/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum CollisionHandler {
    NULL,
    ELECTRIC {
        @Override
        public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
            ChargeManager.instance.zapEntity(world, pos, entity, RailcraftDamageSource.TRACK_ELECTRIC, 2F);
        }
    };

    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
    }
}
