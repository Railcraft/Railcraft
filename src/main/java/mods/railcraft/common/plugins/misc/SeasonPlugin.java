/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.misc;

import mods.railcraft.common.carts.IRailcraftCart;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleSeasonal;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.item.EntityMinecart;

import java.util.Calendar;

/**
 * Created by CovertJaguar on 10/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class SeasonPlugin {
    public static final boolean HARVEST;
    public static final boolean HALLOWEEN;
    public static final boolean CHRISTMAS;
    public static final String GHOST_TRAIN = "Ghost Train";
    public static final String POLAR_EXPRESS = "Polar Express";

    static {
        if (RailcraftModuleManager.isModuleEnabled(ModuleSeasonal.class)) {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH);
            HARVEST = month == Calendar.OCTOBER || month == Calendar.NOVEMBER;

            int day = cal.get(Calendar.DAY_OF_MONTH);
            HALLOWEEN = (month == Calendar.OCTOBER && day >= 21) || (month == Calendar.NOVEMBER && day <= 10);

            CHRISTMAS = month == Calendar.DECEMBER || month == Calendar.JANUARY;
        } else {
            HARVEST = false;
            HALLOWEEN = false;
            CHRISTMAS = false;
        }
    }

    public static boolean isGhostTrain(EntityMinecart cart) {
        Season season = cart instanceof IRailcraftCart ? ((IRailcraftCart) cart).getSeason() : Season.DEFAULT;
        if (season == Season.DEFAULT)
            return (RailcraftConfig.isGhostTrainEnabled() && HALLOWEEN)
                    || cart.hasCustomName() && GHOST_TRAIN.equals(cart.getCustomNameTag());
        return season == Season.HALLOWEEN;
    }

    public static boolean isPolarExpress(EntityMinecart cart) {
        Season season = cart instanceof IRailcraftCart ? ((IRailcraftCart) cart).getSeason() : Season.DEFAULT;
        if (season == Season.DEFAULT)
            return (RailcraftConfig.isPolarExpressEnabled() && CHRISTMAS)
                    || cart.hasCustomName() && POLAR_EXPRESS.equals(cart.getCustomNameTag())
                    || cart.world.canSnowAt(cart.getPosition(), false);
        return season == Season.CHRISTMAS;
    }

    public enum Season {

        DEFAULT("gui.railcraft.season.default"),
        HALLOWEEN("gui.railcraft.season.halloween"),
        CHRISTMAS("gui.railcraft.season.christmas"),
        NONE("gui.railcraft.season.none");
        public static final Season[] VALUES = values();
        private final String locTag;

        Season(String locTag) {
            this.locTag = locTag;
        }

        @Override
        public String toString() {
            return LocalizationPlugin.translate(locTag);
        }

    }
}
