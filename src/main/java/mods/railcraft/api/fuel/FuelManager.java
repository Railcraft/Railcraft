/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.fuel;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fluids.Fluid;
import org.apache.logging.log4j.Level;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FuelManager {

    public static final Map<Fluid, Integer> boilerFuel = new HashMap<Fluid, Integer>();

    /**
     * Register the amount of heat in a bucket of liquid fuel.
     *
     * @param fluid
     * @param heatValuePerBucket
     */
    public static void addBoilerFuel(Fluid fluid, int heatValuePerBucket) {
        ModContainer mod = Loader.instance().activeModContainer();
        String modName = mod != null ? mod.getName() : "An Unknown Mod";
        if (fluid == null) {
            FMLLog.log("Railcraft", Level.WARN, String.format("An error occured while %s was registering a Boiler fuel source", modName));
            return;
        }
        boilerFuel.put(fluid, heatValuePerBucket);
        FMLLog.log("Railcraft", Level.DEBUG, String.format("%s registered \"%s\" as a valid Boiler fuel source with %d heat.", modName, fluid.getName(), heatValuePerBucket));
    }

    public static int getBoilerFuelValue(Fluid fluid) {
        Integer value = boilerFuel.get(fluid);
        if(value != null) return value.intValue();
        else return 0;
    }

}
