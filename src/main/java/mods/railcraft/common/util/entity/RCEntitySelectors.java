/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.entity;

import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 10/19/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum RCEntitySelectors implements Predicate<Entity> {
    LIVING {
        private final Predicate<Entity> predicate = Predicates.and(Objects::nonNull, EntitySelectors.IS_ALIVE, EntitySelectors.NOT_SPECTATING);

        @Override
        public boolean test(Entity entity) {
            return predicate.test(entity);
        }
    },
    KILLABLE {
        @Override
        public boolean test(Entity entity) {
            return LIVING.test(entity)
                    && !(entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)
                    && !(entity.getRidingEntity() instanceof EntityMinecart)
                    && entity instanceof EntityLivingBase
                    && ((EntityLivingBase) entity).getMaxHealth() < 100;
        }
    },
    /**
     * Checks if an entity does not form a part of the core game mechanics, e.g. pre-generation of map
     * via command block minecarts.
     */
    NON_MECHANICAL {
        @Override
        public boolean test(Entity entity) {
            return !entity.ignoreItemEntityData();
        }
    }
}
