/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import org.apache.logging.log4j.Level;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is used for printing API related errors to the log files.
 *
 * Generally, new API functions should be surround by try/catch blocks that
 * catch {@code Error} throws. This ensures that the game doesn't crash if
 * the incorrect API version is loaded first.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class APIErrorHandler {

    private static final Set<Class<?>> printedWarnings = new HashSet<>();

    private APIErrorHandler() {
    }

    public static void versionMismatch(Class<?> type) {
        if (!printedWarnings.contains(type)) {
            Game.log().msg(Level.ERROR, "The Railcraft API (" + type.getSimpleName() + ") in one of the mods you are using needs updating, expect odd behavior.");
            printedWarnings.add(type);
        }
    }

}
