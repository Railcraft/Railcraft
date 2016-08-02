package buildcraft.api.enums;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum EnumFillerPattern implements IStringSerializable {
    NONE,
    BOX,
    CLEAR,
    CYLINDER,
    FILL,
    FLATTEN,
    FRAME,
    HORIZON,
    PYRAMID,
    STAIRS;

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
