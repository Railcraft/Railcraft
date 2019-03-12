/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.events;

import mods.railcraft.common.blocks.TileRailcraft;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class MultiBlockEvent extends Event {
    private final TileRailcraft master;

    MultiBlockEvent(TileRailcraft master) {
        this.master = master;
    }

    public TileRailcraft getMaster() {
        return master;
    }

    public static final class Form extends MultiBlockEvent {
        public Form(TileRailcraft tile) {
            super(tile);
        }
    }
}
