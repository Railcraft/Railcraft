/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.world.IWorldNameable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class GuiTitled extends GuiContainerRailcraft {
    private final IWorldNameable nameable;
    private final @Nullable String name;
    protected boolean drawInvTitle = true;

    protected GuiTitled(IWorldNameable nameable, RailcraftContainer container, String texture) {
        this(nameable, container, texture, null);
    }

    protected GuiTitled(IWorldNameable nameable, RailcraftContainer container, String texture, @Nullable String name) {
        super(container, texture);
        this.nameable = nameable;
        this.name = name;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (name == null || nameable.hasCustomName()) {
            GuiTools.drawCenteredString(fontRenderer, nameable);
        } else {
            GuiTools.drawCenteredString(fontRenderer, name);
        }
        if (drawInvTitle)
            fontRenderer.drawString(LocalizationPlugin.translateFast("container.inventory"), 8, ySize - 94, 0x404040);
    }
}
