package tests;

import mods.railcraft.common.core.Remapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by CovertJaguar on 9/3/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RemapperTest {
    @Test
    void remap() {
        Assertions.assertEquals("abyssal", Remapper.regex("brick_abyssal").get());
        Assertions.assertEquals("abyssal", Remapper.regex("abyssal_brick").get());
    }
}
