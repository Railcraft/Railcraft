/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui.buttons;

import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiToggleButtonSmall extends GuiToggleButton {

    public GuiToggleButtonSmall(int i, int x, int y, String label, boolean active) {
        this(i, x, y, 200, label, active);
    }

    public GuiToggleButtonSmall(int i, int x, int y, int w, String label, boolean active) {
        super(i, x, y, w, StandardButtonTextureSets.SMALL_BUTTON, label, active);
    }

}
