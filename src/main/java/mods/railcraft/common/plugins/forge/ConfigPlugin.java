/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.util.misc.Game;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;

/**
 * Created by CovertJaguar on 8/24/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ConfigPlugin {
    public static final String COMMENT_PREFIX = "\n";
    public static final String COMMENT_SUFFIX = "\n";

    public static boolean getAndClear(Configuration config, String cat, String tag, boolean defaultValue, boolean clearedValue, String comment) {
        Property prop = config.get(cat, tag, defaultValue);
        decorateComment(prop, tag, comment);
        boolean ret = prop.getBoolean(defaultValue);
        if (ret != clearedValue)
            prop.set(clearedValue);
        return ret;
    }

    public static void decorateComment(Property property, String tag, String comment) {
        comment = COMMENT_PREFIX + comment.replace("{t}", tag) + COMMENT_SUFFIX;
        property.setComment(comment);
    }

    public static int parseInteger(Property prop, int defaultValue) {
        String value = prop.getString();
        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            Game.log().throwable(Level.WARN, 3, ex, "Failed to parse config tag, resetting to default: {0}", prop.getName());
            prop.set(defaultValue);
            return defaultValue;
        }
        return parsed;
    }

    public static double parseDouble(Property prop, double defaultValue) {
        String value = prop.getString();
        double parsed;
        try {
            parsed = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            Game.log().throwable(Level.WARN, 3, ex, "Failed to parse config tag, resetting to default: {0}", prop.getName());
            prop.set(defaultValue);
            return defaultValue;
        }
        return parsed;
    }
}
