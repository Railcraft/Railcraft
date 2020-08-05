/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

/**
 *
 */
final class BoilerData {

    static final BoilerData EMPTY = new BoilerData(0, 0, 0f, 0);

    final int numTanks;
    final int ticksPerCycle;
    final float maxHeat;
    final int steamCapacity;

    BoilerData(int tanks, int ticks, float heat, int capacity) {
        this.numTanks = tanks;
        this.ticksPerCycle = ticks;
        this.maxHeat = heat;
        this.steamCapacity = capacity;
    }
}
