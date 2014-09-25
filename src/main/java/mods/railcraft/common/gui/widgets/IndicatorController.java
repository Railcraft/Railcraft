/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.widgets;

import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class IndicatorController implements IIndicatorController {

    private final ToolTip tips = new ToolTip() {
        @Override
        public void refresh() {
            refreshToolTip();
        }

    };
    protected ToolTipLine tip = new ToolTipLine();

    public IndicatorController() {
        tips.add(tip);
    }

    protected void refreshToolTip() {
    }

    @Override
    public final ToolTip getToolTip() {
        return tips;
    }

}
