/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import java.util.ArrayList;
import java.util.List;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSawmill extends TileMultiBlock {

    private final static List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();

    static {
        char[][][] map1 = {
            {
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'A', 'A', 'A', 'B', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'B', 'A', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'A', 'A', 'A', 'B', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'B', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'B', 'A', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            }
        };
        patterns.add(new MultiBlockPattern(map1));

        char[][][] map2 = {
            {
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'A', 'B', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'B', 'A', 'A', 'A', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'A', 'B', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'B', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'B', 'A', 'A', 'A', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            }
        };
        patterns.add(new MultiBlockPattern(map2));


        char[][][] map3 = {
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'B', 'A', 'B', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },};
        patterns.add(new MultiBlockPattern(map3));

        char[][][] map4 = {
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'B', 'A', 'B', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'A', 'A', 'A', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },
            {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
            },};
        patterns.add(new MultiBlockPattern(map4));
    }

    public TileSawmill() {
        super(patterns);
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineBeta.METALS_CHEST;
    }
}
