/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.plugins.misc.Mod;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule(value = "railcraft:thaumcraft", description = "thaumcraft integration")
public class ModuleThaumcraft extends RailcraftModulePayload {

    @Override
    public void checkPrerequisites() throws MissingPrerequisiteException {
        if (!Mod.THAUMCRAFT.isLoaded())
            throw new MissingPrerequisiteException("Thaumcraft not detected");
    }

    public ModuleThaumcraft() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
//                        RailcraftItems.CROWBAR_THAUMIUM,
//                        RailcraftItems.CROWBAR_VOID
                );
            }

            @Override
            public void postInit() {
//                ThaumcraftPlugin.registerAspects();
//                ThaumcraftPlugin.setupResearch();
            }
        });
    }
}
