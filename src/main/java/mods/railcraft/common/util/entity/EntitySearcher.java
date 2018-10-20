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

    public static SearchParameters<Entity> find() {
        return new SearchParameters<>(Entity.class);
    }

    public static class SearchParameters<T extends Entity> {
        private final Class<T> entityClass;
        private AxisAlignedBB searchBox;
        private Entity searcher;
        private Predicate<Entity> filter = RCEntitySelectors.LIVING;

        public SearchParameters(Class<T> entityClass) {
            this.entityClass = entityClass;
        }

        public List<T> at(World world) {
            if (searchBox == null)
                throw new NullPointerException("Improperly defined EntitySearcher");
            return world.getEntitiesWithinAABB(entityClass, searchBox, filter::test);
        }

        public SearchParameters<T> except(Entity entity) {
            this.filter = filter.and(e -> e != entity);
            return this;
        }

        public SearchParameters<T> around(AxisAlignedBB area) {
            searchBox = area;
            return this;
        }

        public SearchParameters<T> around(BlockPos pos) {
            searchBox = AABBFactory.start().createBoxForTileAt(pos).build();
            return this;
        }

        public SearchParameters<T> around(Entity entity) {
            searchBox = entity.getEntityBoundingBox();
            return this;
        }

        public SearchParameters<T> collidingWith(Entity entity) {
            searchBox = entity.getEntityBoundingBox();
            return this;
        }

        public SearchParameters<T> outTo(float distance) {
            searchBox = searchBox.grow(distance);
            return this;
        }

        public SearchParameters<T> upTo(float distance) {
            double x1 = searchBox.minX - distance;
            double y1 = searchBox.minY;
            double z1 = searchBox.minZ - distance;
            double x2 = searchBox.maxX + distance;
            double y2 = searchBox.maxY + distance;
            double z2 = searchBox.maxZ + distance;
            searchBox = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
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
