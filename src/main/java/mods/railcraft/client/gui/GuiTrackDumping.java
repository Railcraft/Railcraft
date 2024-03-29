/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.gui;

import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerTrackDumping;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * GUI for dumping track kit.
 */
@SideOnly(Side.CLIENT)
public class GuiTrackDumping extends GuiTitled {

    private static final String LOCATION = RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_track_dumping.png";
    private final String FILTER_LABEL = LocalizationPlugin.translate("gui.railcraft.filters");
    private final String CART_FILTER_LABEL = LocalizationPlugin.translate("gui.railcraft.filters.carts");

    public GuiTrackDumping(ContainerTrackDumping container) {
        super(container.kit.getTile(), container, LOCATION, LocalizationPlugin.localize(container.kit.getTrackKit()));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GuiTools.drawStringCenteredAtPos(fontRenderer, FILTER_LABEL, 123, 26);
        GuiTools.drawStringCenteredAtPos(fontRenderer, CART_FILTER_LABEL, 51, 35);
    }
}
