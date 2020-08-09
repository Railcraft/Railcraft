/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class StructurePattern {

    public static final char EMPTY_MARKER = 'O';
    private final char[][][] pattern;
    private final BlockPos masterOffset;
    private final @Nullable AxisAlignedBB entityCheckBounds;
    private final Object[] attachedData;

    /**
     * Creates a multiblock pattern builder.
     *
     * @return The created builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public StructurePattern(char[][][] pattern) {
        this(pattern, 1, 1, 1);
    }

    public StructurePattern(char[][][] pattern, Object... attachedData) {
        this(pattern, new BlockPos(1, 1, 1), null, attachedData);
    }

    public StructurePattern(char[][][] pattern, int offsetX, int offsetY, int offsetZ) {
        this(pattern, offsetX, offsetY, offsetZ, null);
    }

    public StructurePattern(char[][][] pattern, int offsetX, int offsetY, int offsetZ, @Nullable AxisAlignedBB entityCheckBounds) {
        this(pattern, new BlockPos(offsetX, offsetY, offsetZ), entityCheckBounds);
    }

    StructurePattern(char[][][] pattern, BlockPos offset, @Nullable AxisAlignedBB entityCheckBounds, Object... attachedData) {
        this.pattern = pattern;
        this.masterOffset = offset;
        this.entityCheckBounds = entityCheckBounds;
        this.attachedData = attachedData;
    }

    public @Nullable AxisAlignedBB getEntityCheckBounds(BlockPos masterPos) {
        if (entityCheckBounds == null)
            return null;
        return entityCheckBounds.offset(masterPos.getX(), masterPos.getY(), masterPos.getZ());
    }

    @Deprecated
    public char getPatternMarkerChecked(BlockPos posInPattern) {
        int x = posInPattern.getX();
        int y = posInPattern.getY();
        int z = posInPattern.getZ();
        if (x < 0 || y < 0 || z < 0)
            return EMPTY_MARKER;
        if (x >= getPatternWidthX() || y >= getPatternHeight() || z >= getPatternWidthZ())
            return EMPTY_MARKER;
        return getPatternMarker(posInPattern);
    }

    public char getPatternMarker(BlockPos posInPattern) {
        return getPatternMarker(posInPattern.getX(), posInPattern.getY(), posInPattern.getZ());
    }

    public char getPatternMarker(int x, int y, int z) {
        return pattern[y][x][z];
    }

    public char getPatternMarker(Vec3i vec) {
        return pattern[vec.getY()][vec.getX()][vec.getZ()];
    }

    public BlockPos getMasterOffset() {
        return masterOffset;
    }

    public int getPatternHeight() {
        return pattern.length;
    }

    public int getPatternWidthX() {
        return pattern[0].length;
    }

    public int getPatternWidthZ() {
        return pattern[0][0].length;
    }

    public int getPatternSize() {
        return getPatternHeight() * getPatternWidthX() * getPatternWidthZ();
    }

    public BlockPos getMasterPosition(BlockPos myPos, BlockPos posInPattern) {
        return masterOffset.subtract(posInPattern).add(myPos);
    }

    public boolean isMasterPosition(BlockPos posInPattern) {
        return masterOffset.equals(posInPattern);
    }

    public Object[] getAttachedData() {
        return attachedData;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttachedData(int index) {
        return (T) attachedData[index];
    }

    // Why is this needed??
    @Contract("_, !null -> !null")
    @SuppressWarnings("unchecked")
    public @Nullable <T> T getAttachedDataOr(int index, @Nullable T backup) {
        return attachedData.length <= index ? backup : (T) attachedData[index];
    }

    @Deprecated
    public State testPattern(TileMultiBlock tile) {
        int xWidth = getPatternWidthX();
        int zWidth = getPatternWidthZ();
        int height = getPatternHeight();

        BlockPos offset = tile.getPos().subtract(getMasterOffset());

        BlockPos.PooledMutableBlockPos now = BlockPos.PooledMutableBlockPos.retain();
        for (int patX = 0; patX < xWidth; patX++) {
            for (int patY = 0; patY < height; patY++) {
                for (int patZ = 0; patZ < zWidth; patZ++) {
                    int x = patX + offset.getX();
                    int y = patY + offset.getY();
                    int z = patZ + offset.getZ();
                    now.setPos(x, y, z);
                    if (!tile.getWorld().isBlockLoaded(now))
                        return State.NOT_LOADED;
                    if (!tile.isMapPositionValid(now, getPatternMarker(patX, patY, patZ)))
                        return State.PATTERN_DOES_NOT_MATCH;
                }
            }
        }
        now.release();

        AxisAlignedBB entityCheckBounds = getEntityCheckBounds(tile.getPos());
//                if(entityCheckBounds != null) {
//                    System.out.println("test entities: " + entityCheckBounds.toString());
//                }
        if (entityCheckBounds != null && !tile.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, entityCheckBounds).isEmpty())
            return State.ENTITY_IN_WAY;
        return State.VALID;
    }

    public State testPattern(StructureLogic logic) {
        int xWidth = getPatternWidthX();
        int zWidth = getPatternWidthZ();
        int height = getPatternHeight();

        BlockPos offset = logic.getPos().subtract(getMasterOffset());

        BlockPos.PooledMutableBlockPos now = BlockPos.PooledMutableBlockPos.retain();
        for (int patX = 0; patX < xWidth; patX++) {
            for (int patY = 0; patY < height; patY++) {
                for (int patZ = 0; patZ < zWidth; patZ++) {
                    int x = patX + offset.getX();
                    int y = patY + offset.getY();
                    int z = patZ + offset.getZ();
                    now.setPos(x, y, z);
                    if (!logic.theWorldAsserted().isBlockLoaded(now))
                        return State.NOT_LOADED;
                    if (!logic.isMapPositionValid(now, getPatternMarker(patX, patY, patZ)))
                        return State.PATTERN_DOES_NOT_MATCH;
                }
            }
        }
        now.release();

        AxisAlignedBB entityCheckBounds = getEntityCheckBounds(logic.getPos());
//                if(entityCheckBounds != null) {
//                    System.out.println("test entities: " + entityCheckBounds.toString());
//                }
        if (entityCheckBounds != null && !logic.theWorldAsserted().getEntitiesWithinAABB(EntityLivingBase.class, entityCheckBounds).isEmpty())
            return State.ENTITY_IN_WAY;
        return State.VALID;
    }

    public Optional<TileLogic> placeStructure(World world, BlockPos pos, Char2ObjectMap<IBlockState> blockMapping) {
        int xWidth = getPatternWidthX();
        int zWidth = getPatternWidthZ();
        int height = getPatternHeight();

        BlockPos offset = pos.subtract(getMasterOffset());

        Optional<TileLogic> master = Optional.empty();

        for (byte px = 0; px < xWidth; px++) {
            for (byte py = 0; py < height; py++) {
                for (byte pz = 0; pz < zWidth; pz++) {

                    char marker = getPatternMarker(px, py, pz);

                    IBlockState blockState = blockMapping.get(marker);
                    if (blockState == null)
                        continue;

                    BlockPos p = new BlockPos(px, py, pz).add(offset);
                    world.setBlockState(p, blockState, 3);

                    if (px == masterOffset.getX() && py == masterOffset.getY() && pz == masterOffset.getZ())
                        master = WorldPlugin.getTileEntity(world, pos, TileLogic.class);
                }
            }
        }
        return master;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pattern.length; i++) {
            builder.append("Level ").append(i).append(":\n");
            char[][] level = pattern[i];
            for (char[] line : level) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    public enum State {

        VALID(TileMultiBlock.MultiBlockState.VALID, "railcraft.multiblock.state.valid"),
        // TODO map to untested?
        ENTITY_IN_WAY(TileMultiBlock.MultiBlockState.INVALID, "railcraft.multiblock.state.invalid.entity"),
        PATTERN_DOES_NOT_MATCH(TileMultiBlock.MultiBlockState.INVALID, "railcraft.multiblock.state.invalid.pattern"),
        NOT_LOADED(TileMultiBlock.MultiBlockState.UNKNOWN, "railcraft.multiblock.state.unknown.unloaded");
        public final TileMultiBlock.MultiBlockState type;
        public final String message;

        State(TileMultiBlock.MultiBlockState type, String msg) {
            this.type = type;
            this.message = msg;
        }

    }

    public static final class Builder {

        private int widthX = -1;
        private int widthZ = -1;
        private BlockPos masterOffset = new BlockPos(1, 1, 1);
        private final List<char[][]> levels = new ArrayList<>();
        private @Nullable AxisAlignedBB box;
        private List<Object> attachedData = new ArrayList<>();

        Builder() {
        }

        /**
         * Sets the master position relative to the corner.
         *
         * @param x The x position
         * @param y The y position
         * @param z The z position
         * @return This builder, for chaining
         */
        public Builder master(int x, int y, int z) {
            masterOffset = new BlockPos(x, y, z);
            return this;
        }

        /**
         * Sets the master position relative to the corner.
         *
         * @param pos The position
         * @return This builder, for chaining
         */
        public Builder master(BlockPos pos) {
            masterOffset = pos.toImmutable();
            return this;
        }

        /**
         * Adds a level at top.
         *
         * @param level The pattern on this level
         * @return This builder, for chaining
         */
        public Builder level(char[][] level) {
            checkArgument(level.length > 0, "The level can't be empty");
            checkArgument(level[0].length > 0, "The level can't be empty");
            for (int i = 1; i < level.length; i++) {
                checkArgument(level[i].length == level[0].length, "The level must be a rectangle");
            }
            if (levels.isEmpty()) {
                widthX = level.length;
                widthZ = level[0].length;
            } else {
                checkArgument(level.length == widthX, "A level of different dimension added");
                checkArgument(level[0].length == widthZ, "A level of different dimension added");
            }
            levels.add(level);
            return this;
        }

        /**
         * Sets the entity check bounds.
         *
         * @param box The check bounds
         * @return This builder, for chaining
         */
        public Builder entityCheckBounds(@Nullable AxisAlignedBB box) {
            this.box = box;
            return this;
        }

        /**
         * Sets the attached data of this pattern/
         *
         * @param data The attached data
         * @param <T>  The data type
         * @return This builder, for chaining
         */
        public <T> Builder attachedData(@Nullable T data) {
            attachedData.add(data);
            return this;
        }

        /**
         * Creates a multiblock pattern.
         *
         * @return The created multiblock pattern
         */
        public StructurePattern build() {
            char[][][] chars = levels.toArray(new char[0][][]);
            return new StructurePattern(chars, masterOffset, box, attachedData.toArray());
        }
    }
}
