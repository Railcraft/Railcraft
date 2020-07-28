/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import mods.railcraft.api.core.ILocalizedObject;
import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class LocalizationPlugin {
    public static final String ENGLISH = "en_US";

    public static String convertTag(String tag) {
        return tag.replace("_", ".");
    }

    public static String translate(String tag) {
        return I18n.translateToLocal(tag).replace("\\n", "\n").replace("\\%", "@");
    }

    public static String translateFast(String tag) {
        return I18n.translateToLocal(tag);
    }

    public static String translate(String tag, ILocalizedObject... args) {
        String text = translate(tag);

        Object[] objects = Arrays.stream(args).map(a -> translateFast(a.getLocalizationTag())).toArray();

        try {
            return String.format(text, objects);
        } catch (IllegalFormatException ex) {
            return "Format error: " + text;
        }
    }

    public static String translate(String tag, Object... args) {
        String text = translate(tag);

        try {
            return String.format(text, args);
        } catch (IllegalFormatException ex) {
            return "Format error: " + text;
        }
    }

    public static String format(String tag, Object... args) {
        String text = translate(tag);
        for (int ii = 0; ii < args.length; ii++) {
            if (args[ii] instanceof Double) {
                args[ii] = HumanReadableNumberFormatter.format((Double) args[ii]);
            }
        }

        try {
            return String.format(text, args);
        } catch (IllegalFormatException ex) {
            return "Format error: " + text;
        }
    }

    public static String translateArgs(String tag, Map<String, ILocalizedObject> args) {
        String text = translate(tag);
        for (Map.Entry<String, ILocalizedObject> arg : args.entrySet()) {
            text = text.replace("{" + arg.getKey() + "}", translateFast(arg.getValue().getLocalizationTag()));
        }
        return text;
    }

    public static boolean hasTag(String tag) {
        return I18n.canTranslate(tag);
    }

    public static String getEntityLocalizationTag(Entity entity) {
        EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
        String s = "generic";
        if (entry != null) {
            String domain = requireNonNull(entry.getRegistryName()).getNamespace();
            if ("minecraft".equals(domain)) {
                s = entry.getName();
            } else {
                s = domain + '.' + entry.getName();
            }
        }

        return "entity." + s + ".name";
    }
}
