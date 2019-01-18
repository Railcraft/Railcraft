/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui.buttons;

import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSimpleButton extends GuiBetterButton<GuiSimpleButton> {

    public GuiSimpleButton(int id, int x, int y, String label) {
        this(id, x, y, 200, StandardButtonTextureSets.LARGE_BUTTON, label);
    }

    public GuiSimpleButton(int id, int x, int y, int width, String label) {
        this(id, x, y, width, StandardButtonTextureSets.LARGE_BUTTON, label);
    }

    public GuiSimpleButton(int id, int x, int y, int width, IButtonTextureSet texture, String label) {
        super(id, x, y, width, texture, label);
    }
}
