/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui.buttons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;

@SideOnly(Side.CLIENT)
public class GuiButtonSmall extends GuiBetterButton {

    public GuiButtonSmall(int i, int x, int y, String s) {
        this(i, x, y, 200, s);
    }

    public GuiButtonSmall(int i, int x, int y, int w, String s) {
        super(i, x, y, w, StandardButtonTextureSets.SMALL_BUTTON, s);
    }

}
