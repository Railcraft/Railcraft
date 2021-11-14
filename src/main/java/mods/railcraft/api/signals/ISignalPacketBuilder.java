/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */
package mods.railcraft.api.signals;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ISignalPacketBuilder {
    void sendPairPacketUpdate(AbstractPair pairing);
    void sendPairPacketRequest(AbstractPair pairing);
}
