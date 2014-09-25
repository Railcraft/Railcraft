/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MultiBlockPattern {

    public final char[][][] pattern;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;
    private final AxisAlignedBB entityCheckBounds;

    public MultiBlockPattern(char[][][] pattern) {
        this(pattern, 1, 1, 1);
    }

    public MultiBlockPattern(char[][][] pattern, int offsetX, int offsetY, int offsetZ) {
        this(pattern, offsetX, offsetY, offsetZ, null);
    }

    public MultiBlockPattern(char[][][] pattern, int offsetX, int offsetY, int offsetZ, AxisAlignedBB entityCheckBounds) {
        this.pattern = pattern;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.entityCheckBounds = entityCheckBounds;
    }

    public AxisAlignedBB getEntityCheckBounds(int masterX, int masterY, int masterZ) {
        if (entityCheckBounds == null)
            return null;
        return entityCheckBounds.copy().offset(masterX, masterY, masterZ);
    }

    public char getPatternMarkerChecked(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0)
            return 'O';
        if (x >= getPatternWidthX() || y >= getPatternHeight() || z >= getPatternWidthZ())
            return 'O';
        return getPatternMarker(x, y, z);
    }

    public char getPatternMarker(int x, int y, int z) {
        return pattern[y][x][z];
    }

    public int getMasterOffsetX() {
        return offsetX;
    }

    public int getMasterOffsetY() {
        return offsetY;
    }

    public int getMasterOffsetZ() {
        return offsetZ;
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

    public int getMasterRelativeX(int posX, int patternX) {
        return (offsetX - patternX) + posX;
    }

    public int getMasterRelativeY(int posY, int patternY) {
        return (offsetY - patternY) + posY;
    }

    public int getMasterRelativeZ(int posZ, int patternZ) {
        return (offsetZ - patternZ) + posZ;
    }

    public TileEntity placeStructure(World world, int xCoord, int yCoord, int zCoord, Block block, Map<Character, Integer> blockMapping) {
        if (block == null)
            return null;

        int xWidth = getPatternWidthX();
        int zWidth = getPatternWidthZ();
        int height = getPatternHeight();

        int xOffset = xCoord - getMasterOffsetX();
        int yOffset = yCoord - getMasterOffsetY();
        int zOffset = zCoord - getMasterOffsetZ();

        TileEntity master = null;

        for (byte px = 0; px < xWidth; px++) {
            for (byte py = 0; py < height; py++) {
                for (byte pz = 0; pz < zWidth; pz++) {

                    char marker = getPatternMarker(px, py, pz);

                    Integer metadata = blockMapping.get(marker);
                    if (metadata == null)
                        continue;

                    int x = px + xOffset;
                    int y = py + yOffset;
                    int z = pz + zOffset;

                    world.setBlock(x, y, z, block, metadata, 3);

                    if (px == getMasterOffsetX() && py == getMasterOffsetY() && pz == getMasterOffsetZ())
                        master = world.getTileEntity(x, y, z);
                }
            }
        }
        return master;
    }

}
