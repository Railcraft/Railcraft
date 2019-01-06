/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.buildcraft;

import buildcraft.api.statements.StatementManager;
import mods.railcraft.common.plugins.buildcraft.actions.ActionProvider;
import mods.railcraft.common.plugins.buildcraft.triggers.TriggerProvider;
import mods.railcraft.common.util.misc.Game;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BCAPIWrapper {
    public static void init() {
        try {
            StatementManager.registerTriggerProvider(new TriggerProvider());
            StatementManager.registerActionProvider(new ActionProvider());
        } catch (Throwable error) {
            Game.log().api("Buildcraft", error, StatementManager.class);
        }
    }

}
