/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.wayobjects.signals;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.modules.ModuleSignals;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CovertJaguar on 7/5/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum SignalVariant implements IEnumMachine<SignalVariant> {
    BLOCK(ModuleSignals.class, "block", TileSignalBlockSignal.class),
    DISTANT(ModuleSignals.class, "distant", TileSignalDistantSignal.class),
    TOKEN(ModuleSignals.class, "token", TileSignalDistantSignal.class),
    DUAL_HEAD_BLOCK(ModuleSignals.class, "block_dual", TileSignalDualHeadBlockSignal.class),
    DUAL_HEAD_DISTANT(ModuleSignals.class, "distant_dual", TileSignalDualHeadDistantSignal.class),
    DUAL_HEAD_TOKEN(ModuleSignals.class, "token_dual", TileSignalDualHeadDistantSignal.class),;

    private static final List<SignalVariant> creativeList = new ArrayList<>();
    public static final SignalVariant[] VALUES = values();

    static {
        for (SignalVariant variant : VALUES) {
            variant.def.passesLight = true;
        }

        creativeList.add(BLOCK);
        creativeList.add(DISTANT);
        creativeList.add(DUAL_HEAD_BLOCK);
        creativeList.add(DUAL_HEAD_DISTANT);
    }

    private final Definition def;

    SignalVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile) {
        this.def = new Definition(tag, tile, module);
    }

    public static SignalVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<SignalVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.signal_" + getBaseTag();
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.SIGNAL;
    }
}