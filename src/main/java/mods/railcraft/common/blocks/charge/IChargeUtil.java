/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 10/19/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IChargeUtil {

    default IChargeNetwork network(World world) {
        return new IChargeNetwork() {
        };
    }

    default void zapEntity(World world, BlockPos pos, Entity entity, DamageSource damageSource, float damage) {
    }
}
