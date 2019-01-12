/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;

import java.lang.annotation.*;

/**
 * Created by CovertJaguar on 12/27/18 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public @interface BlockMeta {

    /**
     * Associates a Tile Entity class with a block.
     *
     * Created by CovertJaguar on 11/29/2018 for Railcraft.
     *
     * @author CovertJaguar <http://www.railcraft.info>
     * @see IRailcraftBlockTile
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface Tile {
        Class<? extends TileRailcraft> value();
    }

    /**
     * Associates a variant enum with a block.
     *
     * Created by CovertJaguar on 2/26/2017 for Railcraft.
     *
     * @author CovertJaguar <http://www.railcraft.info>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface Variant {
        Class<? extends IVariantEnum> value();

        String propertyName() default "variant";
    }
}
