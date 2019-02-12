/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import com.google.common.base.Splitter;
import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * Handles inter-mod communication (IMC) messages.
 */
@FunctionalInterface
public interface IInterModMessageHandler {

    /**
     * A splitter to split string messages.
     */
    Splitter SPLITTER = Splitter.on("@").trimResults();

    /**
     * Interpret an IMC message.
     *
     * @param message the IMC message to interpret
     * @throws RuntimeException when a fatal problem occurs
     */
    void handle(FMLInterModComms.IMCMessage message);
}
