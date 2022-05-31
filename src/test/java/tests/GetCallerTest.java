package tests;

import mods.railcraft.common.util.misc.Code;
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
class GetCallerTest {

    @Test
    void getCallerClass() {
        for (int ii = 0; ii < 5; ii++) {
            LogManager.getLogger("ReflectionTest").log(Level.INFO,
                    new MessageFormatMessage("{0}", Code.getCallerClass(ii)));
        }
        Assertions.assertEquals(GetCallerTest.class, Code.getCallerClass(0));
    }
}
