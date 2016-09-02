/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.misc.Mod;
import net.minecraftforge.fml.common.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule("railcraft:forestry")
public class ModuleForestry extends RailcraftModulePayload {
    @Override
    public void checkPrerequisites() throws MissingPrerequisiteException {
        if (!Mod.FORESTRY.isLoaded())
            throw new MissingPrerequisiteException("Forestry not detected");
    }

    public ModuleForestry() {
        setEnabledEventHandler(new ModuleEventHandler() {

            @Override
            @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
            public void init() {
                ForestryPlugin.instance().registerBackpacks();
            }

            @Override
            @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
            public void postInit() {
                ForestryPlugin.instance().setupBackpackContents();
            }
        });
    }
}
