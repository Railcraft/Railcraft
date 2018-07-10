/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.multi;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class MultiBlockPattern {

    public static final char EMPTY_PATTERN = 'O';
    private final char[][][] pattern;
    private final BlockPos masterOffset;
    @Nullable
    private final AxisAlignedBB entityCheckBounds;
    @Nullable
    private final Object attachedData;

    /**
     * Creates a multiblock pattern builder.
     *
     * @return The created builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public MultiBlockPattern(char[][][] pattern) {
        this(pattern, 1, 1, 1);
    }

    public MultiBlockPattern(char[][][] pattern, int offsetX, int offsetY, int offsetZ) {
        this(pattern, offsetX, offsetY, offsetZ, null);
    }

    public MultiBlockPattern(char[][][] pattern, int offsetX, int offsetY, int offsetZ, @Nullable AxisAlignedBB entityCheckBounds) {
        this.pattern = pattern;
        this.masterOffset = new BlockPos(offsetX, offsetY, offsetZ);
        this.entityCheckBounds = entityCheckBounds;
        this.attachedData = null;
    }

    MultiBlockPattern(char[][][] pattern, BlockPos offset, @Nullable AxisAlignedBB entityCheckBounds, @Nullable Object attachedData) {
        this.pattern = pattern;
        this.masterOffset = offset;
        this.entityCheckBounds = entityCheckBounds;
        this.attachedData = attachedData;
    }

    @Nullable
    public AxisAlignedBB getEntityCheckBounds(BlockPos masterPos) {
        if (entityCheckBounds == null)
            return null;
        return entityCheckBounds.offset(masterPos.getX(), masterPos.getY(), masterPos.getZ());
    }

    public char getPatternMarkerChecked(BlockPos patternPos) {
        int x = patternPos.getX();
        int y = patternPos.getY();
        int z = patternPos.getZ();
        if (x < 0 || y < 0 || z < 0)
            return EMPTY_PATTERN;
        if (x >= getPatternWidthX() || y >= getPatternHeight() || z >= getPatternWidthZ())
            return EMPTY_PATTERN;
        return getPatternMarker(x, y, z);
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

    public BlockPos getMasterPosition(BlockPos myPos, BlockPos posInPattern) {
        return masterOffset.subtract(posInPattern).add(myPos);
    }

    public boolean isMasterPosition(BlockPos posInPattern) {
        return masterOffset.equals(posInPattern);
    }

    @SuppressWarnings("unchecked")
    @Contract("!null -> !null")
    @Nullable
    public <T> T getAttachedData(@Nullable T backup) {
        return attachedData == null ? backup : (T) attachedData;
    }

    @Nullable
    public TileEntity placeStructure(World world, BlockPos pos, Char2ObjectMap<IBlockState> blockMapping) {
        int xWidth = getPatternWidthX();
        int zWidth = getPatternWidthZ();
        int height = getPatternHeight();

        BlockPos offset = pos.subtract(getMasterOffset());

        TileEntity master = null;

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
                        master = WorldPlugin.getBlockTile(world, pos);
                }
            }
        }
        return master;
    }

    public static final class Builder {

        private int widthX = -1;
        private int widthZ = -1;
        private BlockPos masterOffset = new BlockPos(1, 1, 1);
        private List<char[][]> levels = new ArrayList<>();
        @Nullable
        private AxisAlignedBB box;
        @Nullable
        private Object attachedData;

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
         * @param <T> The data type
         * @return This builder, for chaining
         */
        public <T> Builder attachedData(@Nullable T data) {
            this.attachedData = data;
            return this;
        }

        /**
         * Creates a multiblock pattern.
         *
         * @return The created multiblock pattern
         */
        public MultiBlockPattern build() {
            char[][][] chars = levels.toArray(new char[0][][]);
            return new MultiBlockPattern(chars, masterOffset, null, attachedData);
        }
    }
}
