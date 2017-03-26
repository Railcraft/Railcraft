/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by CovertJaguar on 7/31/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class HumanReadableNumberFormatter {
    private static final DecimalFormat smallNumberFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
    private static final DecimalFormat largeNumberFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
    private static String[] suffix = {"", "K", "M", "B", "T"};
//    private static double[] test = {1400.3, 54983.6768, -34.56, -50_000, -100_000, 140_500};

    static {
        smallNumberFormatter.applyPattern("#,##0.###");
        largeNumberFormatter.applyPattern("#,##0.#");
//        for (double n : test)
//            Game.log(Level.INFO, "Formatter Test: {0}->{1}", n, format(n));
    }

    public static String format(double number) {
        return format(number, 0);
    }

    private static String format(double number, int iteration) {
        boolean negative = number < 0.0;
        number = Math.abs(number);
        if (number < 10_000.0) {
            DecimalFormat formatter = iteration > 0 ? largeNumberFormatter : smallNumberFormatter;
            return (negative ? "-" : "") + formatter.format(number) + suffix[iteration];
        }
        return (negative ? "-" : "") + format(number / 1000.0, iteration + 1);
    }
}
