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
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.items.RailcraftItems;

@RailcraftModule("signals")
public class ModuleSignals extends RailcraftModulePayload {

    public ModuleSignals() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.signal,
                        RailcraftItems.signalBlockSurveyor,
                        RailcraftItems.signalTuner,
                        RailcraftItems.signalLabel
                );
            }

            @Override
            public void preInit() {
            }
        });
    }
}
