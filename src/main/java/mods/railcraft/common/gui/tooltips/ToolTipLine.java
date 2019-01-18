/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.tooltips;

import net.minecraft.util.text.TextFormatting;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ToolTipLine {

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
}
