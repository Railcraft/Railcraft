/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.commands;

import net.minecraft.command.CommandException;

/**
 * Created by CovertJaguar on 6/4/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockNotFoundException extends CommandException {
    public BlockNotFoundException() {
        this("command.railcraft.block.notFound");
    }

    public BlockNotFoundException(String message, Object... objects) {
        super(message, objects);
    }
}
