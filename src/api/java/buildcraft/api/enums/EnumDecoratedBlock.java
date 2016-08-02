package buildcraft.api.enums;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum EnumDecoratedBlock implements IStringSerializable {
    DESTROY(0),
    BLUEPRINT(10),
    TEMPLATE(10),
    PAPER(10),
    LEATHER(10);

    public static final EnumDecoratedBlock[] VALUES = values();

    public final int lightValue;

    private EnumDecoratedBlock(int lightValue) {
        this.lightValue = lightValue;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static EnumDecoratedBlock fromMeta(int meta) {
        if (meta < 0 || meta >= VALUES.length) {
            return EnumDecoratedBlock.DESTROY;
        }
        return VALUES[meta];
    }
}
