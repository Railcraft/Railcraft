/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumColor implements IStringSerializable {

    BLACK(0x2D2D2D, "dyeBlack", "black"),
    RED(0xA33835, "dyeRed", "red"),
    GREEN(0x394C1E, "dyeGreen", "green"),
    BROWN(0x5C3A24, "dyeBrown", "brown"),
    BLUE(0x3441A2, "dyeBlue", "blue"),
    PURPLE(0x843FBF, "dyePurple", "purple"),
    CYAN(0x36809E, "dyeCyan", "cyan"),
    SILVER(0x888888, "dyeLightGray", "silver", "lightGray"),
    GRAY(0x444444, "dyeGray", "gray"),
    PINK(0xE585A0, "dyePink", "pink"),
    LIME(0x3FAA36, "dyeLime", "lime"),
    YELLOW(0xFFC700, "dyeYellow", "yellow"),
    LIGHT_BLUE(0x7F9AD1, "dyeLightBlue", "lightBlue"),
    MAGENTA(0xFF64FF, "dyeMagenta", "magenta"),
    ORANGE(0xFF6A00, "dyeOrange", "orange"),
    WHITE(0xFFFFFF, "dyeWhite", "white");
    public final static EnumColor[] VALUES = values();
    private final int hexColor;
    private final String oreTagDyeName;
    private final String[] names;

    EnumColor(int hexColor, String oreTagDyeName, String... names) {
        this.hexColor = hexColor;
        this.oreTagDyeName = oreTagDyeName;
        this.names = names;
    }

    public EnumDyeColor getDye() {
        return EnumDyeColor.byDyeDamage(ordinal());
    }

    public static EnumColor fromDye(EnumDyeColor dyeColor) {
        return fromOrdinal(dyeColor.getDyeDamage());
    }

    public MapColor getMapColor() {
        return getDye().getMapColor();
    }

    public int getHexColor() {
        return hexColor;
    }

    public static EnumColor fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return WHITE;
        return VALUES[id];
    }

    public static EnumColor fromDyeOreDictTag(String dyeTag) {
        for (EnumColor color : VALUES) {
            if (color.getDyeOreDictTag().equalsIgnoreCase(dyeTag))
                return color;
        }
        return null;
    }

    public static EnumColor fromName(String name) {
        for (EnumColor color : VALUES) {
            for (String tag : color.names)
                if (tag.equalsIgnoreCase(name))
                    return color;
        }
        return null;
    }

    public EnumColor next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public EnumColor previous() {
        return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
    }

    public static EnumColor getRand() {
        return VALUES[MiscTools.RANDOM.nextInt(VALUES.length)];
    }

    public EnumColor inverse() {
        return EnumColor.VALUES[15 - ordinal()];
    }

    public String getTag() {
        return "color." + getBaseTag();
    }

    public String getBaseTag() {
        return name().replace("_", ".").toLowerCase(Locale.ENGLISH);
    }

    public String getTranslatedName() {
        return LocalizationPlugin.translate("railcraft." + getTag());
    }

    public String getDyeOreDictTag() {
        return oreTagDyeName;
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

    @Override
    public String getName() {
        return names[0];
    }
}
