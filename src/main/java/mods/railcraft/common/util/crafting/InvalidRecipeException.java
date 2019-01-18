/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFormatMessage;

/**
 * Created by CovertJaguar on 4/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InvalidRecipeException extends Exception {
    private final Message message;

    public InvalidRecipeException(final String messagePattern, final Object... arguments) {
        super(new MessageFormatMessage(messagePattern, arguments).getFormattedMessage());
        message = new MessageFormatMessage(messagePattern, arguments);
    }

    public Message getRawMessage() {
        return message;
    }
}
