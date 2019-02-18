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
import mods.railcraft.common.blocks.multi.IMultiBlockTile;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class MultiBlockEvent extends Event {
    private final TileRailcraft multiBlock;

    MultiBlockEvent(TileRailcraft tile) {
        this.multiBlock = tile;
    }

    public TileRailcraft getMultiBlock() {
        return multiBlock;
    }

    public static final class Form extends MultiBlockEvent {
        public Form(TileRailcraft tile) {
            super(tile);
        }
    }
}
