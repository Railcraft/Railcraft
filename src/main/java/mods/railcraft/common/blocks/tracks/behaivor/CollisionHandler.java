/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.api.charge.Charge;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
            if (entity instanceof EntityLivingBase)
                Charge.distribution.network(world).access(pos).zap(entity, Charge.DamageOrigin.TRACK, 2F);
        }
    };

    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
    }
}
