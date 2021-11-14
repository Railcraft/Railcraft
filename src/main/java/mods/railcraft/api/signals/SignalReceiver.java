/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */
package mods.railcraft.api.signals;

import mods.railcraft.api.core.WorldCoordinate;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class SignalReceiver extends AbstractPair {
    private boolean needsInit = true;

    public SignalReceiver(String locTag, TileEntity tile, int maxPairings) {
        super(locTag, tile, maxPairings);
    }

    public SignalController getControllerAt(WorldCoordinate coord) {
        TileEntity con = getPairAt(coord);
        if (con != null) {
            return ((IControllerTile) con).getController();
        }
        return null;
    }

    @Override
    public void informPairsOfNameChange() {
        for(WorldCoordinate coord : getPairs()) {
            SignalController ctrl = getControllerAt(coord);
            if(ctrl != null){
                ctrl.onPairNameChange(getCoords(), getName());
            }
        }
    }

    @Override
    protected String getTagName() {
        return "receiver";
    }

    @Override
    public boolean isValidPair(WorldCoordinate otherCoord, TileEntity otherTile) {
        if (otherTile instanceof IControllerTile) {
            SignalController controller = ((IControllerTile) otherTile).getController();
            return controller.isPairedWith(getCoords());
        }
        return false;
    }

    public void onControllerAspectChange(SignalController con, @Nonnull SignalAspect aspect) {
        ((IReceiverTile) tile).onControllerAspectChange(con, aspect);
    }

    protected void registerController(SignalController controller) {
        addPairing(controller.getCoords());
    }

    @Override
    public void tickServer() {
        super.tickServer();
        if (needsInit) {
            needsInit = false;
            for (WorldCoordinate pair : getPairs()) {
                SignalController controller = getControllerAt(pair);
                if (controller != null) {
                    onControllerAspectChange(controller, controller.getAspectFor(getCoords()));
                }
            }
        }
    }
}
