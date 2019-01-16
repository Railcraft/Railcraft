/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class NoiseGen {

    public static final int OFFSET_RANGE = 50000;
    protected final int xOffset, yOffset, zOffset;
    protected final double scale;

    /**
     * @param scale applied to the input values, values less than one magnify the noise map, values greater than one shrink it
     */
    protected NoiseGen(Random rand, double scale) {
        this.scale = scale;
        xOffset = rand.nextInt(OFFSET_RANGE) - (OFFSET_RANGE / 2);
        yOffset = rand.nextInt(OFFSET_RANGE) - (OFFSET_RANGE / 2);
        zOffset = rand.nextInt(OFFSET_RANGE) - (OFFSET_RANGE / 2);
    }

    public abstract double noise(double x, double z);

    public abstract double noise(double x, double y, double z);

    public boolean isLessThan(double x, double z, double level) {
        return noise(x, z) < level;
    }

    public boolean isGreaterThan(double x, double z, double level) {
        return noise(x, z) > level;
    }

    public boolean isGreaterThan(double x, double y, double z, double level) {
        return noise(x, y, z) > level;
    }

    public static class NoiseGenSimplex extends NoiseGen {

        public NoiseGenSimplex(Random rand, double scale) {
            super(rand, scale);
        }

        @Override
        public double noise(double x, double z) {
            return SimplexNoise.noise((x + xOffset) * scale, (z + zOffset) * scale);
        }

        @Override
        public double noise(double x, double y, double z) {
            return SimplexNoise.noise((x + xOffset) * scale, (y + yOffset) * scale, (z + zOffset) * scale);
        }

    }

}
