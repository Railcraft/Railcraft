/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.modules;

import mods.railcraft.common.plugins.thaumcraft.ItemCrowbarMagic;
import mods.railcraft.common.plugins.thaumcraft.ItemCrowbarVoid;
import mods.railcraft.common.plugins.thaumcraft.ThaumcraftPlugin;
import mods.railcraft.common.util.misc.Game;
import org.apache.logging.log4j.Level;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ModuleThaumcraft extends RailcraftModulePayload {

    @Override
    public boolean checkPrerequisites() {
        return ThaumcraftPlugin.isModInstalled();
    }

    @Override
    public void printLoadError() {
        Game.log(Level.INFO, "Module disabled: {0}, Thaumcraft not detected", this);
    }

    @Override
    public void initFirst() {
        ItemCrowbarMagic.registerItem();
        ItemCrowbarVoid.registerItem();
    }

    @Override
    public void initSecond() {
    }

    @Override
    public void postInit() {
        ThaumcraftPlugin.registerAspects();
        ThaumcraftPlugin.setupResearch();

        ItemCrowbarMagic.registerResearch();
        ItemCrowbarVoid.registerResearch();
    }

}
