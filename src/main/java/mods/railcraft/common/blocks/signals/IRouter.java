/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.gui.buttons.*;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.inventory.IInventory;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IRouter extends ISecure<LockButtonState>, IGuiReturnHandler {

    public enum RoutingButtonState implements IMultiButtonState {

        PUBLIC("railcraft.gui.routing.type.public"),
        PRIVATE("railcraft.gui.routing.type.private");
        private final String labelTag;

        private RoutingButtonState(String labelTag) {
            this.labelTag = labelTag;
        }

        @Override
        public String getLabel() {
            return LocalizationPlugin.translate(labelTag);
        }

        @Override
        public IButtonTextureSet getTextureSet() {
            return StandardButtonTextureSets.SMALL_BUTTON;
        }

        @Override
        public ToolTip getToolTip() {
            return null;
        }

    }

    MultiButtonController<RoutingButtonState> getRoutingController();

    IInventory getInventory();

    void resetLogic();

    RoutingLogic getLogic();

}
