/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.multi;

import static mods.railcraft.common.blocks.multi.TileTankSteelWall.STEEL_TANK;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileTankSteelGauge<M extends TileTankBase<M, M>> extends TileTankIronGauge<TileTankSteelGauge<M>, M> {

    @Override
    @SuppressWarnings("unchecked")
    protected Class<TileTankSteelGauge<M>> defineSelfClass() {
        return (Class<TileTankSteelGauge<M>>) (Class<?>) TileTankSteelGauge.class;
    }

    @Override
    public MetalTank getTankType() {
        return STEEL_TANK;
    }

    @Override
    public int getCapacityPerBlock() {
        return CAPACITY_PER_BLOCK_STEEL;
    }
}
