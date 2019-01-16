/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
        if (isAnnotated(annotation, obj))
            return true;
        Class<?> objClass = obj.getClass();
        do {
            if (Arrays.stream(objClass.getInterfaces()).anyMatch(c -> c.isAnnotationPresent(annotation)))
                return true;
        } while ((objClass = objClass.getSuperclass()) != Object.class);
        return false;
    }
}
