package tests;

import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.MessageFormatMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by CovertJaguar on 11/7/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
class HumanReadableNumberFormatterTest {

    @Test
    void formatNumber() {
        double[] test = {15e15, 98e18, 15e14, 105e14, 1400.3, 54983.6768, -34.56, -50_000, -100_000, 140_500};
        String[] result = {"15Q", "98,000Q", "1,500T", "10.5Q", "1,400.3", "55K", "-34.56", "-50K", "-100K", "140.5K"};
        for (int i = 0; i < test.length; i++) {
            String output = HumanReadableNumberFormatter.format(test[i]);
            LogManager.getLogger("NumberFormatter").log(Level.INFO,
                    new MessageFormatMessage("{0} == {1}", test[i], output));
            Assertions.assertEquals(result[i], output);
        }
    }
}
