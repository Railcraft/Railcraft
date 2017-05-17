package mods.railcraft.common.worldgen;

import mods.railcraft.common.items.Metal;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;

/**
 * Created by GeneralCamo on 5/16/2017.
 *
 * @author GeneralCamo
 *         Created for Railcraft <http://www.railcraft.info>
 */
public class GeneratorMineZinc extends GeneratorMine {

    private static final int Y_LEVEL = 40;
    private static final int Y_RANGE = 3;
    private static final int DENSITY = 4;
    private static final int SEED = 30;

    public GeneratorMineZinc() {
        super(EventType.CUSTOM, Metal.ZINC, DENSITY, Y_LEVEL, Y_RANGE, SEED);
    }

}
