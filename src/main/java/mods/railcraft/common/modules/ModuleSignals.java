/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.wayobjects.signals.TokenManager;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@RailcraftModule(value = "railcraft:signals", description = "signals, signal boxes")
public class ModuleSignals extends RailcraftModulePayload {

    public ModuleSignals() {
        add(
                RailcraftBlocks.SIGNAL,
                RailcraftBlocks.SIGNAL_DUAL,
                RailcraftBlocks.SIGNAL_BOX,
                RailcraftItems.SIGNAL_BLOCK_SURVEYOR,
                RailcraftItems.SIGNAL_TUNER,
                RailcraftItems.SIGNAL_LAMP,
                RailcraftItems.SIGNAL_LABEL,
                RailcraftItems.CIRCUIT
        );
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                MinecraftForge.EVENT_BUS.register(TokenManager.getEventListener());
            }
        });
    }

    @Override
    public void loadConfig(Configuration config) {
        SignalTools.printSignalDebug = config.getBoolean("printDebug", CAT_CONFIG, false,
                "change to 'true' to log debug info for Signal Blocks");

        SignalTools.signalUpdateInterval = config.getInt("updateInterval", CAT_CONFIG, 4, 1, 64,
                "measured in ticks, smaller numbers update more often, resulting in more sensitive signals, but cost more cpu power");
    }
}
