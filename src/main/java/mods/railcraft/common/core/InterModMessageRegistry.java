/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.common.util.misc.Game;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;

public final class InterModMessageRegistry {

    // convert to something contained in mod object/module manager at some point
    private static final InterModMessageRegistry INSTANCE = new InterModMessageRegistry();
    private final Map<String, IInterModMessageHandler> handlers = new HashMap<>();
    private boolean called; // may be called multiple times

    public static InterModMessageRegistry getInstance() {
        return INSTANCE;
    }

    public void register(String key, IInterModMessageHandler handler) {
        if (called) {
            Game.log().msg(Level.ERROR, "IMC event has passed when a handler is registered for key {0}", key);
            if (Game.DEVELOPMENT_VERSION)
                throw new IllegalStateException("IMC registration is too late!");
        }
        IInterModMessageHandler old = handlers.put(key, handler);
        if (old != null) {
            Game.log().msg(Level.INFO, "Overridden previous IMC handler for key {0}", key);
        }
    }

    public void handle(FMLInterModComms.IMCMessage message) {
        called = true;
        IInterModMessageHandler handler = handlers.get(message.key);
        if (handler == null) {
            // Some handlers are not registered when some parts of Railcraft is disabled
            Game.log().msg(Level.DEBUG, "Mod {0} sent an ignored IMC message with key {1}", message.getSender(), message.key);
            return;
        }
        handler.handle(message);
    }

    private InterModMessageRegistry() {}
}
