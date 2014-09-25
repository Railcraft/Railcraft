/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import mods.railcraft.common.plugins.forestry.ForestryPlugin;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModuleForestry extends RailcraftModule {

    @Override
    public boolean canModuleLoad() {
        return ForestryPlugin.isForestryInstalled();
    }

    @Override
    public void initFirst() {
        ForestryPlugin.registerBackpacks();
    }

    @Override
    public void postInit() {
        ForestryPlugin.setupBackpackContents();
    }

}
