/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.misc;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleSeasonal;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.entity.item.EntityMinecart;

import java.util.Calendar;

/**
 * Created by CovertJaguar on 10/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SeasonPlugin {
    public static final boolean HARVEST;
    public static final boolean HALLOWEEN;
    public static final boolean HOLIDAYS;
    public static final String GHOST_TRAIN = "Ghost Train";

    static {
        if (RailcraftModuleManager.isModuleEnabled(ModuleSeasonal.class)) {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH);
            HARVEST = month == Calendar.OCTOBER || month == Calendar.NOVEMBER;

            int day = cal.get(Calendar.DAY_OF_MONTH);
            HALLOWEEN = (month == Calendar.OCTOBER && day >= 21) || (month == Calendar.NOVEMBER && day <= 10);

            HOLIDAYS = month == Calendar.DECEMBER || month == Calendar.JANUARY;
        } else {
            HARVEST = false;
            HALLOWEEN = false;
            HOLIDAYS = false;
        }
    }

    public static boolean isGhostTrain(EntityMinecart cart) {
        return RailcraftConfig.isGhostTrainEnabled() && (cart.hasCustomName() && GHOST_TRAIN.equals(cart.getCustomNameTag()) || HALLOWEEN);
    }
}
