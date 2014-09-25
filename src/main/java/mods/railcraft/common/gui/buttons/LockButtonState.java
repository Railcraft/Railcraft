/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.buttons;

import mods.railcraft.common.gui.tooltips.ToolTip;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum LockButtonState implements IMultiButtonState {

    UNLOCKED(new ButtonTextureSet(224, 0, 16, 16)),
    LOCKED(new ButtonTextureSet(240, 0, 16, 16));
    public static final LockButtonState[] VALUES = values();
    private final IButtonTextureSet texture;

    private LockButtonState(IButtonTextureSet texture) {
        this.texture = texture;
    }

    @Override
    public String getLabel() {
        return "";
    }

    @Override
    public IButtonTextureSet getTextureSet() {
        return texture;
    }

    @Override
    public ToolTip getToolTip() {
        return null;
    }

}
