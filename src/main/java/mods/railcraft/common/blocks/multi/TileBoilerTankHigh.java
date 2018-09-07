/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.gui.EnumGui;
import org.jetbrains.annotations.NotNull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileBoilerTankHigh<M extends TileBoilerFirebox<M>> extends TileBoilerTank<TileBoilerTankHigh<M>, M> {

    public TileBoilerTankHigh() {
        super();
    }

    @NotNull
    @Override
    public EnumGui getGui() {
        return EnumGui.TANK;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<TileBoilerTankHigh<M>> defineSelfClass() {
        return (Class<TileBoilerTankHigh<M>>) (Class<?>) TileBoilerTankHigh.class;
    }
}
