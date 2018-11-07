package tests;

import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.MessageFormatMessage;

/**
 * Created by CovertJaguar on 11/7/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class HumanReadableNumberFormatterTest {

    public static void main(String[] args) {
        double[] test = {15e15, 98e18, 15e14, 105e14, 1400.3, 54983.6768, -34.56, -50_000, -100_000, 140_500};
        for (double n : test)
            LogManager.getLogger("Test").log(Level.INFO, new MessageFormatMessage("Formatter Test: {0}->{1}", n, HumanReadableNumberFormatter.format(n)));
    }
}
