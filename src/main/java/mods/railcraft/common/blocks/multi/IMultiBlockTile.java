/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.api.core.IOwnable;
import mods.railcraft.common.blocks.interfaces.ITile;
import mods.railcraft.common.blocks.multi.TileMultiBlock.MultiBlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IMultiBlockTile extends IOwnable, ITile {

    boolean isStructureValid();

    /**
     * Note: Must call {@link #isStructureValid()} before doing master functions for edge cases!
     *
     * @return True if this block entity is the master block
     */
    boolean isMaster();

    @Nullable
    TileMultiBlock getMasterBlock();

    MultiBlockPattern getCurrentPattern();

    MultiBlockState getState();

    BlockPos getPatternPosition();

    Collection<? extends MultiBlockPattern> getPatterns();
}
