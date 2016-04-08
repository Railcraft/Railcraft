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

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.plugins.thaumcraft.ItemCrowbarMagic;
import mods.railcraft.common.plugins.thaumcraft.ItemCrowbarVoid;
import mods.railcraft.common.plugins.thaumcraft.ThaumcraftPlugin;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule("thaumcraft")
public class ModuleThaumcraft extends RailcraftModulePayload {

    @Override
    public void checkPrerequisites() throws MissingPrerequisiteException {
        if (!ThaumcraftPlugin.isModInstalled())
            throw new MissingPrerequisiteException("Thaumcraft not detected");
    }

    public ModuleThaumcraft() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void preInit() {
                ItemCrowbarMagic.registerItem();
                ItemCrowbarVoid.registerItem();
            }

            @Override
            public void postInit() {
                ThaumcraftPlugin.registerAspects();
                ThaumcraftPlugin.setupResearch();

                ItemCrowbarMagic.registerResearch();
                ItemCrowbarVoid.registerResearch();
            }
        });
    }
}
