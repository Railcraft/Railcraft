/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.tooltips;

import net.minecraft.util.EnumChatFormatting;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ToolTipLine {

    public String text;
    public EnumChatFormatting format;
    public int spacing;

    public ToolTipLine(String text, EnumChatFormatting format) {
        this.text = text;
        this.format = format;
    }

    public ToolTipLine(String text) {
        this(text, null);
    }

    public ToolTipLine() {
        this("", null);
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public int getSpacing() {
        return spacing;
    }

}
