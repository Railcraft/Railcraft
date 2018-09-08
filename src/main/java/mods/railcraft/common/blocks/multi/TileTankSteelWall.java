/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.multi;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileTankSteelWall<M extends TileTankBase<M, M>> extends TileTankIronWall<TileTankSteelWall<M>, M> {

    public static final MetalTank STEEL_TANK = new SteelTank();

    @SuppressWarnings("unchecked")
    @Override
    protected Class<TileTankSteelWall<M>> defineSelfClass() {
        return (Class<TileTankSteelWall<M>>) (Class<?>) TileTankSteelWall.class;
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
