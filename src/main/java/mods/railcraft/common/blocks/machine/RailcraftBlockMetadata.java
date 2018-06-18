/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine;

import mods.railcraft.api.core.IVariantEnum;

import java.lang.annotation.*;

/**
 * Created by CovertJaguar on 2/26/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface RailcraftBlockMetadata {
    Class<? extends IVariantEnum> variant();

    String propertyName() default "variant";
}
