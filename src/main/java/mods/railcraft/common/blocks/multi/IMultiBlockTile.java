package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.multi.TileMultiBlock.MultiBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;
import java.util.Collection;

/**
 * @param <T> Common type
 * @param <M> Master type
 */
public interface IMultiBlockTile<L extends IMultiBlockTile<L, ? extends L, M>, T extends IMultiBlockTile<L, T, M>, M extends IMultiBlockTile<L, M, M>> {

    default TileEntity tile() {
        return (TileEntity) this;
    }

    Class<L> getLeastCommonType();

    Class<M> getMasterType();

    Class<T> getSelfType();

    boolean isStructureValid();

    boolean isMaster();

    @Nullable
    M getMasterBlock();

    MultiBlockPattern getCurrentPattern();

    MultiBlockState getState();

    BlockPos getPatternPosition();

    Collection<MultiBlockPattern> getPatterns();
}
