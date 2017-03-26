/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.routing;

import mods.railcraft.common.gui.buttons.*;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.ISecureObject;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.inventory.IInventory;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IRouter extends ISecureObject<LockButtonState>, IGuiReturnHandler {

    enum RoutingButtonState implements IMultiButtonState {

        PUBLIC("gui.railcraft.routing.type.public"),
        PRIVATE("gui.railcraft.routing.type.private");
        private final String labelTag;

        RoutingButtonState(String labelTag) {
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
