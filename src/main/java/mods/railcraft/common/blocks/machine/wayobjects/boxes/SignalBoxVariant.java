/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.modules.ModuleSignals;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum SignalBoxVariant implements IEnumMachine<SignalBoxVariant> {
    ANALOG(ModuleSignals.class, "analog", TileBoxAnalog.class),
    CAPACITOR(ModuleSignals.class, "capacitor", TileBoxCapacitor.class),
    CONTROLLER(ModuleSignals.class, "controller", TileBoxController.class),
    INTERLOCK(ModuleSignals.class, "interlock", TileBoxInterlock.class),
    RECEIVER(ModuleSignals.class, "receiver", TileBoxReceiver.class),
    RELAY(ModuleSignals.class, "relay", TileBoxRelay.class),
    SEQUENCER(ModuleSignals.class, "sequencer", TileBoxSequencer.class),;

    private static final List<SignalBoxVariant> creativeList = new ArrayList<>();
    public static final SignalBoxVariant[] VALUES = values();

    static {
        for (SignalBoxVariant variant : VALUES) {
            variant.def.passesLight = true;
        }

        creativeList.add(CONTROLLER);
        creativeList.add(RECEIVER);
        creativeList.add(ANALOG);
        creativeList.add(CAPACITOR);
        creativeList.add(INTERLOCK);
        creativeList.add(SEQUENCER);
        creativeList.add(RELAY);
    }

    private final Definition def;

    SignalBoxVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile) {
        this.def = new Definition(tag, tile, module);
    }

    public static SignalBoxVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<SignalBoxVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.signal_box_" + getBaseTag();
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.SIGNAL_BOX;
    }
}
