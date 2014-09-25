/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import mods.railcraft.common.blocks.ore.EnumOre;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class PoorGoldGenerator extends PoorOreGenerator {

    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "RC_POOR_GOLD", new Class[0], new Object[0]);
    private static final int Y_LEVEL = 15;
    private static final int Y_RANGE = 1;
    private static final int DENSITY = 1;
    private static final int SEED = 79;

    public PoorGoldGenerator() {
        super(EVENT_TYPE, EnumOre.POOR_GOLD, DENSITY, Y_LEVEL, Y_RANGE, SEED);
    }

}
