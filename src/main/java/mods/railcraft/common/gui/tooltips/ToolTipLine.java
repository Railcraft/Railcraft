/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.tooltips;

import com.google.common.base.Objects;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ToolTipLine implements Comparable<ToolTipLine> {

    public String text;
    public TextFormatting format;
    public int spacing;

    public ToolTipLine(String text, TextFormatting format) {
        this.text = text;
        this.format = format;
    }

    public ToolTipLine(String text) {
        this(text, TextFormatting.GRAY);
    }

    public ToolTipLine() {
        this("", TextFormatting.GRAY);
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public int getSpacing() {
        return spacing;
    }

    @Override
    public String toString() {
        String line;
        if (format != null)
            line = format + text;
        else
            line = text;
        line = line.replace('\u00A0', ' ');
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToolTipLine that = (ToolTipLine) o;
        return spacing == that.spacing &&
                Objects.equal(text, that.text) &&
                format == that.format;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(text, format, spacing);
    }

    @Override
    public int compareTo(@NotNull ToolTipLine that) {
        return text.compareTo(that.text);
    }
}
