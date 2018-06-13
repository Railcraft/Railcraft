package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.multi.TileMultiBlock.MultiBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @param <T> Common type
 * @param <M> Master type
 */
public interface IMultiBlockTile<T extends IMultiBlockTile<?, ?>, M extends T> {

    default TileEntity tile() {
        return (TileEntity) this;
    }

    Class<M> getMasterType();

    Class<T> getCommonType();

    boolean isStructureValid();

    boolean isMaster();

    @Nullable
    M getMasterBlock();

    MultiBlockPattern getCurrentPattern();

    MultiBlockState getState();

    BlockPos getPatternPosition();

    Collection<MultiBlockPattern> getPatterns();
}
