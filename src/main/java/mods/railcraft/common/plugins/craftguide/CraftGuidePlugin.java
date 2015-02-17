/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.craftguide;

import cpw.mods.fml.common.Loader;
import java.lang.reflect.Method;
import org.apache.logging.log4j.Level;
import mods.railcraft.common.util.misc.Game;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CraftGuidePlugin {

    private static Method register;

    public static void init() {
        if (Loader.isModLoaded("craftguide")) {
            try {
                registerCraftGuideObject(new BlastFurnacePlugin());
                registerCraftGuideObject(new CokeOvenPlugin());
                registerCraftGuideObject(new RockCrusherPlugin());
                registerCraftGuideObject(new RollingMachinePlugin());
                registerCraftGuideObject(new CustomRecipesPlugin());
                registerCraftGuideObject(new RecipeFilter());
            } catch (Throwable error) {
                Game.log(Level.WARN, "Could not register CraftGuide plugins: {0}", error.getMessage());
            }
        } else {
            Game.log(Level.WARN, "Could not register CraftGuide plugins: CraftGuide missing.");
        }
    }

    private static void registerCraftGuideObject(Object obj) {
        try {
            if (register == null) {
                Class api = Class.forName("uristqwerty.CraftGuide.ReflectionAPI");
                register = api.getMethod("registerAPIObject", Object.class);
            }
            register.invoke(null, obj);
            Game.log(Level.DEBUG, "Successfully registered CraftGuide plugin: {0}", obj.getClass().getSimpleName());
        } catch (Exception e) {
            Game.log(Level.WARN, "Could not register CraftGuide plugin: {0}", obj.getClass().getSimpleName());
        }
    }

}
