package tests;

import mods.railcraft.common.plugins.forge.ChatPlugin;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by CovertJaguar on 9/3/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TranslationTest {
    @Test
    void translate() {
        ITextComponent textComponent = ChatPlugin.translateMessage("%s bought a one-way ticket to the afterlife", "CovertJaguar");
        LogManager.getLogger("NumberFormatter").log(Level.INFO, textComponent.toString());
        LogManager.getLogger("NumberFormatter").log(Level.INFO, textComponent.getFormattedText());
        Assertions.assertEquals("CovertJaguar bought a one-way ticket to the afterlife", textComponent.getFormattedText().replace("§r", ""));
    }
}
