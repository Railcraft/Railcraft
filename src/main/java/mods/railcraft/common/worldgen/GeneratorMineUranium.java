/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.common.items.Metal;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GeneratorMineUranium extends GeneratorMine {

    private static final int Y_LEVEL = 30;
    private static final int Y_RANGE = 1;
    private static final int DENSITY = 111;
    private static final int SEED = 51;

    public GeneratorMineUranium() {
        super(EventType.CUSTOM, Metal.URANIUM, DENSITY, Y_LEVEL, Y_RANGE, SEED);
    }

}
