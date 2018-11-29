/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import java.lang.annotation.*;

/**
 * Created by CovertJaguar on 11/29/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface BlockMetaTile {
    Class<? extends RailcraftTileEntity> value();
}
