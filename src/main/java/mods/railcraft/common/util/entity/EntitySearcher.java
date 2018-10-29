/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.entity;

import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 8/29/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntitySearcher {

    public static SearchParameters<EntityMinecart> findMinecarts() {
        return new SearchParameters<>(EntityMinecart.class);
    }

    public static <T extends Entity> SearchParameters<T> find(Class<T> entityClass) {
        return new SearchParameters<>(entityClass);
    }

    public static SearchParameters<EntityLivingBase> findLivings() {
        return new SearchParameters<>(EntityLivingBase.class);
    }

    public static SearchParameters<Entity> find() {
        return new SearchParameters<>(Entity.class);
    }

    public static class SearchParameters<T extends Entity> {
        private final Class<T> entityClass;
        private AABBFactory box = AABBFactory.start();
        private boolean needBox = true;
        private Predicate<Entity> filter = RCEntitySelectors.LIVING;

        public SearchParameters(Class<T> entityClass) {
            this.entityClass = entityClass;
        }

        public List<T> at(World world) {
            if (needBox)
                throw new NullPointerException("Improperly defined EntitySearcher without a search box");
            return world.getEntitiesWithinAABB(entityClass, box.build(), filter::test);
        }

        public SearchParameters<T> except(Entity entity) {
            this.filter = filter.and(e -> e != entity);
            return this;
        }

        public SearchParameters<T> around(AxisAlignedBB area) {
            box.fromAABB(area);
            needBox = false;
            return this;
        }

        public SearchParameters<T> around(BlockPos pos) {
            box.createBoxForTileAt(pos);
            needBox = false;
            return this;
        }

        public SearchParameters<T> around(Entity entity) {
            box.fromAABB(entity.getEntityBoundingBox());
            needBox = false;
            return this;
        }

        // TODO difference from #around method?
        public SearchParameters<T> collidingWith(Entity entity) {
            box.fromAABB(entity.getEntityBoundingBox());
            needBox = false;
            return this;
        }

        public SearchParameters<T> outTo(float distance) {
            box.growUnrestricted(distance);
            needBox = false;
            return this;
        }

        public SearchParameters<T> upTo(float distance) {
            box.upTo(distance);
            needBox = false;
            return this;
        }

        public SearchParameters<T> boxFactory(AABBFactory factory) {
            box = factory;
            needBox = false;
            return this;
        }

        public SearchParameters<T> and(Predicate<Entity> filter) {
            this.filter.and(filter);
            return this;
        }

        @SafeVarargs
        public final SearchParameters<T> and(Predicate<Entity>... filters) {
            if (!ArrayUtils.isEmpty(filters))
                filter.and(Predicates.and(filters));
            return this;
        }
    }
}
