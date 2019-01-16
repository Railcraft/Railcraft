/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.events;

import mods.railcraft.common.blocks.multi.IMultiBlockTile;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class MultiBlockEvent extends Event {
    private final IMultiBlockTile multiBlock;

    MultiBlockEvent(IMultiBlockTile tile) {
        this.multiBlock = tile;
    }

    public IMultiBlockTile getMultiBlock() {
        return multiBlock;
    }

    public static final class Form extends MultiBlockEvent {
        public Form(IMultiBlockTile tile) {
            super(tile);
        }
    }
}
