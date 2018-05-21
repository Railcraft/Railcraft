/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.multi;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MultiBlockPattern {

    public final char[][][] pattern;
    private final BlockPos masterOffset;
    @Nullable
    private final AxisAlignedBB entityCheckBounds;

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
            return 'O';
        if (x >= getPatternWidthX() || y >= getPatternHeight() || z >= getPatternWidthZ())
            return 'O';
        return getPatternMarker(x, y, z);
    }

    public char getPatternMarker(int x, int y, int z) {
        return pattern[y][x][z];
    }

    public Vec3i getMasterOffset() {
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

    @Nullable
    public TileEntity placeStructure(World world, BlockPos pos, Map<Character, IBlockState> blockMapping) {
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
                        master = world.getTileEntity(pos);
                }
            }
        }
        return master;
    }
}
