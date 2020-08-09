/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 1/28/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FluidPushLogic extends Logic {
    private final int tankIndex;
    private final int outputRate;
    private final EnumFacing[] outputFaces;
    private final Predicate<? super TileEntity> validTargets;

    @SuppressWarnings("unchecked")
    public static Predicate<? super TileEntity> defaultTargets(Adapter adapter) {
        return (Predicate<? super TileEntity>) adapter.tile()
                .map(Object::getClass)
                .map(Predicates::notInstanceOf)
                .orElse(Predicates.alwaysTrue());
    }

    public FluidPushLogic(Adapter adapter, int tankIndex, int outputRate, EnumFacing... outputFaces) {
        this(adapter, tankIndex, outputRate, defaultTargets(adapter), outputFaces);
    }

    public FluidPushLogic(Adapter adapter, int tankIndex, int outputRate, Predicate<? super TileEntity> validTargets, EnumFacing... outputFaces) {
        super(adapter);
        this.tankIndex = tankIndex;
        this.outputRate = outputRate;
        this.outputFaces = outputFaces;
        this.validTargets = validTargets;
    }

    @Override
    protected void updateServer() {
        super.updateServer();
        adapter.tile().ifPresent(tile -> getLogic(FluidLogic.class).ifPresent(tank -> {
            TankManager tMan = tank.getTankManager();
            if (!tMan.isEmpty()) {
                tMan.push(tile.getTileCache(), validTargets, outputFaces, tankIndex, outputRate);
            }
        }));
    }
}
