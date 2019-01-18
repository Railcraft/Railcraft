/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.buttons;

import mods.railcraft.common.gui.tooltips.ToolTip;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IMultiButtonState {

    String getLabel();

    String name();

    IButtonTextureSet getTextureSet();

    @Nullable
    ToolTip getToolTip();

}
