/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Created by CovertJaguar on 12/16/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Annotations {

    public static boolean isAnnotated(Class<? extends Annotation> annotation, Object obj) {
        return obj.getClass().isAnnotationPresent(annotation);
    }

    public static boolean isAnnotatedDeepSearch(Class<? extends Annotation> annotation, Object obj) {
        return isAnnotated(annotation, obj)
                || Arrays.stream(obj.getClass().getInterfaces()).anyMatch(c -> c.isAnnotationPresent(annotation));
    }
}
