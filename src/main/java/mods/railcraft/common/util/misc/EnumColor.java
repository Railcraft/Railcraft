/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import java.util.Locale;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumColor {

    BLACK(0x2D2D2D),
    RED(0xA33835),
    GREEN(0x394C1E),
    BROWN(0x5C3A24),
    BLUE(0x3441A2),
    PURPLE(0x843FBF),
    CYAN(0x36809E),
    LIGHT_GRAY(0x888888),
    GRAY(0x444444),
    PINK(0xE585A0),
    LIME(0x3FAA36),
    YELLOW(0xFFC700),
    LIGHT_BLUE(0x7F9AD1),
    MAGENTA(0xFF64FF),
    ORANGE(0xFF6A00),
    WHITE(0xFFFFFF);
    public final static EnumColor[] VALUES = values();
    public final static String[] DYES = {
        "dyeBlack",
        "dyeRed",
        "dyeGreen",
        "dyeBrown",
        "dyeBlue",
        "dyePurple",
        "dyeCyan",
        "dyeLightGray",
        "dyeGray",
        "dyePink",
        "dyeLime",
        "dyeYellow",
        "dyeLightBlue",
        "dyeMagenta",
        "dyeOrange",
        "dyeWhite"};
    public final static String[] NAMES = {
        "Black",
        "Red",
        "Green",
        "Brown",
        "Blue",
        "Purple",
        "Cyan",
        "LightGray",
        "Gray",
        "Pink",
        "Lime",
        "Yellow",
        "LightBlue",
        "Magenta",
        "Orange",
        "White"};
    private final int color;

    private EnumColor(int color) {
        this.color = color;
    }

    public int getHexColor() {
        return color;
    }

    public static EnumColor fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            return WHITE;
        return VALUES[id];
    }

    public static EnumColor fromDye(String dyeTag) {
        for (int id = 0; id < DYES.length; id++) {
            if (DYES[id].equals(dyeTag))
                return VALUES[id];
        }
        return null;
    }

    public static EnumColor fromName(String name) {
        for (int id = 0; id < NAMES.length; id++) {
            if (NAMES[id].equals(name))
                return VALUES[id];
        }
        return null;
    }

    public EnumColor getNext() {
        EnumColor next = VALUES[(ordinal() + 1) % VALUES.length];
        return next;
    }

    public EnumColor getPrevious() {
        EnumColor previous = VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
        return previous;
    }

    public static EnumColor getRand() {
        return VALUES[MiscTools.getRand().nextInt(VALUES.length)];
    }

    public EnumColor inverse() {
        return EnumColor.VALUES[15 - ordinal()];
    }

    public String getTag() {
        return "color." + name().replace("_", ".").toLowerCase(Locale.ENGLISH);
    }

    public String getBasicTag() {
        return name().replace("_", ".").toLowerCase(Locale.ENGLISH);
    }

    public String getTranslatedName() {
        return LocalizationPlugin.translate("railcraft." + getTag());
    }

    public String getDye() {
        return DYES[ordinal()];
    }

    @Override
    public String toString() {
        String s = name().replace("_", " ");
        String[] words = s.split(" ");
        StringBuilder b = new StringBuilder();
        for (String word : words) {
            b.append(word.charAt(0)).append(word.substring(1).toLowerCase(Locale.ENGLISH)).append(" ");
        }
        return b.toString().trim();
    }

}
