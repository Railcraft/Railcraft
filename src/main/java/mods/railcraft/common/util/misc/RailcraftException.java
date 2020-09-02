/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import org.apache.logging.log4j.message.MessageFormatMessage;

/**
 * Created by CovertJaguar on 9/2/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RailcraftException extends Exception {

    public RailcraftException(final String messagePattern, final Object... arguments) {
        super(new MessageFormatMessage(messagePattern, arguments).getFormattedMessage());
    }
}
